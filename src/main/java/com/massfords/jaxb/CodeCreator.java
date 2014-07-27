package com.massfords.jaxb;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.Set;

/**
 * Base class for creating code. Pattern here is to run through the set of generated beans
 * and produce some new interface or class in the form of a JDefinedClass.
 * 
 * @author markford
 */
public abstract class CodeCreator {

    protected final JPackage jpackage;
    protected final Outline outline;
    private JDefinedClass output;

    public CodeCreator(Outline outline, JPackage jPackage) {
        this.outline = outline;
        this.jpackage = jPackage;
    }

    protected abstract void run(Set<ClassOutline> classes);

    protected JDefinedClass getOutput() {
        return output;
    }
    
    protected void setOutput(JDefinedClass output) {
        this.output = output;
    }

    protected JPackage getPackage() {
        return jpackage;
    }

    protected Outline getOutline() {
        return outline;
    }
}
