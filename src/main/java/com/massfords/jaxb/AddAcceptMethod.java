package com.massfords.jaxb;

import com.sun.codemodel.*;
import com.sun.tools.xjc.outline.ClassOutline;

import java.util.Set;

/**
 * Adds the accept method to the bean.
 * 
 * @author markford
 */
public class AddAcceptMethod {

    private boolean includeType;

    public AddAcceptMethod(boolean includeType) {
        this.includeType = includeType;
    }

    public void run(Set<ClassOutline> sorted, JDefinedClass visitor) {
        for (ClassOutline classOutline : sorted) {
            // skip over abstract classes
            if (!classOutline.target.isAbstract()) {
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
                if (includeType)
                    block._return(vizParam.invoke("visit" + beanImpl.name()).arg(JExpr._this()));
                else
                    block._return(vizParam.invoke("visit").arg(JExpr._this()));
            }
        }
    }
}
