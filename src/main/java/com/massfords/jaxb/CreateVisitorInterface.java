package com.massfords.jaxb;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTypeVar;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.Set;

import static com.massfords.jaxb.ClassDiscoverer.allConcreteClasses;

/**
 * Creates the visitor interface. After creating the interface, a visit method is added for each of the beans.
 * 
 * @author markford
 */
public class CreateVisitorInterface extends CodeCreator {
    
    private boolean includeType;

    public CreateVisitorInterface(Outline outline, JPackage jPackage, boolean includeType) {
        super(outline, jPackage);
        this.includeType = includeType;
    }
    
    @Override
    protected void run(Set<ClassOutline> classes, Set<JClass> directClasses) {
        
        final JDefinedClass _interface = outline.getClassFactory().createInterface(jpackage, "Visitor", null);
        
        final JTypeVar returnType = _interface.generify("R");
        final JTypeVar exceptionType = _interface.generify("E", Throwable.class);

        setOutput( _interface );

        for(JClass jc : allConcreteClasses(classes, directClasses)) {
//            System.out.println("seeing class:" + jc.name());
            declareVisitMethod(returnType, exceptionType, jc);
        }
    }

    private void declareVisitMethod(JTypeVar returnType, JTypeVar exceptionType, JClass implClass) {
        JMethod vizMethod;
        if (includeType)
            vizMethod = getOutput().method(JMod.PUBLIC, returnType, "visit" + implClass.name());
        else
            vizMethod = getOutput().method(JMod.PUBLIC, returnType, "visit");
        vizMethod._throws(exceptionType);
        vizMethod.param(implClass, "aBean");
    }
}
