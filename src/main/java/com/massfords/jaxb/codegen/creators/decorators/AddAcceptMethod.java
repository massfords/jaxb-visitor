package com.massfords.jaxb.codegen.creators.decorators;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.VisitorState;
import com.massfords.jaxb.codegen.creators.Utils;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

/**
 * Adds the accept method to the bean.
 *
 * @author markford
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddAcceptMethod {

    public static void decorate(VisitorState state, CodeGenOptions options) {
        // skip over abstract classes
        // add the accept method to the bean
        state.getSorted()
                .stream()
                .filter(classOutline -> !classOutline.target.isAbstract())
                .forEach(classOutline -> {
                    // add the accept method to the bean
                    JDefinedClass beanImpl = classOutline.implClass;
                    final JMethod acceptMethod = beanImpl.method(JMod.PUBLIC, void.class, "accept");
                    final Utils.GenerifyResults genericTypes = Utils.generify(acceptMethod, options);
                    acceptMethod.type(genericTypes.getReturnType());
                    acceptMethod._throws(genericTypes.getExceptionType());

                    JVar vizParam = acceptMethod.param(state.getNarrowedVisitor(), "aVisitor");
                    Optional<JTypeVar> argType = Optional.ofNullable(genericTypes.getArgType());
                    Optional<JVar> argParam = argType.map(jTypeVar -> acceptMethod.param(jTypeVar, "arg")) ;
                    JBlock block = acceptMethod.body();
                    String methodName = options.getVisitMethodNamer().apply(beanImpl.name());
                    JInvocation invocation = vizParam.invoke(methodName).arg(JExpr._this());
                    argParam.ifPresent(invocation::arg);
                    block._return(invocation);
                });
    }
}
