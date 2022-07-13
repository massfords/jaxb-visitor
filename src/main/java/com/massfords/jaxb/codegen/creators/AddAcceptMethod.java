package com.massfords.jaxb.codegen.creators;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.VisitorCreated;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Adds the accept method to the bean.
 *
 * @author markford
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddAcceptMethod {

    public static void decorate(VisitorCreated state, CodeGenOptions options) {
        // skip over abstract classes
        // add the accept method to the bean
        state.getInitial().getSorted()
                .stream()
                .filter(classOutline -> !classOutline.target.isAbstract())
                .forEach(classOutline -> {
                    // add the accept method to the bean
                    JDefinedClass beanImpl = classOutline.implClass;
                    final JMethod acceptMethod = beanImpl.method(JMod.PUBLIC, void.class, "accept");
                    final JTypeVar returnType = acceptMethod.generify("R");
                    final JTypeVar exceptionType = acceptMethod.generify("E", Throwable.class);
                    acceptMethod.type(returnType);
                    acceptMethod._throws(exceptionType);
                    final JClass narrowedVisitor = state.getVisitor().narrow(returnType, exceptionType);
                    JVar vizParam = acceptMethod.param(narrowedVisitor, "aVisitor");
                    JBlock block = acceptMethod.body();
                    String methodName = options.getVisitMethodNamer().apply(beanImpl.name());
                    block._return(vizParam.invoke(methodName).arg(JExpr._this()));
                });
    }
}
