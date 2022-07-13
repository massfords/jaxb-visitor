package com.massfords.jaxb.codegen.creators;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.InitialState;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static com.massfords.jaxb.codegen.ClassDiscoverer.allConcreteClasses;
import static com.massfords.jaxb.codegen.creators.CodeCreator.annotateGenerated;

/**
 * Creates the visitor interface. After creating the interface, a visit method is added for each of the beans.
 *
 * @author markford
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class VisitorInterface {
    public static JDefinedClass create(InitialState codeGenState, CodeGenOptions options) {

        final JDefinedClass _interface = codeGenState.getOutline().getClassFactory().createInterface(options.getPackageForVisitor(), "Visitor", null);

        final JTypeVar returnType = _interface.generify("R");
        final JTypeVar exceptionType = _interface.generify("E", Throwable.class);

        annotateGenerated(_interface);

        allConcreteClasses(codeGenState.getSorted(), codeGenState.getDirectClasses()).forEach(jc -> {
            JMethod vizMethod;
            String visitMethod = options.getVisitMethodNamer().apply(jc.name());
            vizMethod = _interface.method(JMod.NONE, returnType, visitMethod);
            vizMethod._throws(exceptionType);
            vizMethod.param(jc, "aBean");
        });

        return _interface;
    }
}
