package com.massfords.jaxb.codegen.creators.classes;

import com.massfords.jaxb.codegen.AllInterfacesCreated;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.creators.GenerifyResults;
import com.massfords.jaxb.codegen.creators.Utils;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import static com.massfords.jaxb.codegen.ClassDiscoverer.allConcreteClasses;
import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;

/**
 * Creates a no-op implementation of the Visitor interface. After creating the class
 * a visit method is added for each of the beans that were generated.
 *
 * @author markford
 */
public final class BaseVisitor {
    private BaseVisitor() {
    }

    public static void createClass(AllInterfacesCreated state, CodeGenOptions options) {
        JDefinedClass clazz = state.initial().outline().getClassFactory().createClass(
                options.packageForVisitor(), "BaseVisitor", null);
        annotateGenerated(clazz, options);
        GenerifyResults results = Utils.generify(clazz, options);
        clazz._implements(state.narrowedVisitor());

        allConcreteClasses(state.initial().allClasses(), state.initial().directClasses())
                .forEach(jc -> {
                    String methodName = options.visitMethodNamer().apply(jc.name());
                    JMethod method = clazz.method(JMod.PUBLIC, results.returnType(), methodName);
                    method._throws(results.exceptionType());
                    method.param(jc, "aBean");
                    results.argType().ifPresent(jTypeVar -> method.param(jTypeVar, "arg"));
                    method.body()._return(JExpr._null());
                    method.annotate(Override.class);
                });
    }
}

