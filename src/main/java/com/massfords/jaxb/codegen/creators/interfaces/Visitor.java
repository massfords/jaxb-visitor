package com.massfords.jaxb.codegen.creators.interfaces;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.InitialState;
import com.massfords.jaxb.codegen.creators.Utils;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

import static com.massfords.jaxb.codegen.ClassDiscoverer.allConcreteClasses;
import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;

/**
 * Creates the visitor interface. After creating the interface, a visit method is added for each of the beans.
 *
 * @author markford
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Visitor {
    public static JDefinedClass create(InitialState codeGenState, CodeGenOptions options) {

        final JDefinedClass _interface = codeGenState.getOutline().getClassFactory().createInterface(options.getPackageForVisitor(), "Visitor", null);
        annotateGenerated(_interface, options);

        // define the generics for the visitor
        Utils.GenerifyResults results = Utils.generify(_interface, options);

        allConcreteClasses(codeGenState.getSorted(), codeGenState.getDirectClasses())
                .forEach(jc -> {
                    String visitMethod = options.getVisitMethodNamer().apply(jc.name());
                    JMethod vizMethod = _interface.method(JMod.NONE, results.getReturnType(), visitMethod);
                    vizMethod._throws(results.getExceptionType());
                    vizMethod.param(jc, "aBean");
                    Optional.ofNullable(results.getArgType()).ifPresent(jTypeVar -> vizMethod.param(jTypeVar, "arg"));
                });

        return _interface;
    }
}
