package com.massfords.jaxb.codegen.creators.interfaces;

import com.massfords.jaxb.VisitorPlugin;
import com.massfords.jaxb.codegen.CodeGenOptions;
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
    private Visitor() {
    }
    public static JDefinedClass create(VisitorPlugin.InitialState codeGenState, CodeGenOptions options) {

        final JDefinedClass visitorModel = codeGenState.outline().getClassFactory().createInterface(
                options.packageForVisitor(), "Visitor", null);
        annotateGenerated(visitorModel, options);

        // define the generics for the visitor
        GenerifyResults results = Utils.generify(visitorModel, options);

        allConcreteClasses(codeGenState.allClasses(), codeGenState.directClasses())
                .forEach(jc -> {
                    String visitMethod = options.visitMethodNamer().apply(jc.name());
                    JMethod vizMethod = visitorModel.method(JMod.NONE, results.returnType(), visitMethod);
                    vizMethod._throws(results.exceptionType());
                    vizMethod.param(jc, "aBean");
                    results.argType().ifPresent(jTypeVar -> vizMethod.param(jTypeVar, "arg"));
                });

        return visitorModel;
    }
}
