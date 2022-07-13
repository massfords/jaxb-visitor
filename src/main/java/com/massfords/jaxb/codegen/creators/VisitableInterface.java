package com.massfords.jaxb.codegen.creators;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.VisitorCreated;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static com.massfords.jaxb.codegen.creators.CodeCreator.annotateGenerated;

/**
 * Creates the interface to tag each one of the beans to add
 * an accept method as part of the double dispatch Visitor pattern
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class VisitableInterface {

    public static JDefinedClass create(VisitorCreated state, CodeGenOptions options) {
        final JDefinedClass _interface = state.getInitial().getOutline().getClassFactory().createInterface(options.getPackageForVisitor(), "Visitable", null);
        annotateGenerated(_interface, options);
        final JMethod _method = _interface.method(JMod.NONE, void.class, "accept");
        final JTypeVar returnType = _method.generify("R");
        final JTypeVar exceptionType = _method.generify("E", Throwable.class);
        _method.type(returnType);
        _method._throws(exceptionType);
        final JClass narrowedVisitor = state.getVisitor().narrow(returnType, exceptionType);
        _method.param(narrowedVisitor, "aVisitor");

        state.getInitial().getSorted().forEach(classOutline -> classOutline.implClass._implements(_interface));
        return _interface;
    }
}
