package com.massfords.jaxb.codegen.creators.interfaces;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.VisitorState;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTypeVar;
import com.sun.tools.xjc.outline.Outline;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.massfords.jaxb.codegen.ClassDiscoverer.allConcreteClasses;
import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;

/**
 * Creates the traverser interface. A traverse method is added for each of the generated beans.
 *
 * @author markford
 */
public final class Traverser {
    private Traverser() {
    }
    public static JDefinedClass createInterface(VisitorState state, CodeGenOptions options) {
        Outline outline = state.initial().outline();
        JPackage jpackage = options.packageForVisitor();
        JDefinedClass scratch = outline.getClassFactory().createInterface(jpackage, "_scratch", null);
        try {
            JDefinedClass traverserModel = outline.getClassFactory()
                    .createInterface(jpackage, "Traverser", null);
            annotateGenerated(traverserModel, options);

            final JTypeVar returnType = scratch.generify("?");
            final JTypeVar exceptionType = traverserModel.generify("E", Throwable.class);
            final JTypeVar argType = options.includeArg() ? traverserModel.generify("A") : null;

            final List<JTypeVar> generics = Stream.of(returnType, exceptionType, argType)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            final JClass narrowedVisitorWithWildcard = state.visitor().narrow(generics);
            traverserModel
                    .narrow(Stream.of(returnType, exceptionType, argType)
                            .filter(Objects::nonNull)
                            .toArray(JTypeVar[]::new));

            allConcreteClasses(state.initial().allClasses(), state.initial().directClasses())
                    .forEach((jc -> {
                        String methodName = options.traverseMethodNamer().apply(jc.name());
                        JMethod traverseMethod = traverserModel.method(JMod.NONE, void.class, methodName);
                        traverseMethod._throws(exceptionType);
                        traverseMethod.param(jc, "aBean");
                        traverseMethod.param(narrowedVisitorWithWildcard, "aVisitor");
                        if (options.includeArg()) {
                            traverseMethod.param(argType, "arg");
                        }
                    }));
            return traverserModel;
        } finally {
            options.packageForVisitor().remove(scratch);
        }
    }
}
