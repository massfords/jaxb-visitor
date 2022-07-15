package com.massfords.jaxb.codegen.creators.decorators;

import com.massfords.jaxb.VisitorPlugin;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.creators.GenerifyResults;
import com.massfords.jaxb.codegen.creators.Utils;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import java.util.Optional;

/**
 * Adds the accept method to the bean.
 *
 * @author markford
 */
public final class AddAcceptMethod {

    private AddAcceptMethod() {
    }

    public static void decorate(VisitorPlugin.VisitorState state, CodeGenOptions options) {
        // skip over abstract classes
        // add the accept method to the bean
        state.initial().allClasses()
                .stream()
                .filter(classOutline -> !classOutline.target.isAbstract())
                .forEach(classOutline -> {
                    // add the accept method to the bean
                    JDefinedClass beanImpl = classOutline.implClass;
                    final JMethod acceptMethod = beanImpl.method(JMod.PUBLIC, void.class, "accept");
                    final GenerifyResults genericTypes = Utils.generify(acceptMethod, options);
                    acceptMethod.type(genericTypes.returnType());
                    acceptMethod._throws(genericTypes.exceptionType());

                    JVar vizParam = acceptMethod.param(state.narrowedVisitor(), "aVisitor");
                    Optional<JVar> argParam = genericTypes.argType()
                            .map(jTypeVar -> acceptMethod.param(jTypeVar, "arg"));
                    JBlock block = acceptMethod.body();
                    String methodName = options.visitMethodNamer().apply(beanImpl.name());
                    JInvocation invocation = vizParam.invoke(methodName).arg(JExpr._this());
                    argParam.ifPresent(invocation::arg);
                    block._return(invocation);
                });
    }
}
