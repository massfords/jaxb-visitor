package com.massfords.jaxb;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.Set;

/**
 * An optional callback class for the TraversingVisitor that someone
 * could implement in order to get callbacks on when beans are traversed
 * or visited.
 *
 * Not sure I want to keep this as part of the project...
 */
class CreateTraversingVisitorProgressMonitorInterface extends CodeCreator {

    CreateTraversingVisitorProgressMonitorInterface(Outline outline,
                                                    JPackage jPackage) {
        super(outline, jPackage);
    }

    @Override
    protected void run(Set<ClassOutline> classes, Set<JClass> directClasses) {
        setOutput( outline.getClassFactory().createInterface(jpackage, "TraversingVisitorProgressMonitor", null) );
        getOutput().method(JMod.NONE, void.class, "visited").param(Object.class, "aVisitable");
        getOutput().method(JMod.NONE, void.class, "traversed").param(Object.class, "aVisitable");
    }

}
