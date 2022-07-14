package com.massfords.jaxb.codegen.creators.interfaces;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.InitialState;
import com.massfords.jaxb.codegen.creators.GenerifyResults;
import com.massfords.jaxb.codegen.creators.Utils;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import static com.massfords.jaxb.codegen.ClassDiscoverer.allConcreteClasses;
import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;

/**
 * Creates the visitor interface. After creating the interface, a visit method is added for each of the beans.
 *
 * @author markford
 */
public final class Visitor {
    public static JDefinedClass create(InitialState codeGenState, CodeGenOptions options) {

        final JDefinedClass _interface = codeGenState.outline().getClassFactory().createInterface(options.packageForVisitor(), "Visitor", null);
        annotateGenerated(_interface, options);

        // define the generics for the visitor
        GenerifyResults results = Utils.generify(_interface, options);

        allConcreteClasses(codeGenState.allClasses(), codeGenState.directClasses())
                .forEach(jc -> {
                    String visitMethod = options.visitMethodNamer().apply(jc.name());
                    JMethod vizMethod = _interface.method(JMod.NONE, results.returnType(), visitMethod);
                    vizMethod._throws(results.exceptionType());
                    vizMethod.param(jc, "aBean");
                    results.argType().ifPresent(jTypeVar -> vizMethod.param(jTypeVar, "arg"));
                });

        return _interface;
    }
}
