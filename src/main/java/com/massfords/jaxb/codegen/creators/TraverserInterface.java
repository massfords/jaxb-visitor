package com.massfords.jaxb.codegen.creators;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.VisitorCreated;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTypeVar;
import com.sun.tools.xjc.outline.Outline;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.massfords.jaxb.codegen.ClassDiscoverer.allConcreteClasses;
import static com.massfords.jaxb.codegen.creators.CodeCreator.annotateGenerated;

/**
 * Creates the traverser interface. A traverse method is added for each of the generated beans.
 *
 * @author markford
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraverserInterface {
    public static JDefinedClass createInterface(VisitorCreated state, CodeGenOptions options) {
        Outline outline = state.getInitial().getOutline();
        JPackage jpackage = options.getPackageForVisitor();
        JDefinedClass scratch = outline.getClassFactory().createInterface(jpackage, "_scratch", null);
        try {
            JDefinedClass _interface = outline.getClassFactory().createInterface(jpackage, "Traverser", null);
            annotateGenerated(_interface, options);
            final JTypeVar retType = scratch.generify("?");
            final JTypeVar exceptionType = _interface.generify("E", Throwable.class);
            final JClass narrowedVisitor = state.getVisitor().narrow(retType).narrow(exceptionType);

            allConcreteClasses(state.getInitial().getSorted(), state.getInitial().getDirectClasses())
                    .forEach((jc -> {
                        JMethod traverseMethod;
                        String methodName = options.getTraverseMethodNamer().apply(jc.name());
                        traverseMethod = _interface.method(JMod.NONE, void.class, methodName);
                        traverseMethod._throws(exceptionType);
                        traverseMethod.param(jc, "aBean");
                        traverseMethod.param(narrowedVisitor, "aVisitor");
                    }));
            return _interface;
        } finally {
            options.getPackageForVisitor().remove(scratch);
        }
    }
}
