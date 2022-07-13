package com.massfords.jaxb.codegen.creators;

import com.massfords.jaxb.codegen.AllInterfacesCreated;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static com.massfords.jaxb.codegen.ClassDiscoverer.allConcreteClasses;
import static com.massfords.jaxb.codegen.creators.CodeCreator.annotateGenerated;

/**
 * Creates a no-op implementation of the Visitor interface. After creating the class
 * a visit method is added for each of the beans that were generated.
 *
 * @author markford
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class BaseVisitorClass {
    public static void createClass(AllInterfacesCreated codeGenState, CodeGenOptions options) {
        JDefinedClass _class = codeGenState.getInitialState().getOutline().getClassFactory().createClass(options.getPackageForVisitor(), "BaseVisitor", null);
        annotateGenerated(_class);
        final JTypeVar returnType = _class.generify("R");
        final JTypeVar exceptionType = _class.generify("E", Throwable.class);
        final JClass narrowedVisitor = codeGenState.getVisitor().narrow(returnType, exceptionType);
        _class._implements(narrowedVisitor);

        allConcreteClasses(codeGenState.getInitialState().getSorted(), codeGenState.getInitialState().getDirectClasses())
                .forEach(jc -> {
                    JMethod _method;
                    String methodName = options.getVisitMethodNamer().apply(jc.name());
                    _method = _class.method(JMod.PUBLIC, returnType, methodName);
                    _method._throws(exceptionType);
                    _method.param(jc, "aBean");
                    _method.body()._return(JExpr._null());
                    _method.annotate(Override.class);
                });
    }
}

