package com.massfords.jaxb;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.outline.ClassOutline;

import java.util.Set;
import java.util.function.Function;

/**
 * Adds the accept method to the bean.
 * 
 * @author markford
 */
class AddAcceptMethod {

    /**
     * Function that accepts a type name and returns the name of the method to
     * create. This encapsulates the behavior associated with the includeType
     * flag.
     */
    private final Function<String,String> visitMethodNamer;

    AddAcceptMethod(Function<String,String> visitMethodNamer) {
        this.visitMethodNamer = visitMethodNamer;
    }

    public void run(Set<ClassOutline> sorted, JDefinedClass visitor) {
        // skip over abstract classes
// add the accept method to the bean
        sorted.stream().filter(classOutline -> !classOutline.target.isAbstract()).forEach(classOutline -> {
            // add the accept method to the bean
            JDefinedClass beanImpl = classOutline.implClass;
            final JMethod acceptMethod = beanImpl.method(JMod.PUBLIC, void.class, "accept");
            final JTypeVar returnType = acceptMethod.generify("R");
            final JTypeVar exceptionType = acceptMethod.generify("E", Throwable.class);
            acceptMethod.type(returnType);
            acceptMethod._throws(exceptionType);
            final JClass narrowedVisitor = visitor.narrow(returnType, exceptionType);
            JVar vizParam = acceptMethod.param(narrowedVisitor, "aVisitor");
            JBlock block = acceptMethod.body();
            String methodName = visitMethodNamer.apply(beanImpl.name());
            block._return(vizParam.invoke(methodName).arg(JExpr._this()));
        });
    }
}
