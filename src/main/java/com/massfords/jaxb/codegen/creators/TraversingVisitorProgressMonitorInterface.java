package com.massfords.jaxb.codegen.creators;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.VisitorCreated;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static com.massfords.jaxb.codegen.creators.CodeCreator.annotateGenerated;

/**
 * An optional callback class for the TraversingVisitor that someone
 * could implement in order to get callbacks on when beans are traversed
 * or visited.
 * <p>
 * Not sure I want to keep this as part of the project...
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraversingVisitorProgressMonitorInterface {
    public static JDefinedClass createInterface(VisitorCreated state, CodeGenOptions options) {
        JDefinedClass _interface = state.getInitial().getOutline().getClassFactory().createInterface(
                options.getPackageForVisitor(), "TraversingVisitorProgressMonitor", null);
        annotateGenerated(_interface);
        _interface.method(JMod.NONE, void.class, "visited").param(Object.class, "aVisitable");
        _interface.method(JMod.NONE, void.class, "traversed").param(Object.class, "aVisitable");
        return _interface;
    }
}
