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
 * Creates the traverser interface. A traverse method is added for each of the generated beans.
 * 
 * @author markford
 */
public class CreateTraverserInterface extends CodeCreator {
    
    private JDefinedClass visitor;

    public CreateTraverserInterface(JDefinedClass visitor, Outline outline, JPackage jpackage) {
        super(outline, jpackage);
        this.visitor = visitor;
    }

    @Override
    protected void run(Set<ClassOutline> classes, Set<JClass> directClasses) {
        JDefinedClass scratch = getOutline().getClassFactory().createInterface(getPackage(), "_scratch", null);
        try {
            JDefinedClass _interface = getOutline().getClassFactory().createInterface(getPackage(), "Traverser", null);
            setOutput(_interface);
            final JTypeVar retType = scratch.generify("?");
            final JTypeVar exceptionType = _interface.generify("E", Throwable.class);
            final JClass narrowedVisitor = visitor.narrow(retType).narrow(exceptionType);

            for (JClass jc : allConcreteClasses(classes, directClasses)) {
                implTraverse(exceptionType, narrowedVisitor, jc);
            }
        } finally {
            jpackage.remove(scratch);
        }
    }

    private void implTraverse(JTypeVar exceptionType, JClass narrowedVisitor, JClass implClass) {
        JMethod traverseMethod = getOutput().method(JMod.PUBLIC, void.class, "traverse");
        traverseMethod._throws(exceptionType);
        traverseMethod.param(implClass, "aBean");
        traverseMethod.param(narrowedVisitor, "aVisitor");
    }
}
