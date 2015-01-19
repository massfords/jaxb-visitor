package com.massfords.jaxb;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTypeVar;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.Set;

import static com.massfords.jaxb.ClassDiscoverer.allConcreteClasses;

/**
 * Creates a no-op implementation of the Visitor interface. After creating the class
 * a visit method is added for each of the beans that were generated.
 * 
 * @author markford
 */
public class CreateBaseVisitorClass extends CodeCreator {

    private JDefinedClass visitor;

    public CreateBaseVisitorClass(JDefinedClass visitor, Outline outline, JPackage jPackage) {
        super(outline, jPackage);
        this.visitor = visitor;
    }
    
    @Override
    protected void run(Set<ClassOutline> classes, Set<JClass> directClasses) {
        JDefinedClass _class = getOutline().getClassFactory().createClass(getPackage(), "BaseVisitor", null);
		setOutput(_class);
        final JTypeVar returnType = _class.generify("R");
        final JTypeVar exceptionType = _class.generify("E", Throwable.class);
		final JClass narrowedVisitor = visitor.narrow(returnType, exceptionType);
        getOutput()._implements(narrowedVisitor);

        for(JClass jc : allConcreteClasses(classes, directClasses)) {
            implementVisitMethod(returnType, exceptionType, jc);
        }
    }

    private void implementVisitMethod(JTypeVar returnType, JTypeVar exceptionType, JClass implClass) {
        JMethod _method = getOutput().method(JMod.PUBLIC, returnType, "visit");
        _method._throws(exceptionType);
        _method.param(implClass, "aBean");
        _method.body()._return(JExpr._null());
        _method.annotate(Override.class);
    }
}

