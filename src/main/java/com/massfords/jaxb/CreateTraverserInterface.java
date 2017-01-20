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
import java.util.function.Function;

import static com.massfords.jaxb.ClassDiscoverer.allConcreteClasses;

/**
 * Creates the traverser interface. A traverse method is added for each of the generated beans.
 * 
 * @author markford
 */
class CreateTraverserInterface extends CodeCreator {
    
    private final JDefinedClass visitor;
    /**
     * Function that accepts a type name and returns the name of the method to
     * create. This encapsulates the behavior associated with the includeType
     * flag.
     */
    private final Function<String,String> traverseMethodNamer;

    CreateTraverserInterface(JDefinedClass visitor, Outline outline,
                             JPackage jpackage,
                             Function<String, String> traverseMethodNamer) {
        super(outline, jpackage);
        this.visitor = visitor;
        this.traverseMethodNamer = traverseMethodNamer;
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
        JMethod traverseMethod;
        String methodName = traverseMethodNamer.apply(implClass.name());
        traverseMethod = getOutput().method(JMod.NONE, void.class, methodName);
        traverseMethod._throws(exceptionType);
        traverseMethod.param(implClass, "aBean");
        traverseMethod.param(narrowedVisitor, "aVisitor");
    }
}
