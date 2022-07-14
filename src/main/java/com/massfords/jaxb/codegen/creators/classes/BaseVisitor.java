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
    public static void createClass(AllInterfacesCreated state, CodeGenOptions options) {
        JDefinedClass _class = state.outline().getClassFactory().createClass(options.packageForVisitor(), "BaseVisitor", null);
        annotateGenerated(_class, options);
        GenerifyResults results = Utils.generify(_class, options);
        _class._implements(state.narrowedVisitor());

        allConcreteClasses(state.sorted(), state.directClasses())
                .forEach(jc -> {
                    JMethod _method;
                    String methodName = options.visitMethodNamer().apply(jc.name());
                    _method = _class.method(JMod.PUBLIC, results.returnType(), methodName);
                    _method._throws(results.exceptionType());
                    _method.param(jc, "aBean");
                    results.argType().ifPresent(jTypeVar -> _method.param(jTypeVar, "arg"));
                    _method.body()._return(JExpr._null());
                    _method.annotate(Override.class);
                });
    }
}

