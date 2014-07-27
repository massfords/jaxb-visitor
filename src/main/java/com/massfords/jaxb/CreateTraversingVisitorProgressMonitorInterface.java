package com.massfords.jaxb;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.Set;

public class CreateTraversingVisitorProgressMonitorInterface extends CodeCreator {

    private JDefinedClass visitable;

    public CreateTraversingVisitorProgressMonitorInterface(JDefinedClass visitable, Outline outline,
                                                           JPackage jPackage) {
        super(outline, jPackage);
        this.visitable = visitable;
    }

    @Override
    protected void run(Set<ClassOutline> classes) {
        setOutput( outline.getClassFactory().createInterface(jpackage, "TraversingVisitorProgressMonitor", null) );
        getOutput().method(JMod.PUBLIC, void.class, "visited").param(visitable, "aVisitable");
        getOutput().method(JMod.PUBLIC, void.class, "traversed").param(visitable, "aVisitable");
    }

}
