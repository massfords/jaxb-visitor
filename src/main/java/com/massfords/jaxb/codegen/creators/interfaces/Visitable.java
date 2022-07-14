package com.massfords.jaxb.codegen.creators.interfaces;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.VisitorState;
import com.massfords.jaxb.codegen.creators.GenerifyResults;
import com.massfords.jaxb.codegen.creators.Utils;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;

/**
 * Creates the interface to tag each one of the beans to add
 * an accept method as part of the double dispatch Visitor pattern
 */
public final class Visitable {

    public static JDefinedClass create(VisitorState state, CodeGenOptions options) {
        final JDefinedClass _interface = state.outline().getClassFactory().createInterface(options.packageForVisitor(), "Visitable", null);
        annotateGenerated(_interface, options);
        final JMethod _method = _interface.method(JMod.NONE, void.class, "accept");
        final GenerifyResults genericTypes = Utils.generify(_method, options);
        _method.type(genericTypes.returnType());
        _method._throws(genericTypes.exceptionType());
        _method.param(state.narrowedVisitor(), "aVisitor");
        genericTypes.argType().ifPresent(jTypeVar -> _method.param(jTypeVar, "arg"));

        state.allClasses().forEach(classOutline -> classOutline.implClass._implements(_interface));
        return _interface;
    }
}
