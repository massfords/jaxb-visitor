package com.massfords.jaxb.codegen.creators.interfaces;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.VisitorState;
import com.massfords.jaxb.codegen.creators.Utils;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;

/**
 * Creates the interface to tag each one of the beans to add
 * an accept method as part of the double dispatch Visitor pattern
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Visitable {

    public static JDefinedClass create(VisitorState state, CodeGenOptions options) {
        final JDefinedClass _interface = state.getOutline().getClassFactory().createInterface(options.getPackageForVisitor(), "Visitable", null);
        annotateGenerated(_interface, options);
        final JMethod _method = _interface.method(JMod.NONE, void.class, "accept");
        final Utils.GenerifyResults genericTypes = Utils.generify(_method, options);
        _method.type(genericTypes.getReturnType());
        _method._throws(genericTypes.getExceptionType());
        _method.param(state.getNarrowedVisitor(), "aVisitor");
        Optional.ofNullable(genericTypes.getArgType()).ifPresent(jTypeVar -> _method.param(jTypeVar, "arg"));

        state.getSorted().forEach(classOutline -> classOutline.implClass._implements(_interface));
        return _interface;
    }
}
