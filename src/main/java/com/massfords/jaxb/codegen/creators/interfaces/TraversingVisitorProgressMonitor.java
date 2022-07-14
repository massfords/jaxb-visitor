package com.massfords.jaxb.codegen.creators.interfaces;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.VisitorState;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;

import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;

/**
 * An optional callback class for the TraversingVisitor that someone
 * could implement in order to get callbacks on when beans are traversed
 * or visited.
 * <p>
 * Not sure I want to keep this as part of the project...
 */
public final class TraversingVisitorProgressMonitor {
    public static JDefinedClass createInterface(VisitorState state, CodeGenOptions options) {
        JDefinedClass _interface = state.outline().getClassFactory().createInterface(
                options.packageForVisitor(), "TraversingVisitorProgressMonitor", null);
        annotateGenerated(_interface, options);
        _interface.method(JMod.NONE, void.class, "visited").param(Object.class, "aVisitable");
        _interface.method(JMod.NONE, void.class, "traversed").param(Object.class, "aVisitable");
        return _interface;
    }
}
