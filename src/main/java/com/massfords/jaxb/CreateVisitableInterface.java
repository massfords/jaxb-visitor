package com.massfords.jaxb;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTypeVar;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.Set;

public class CreateVisitableInterface extends CodeCreator {

    private JDefinedClass visitor;

    public CreateVisitableInterface(JDefinedClass visitor, Outline outline, JPackage jPackage) {
        super(outline, jPackage);
        this.visitor = visitor;
    }

    @Override
    protected void run(Set<ClassOutline> classes) {
        final JDefinedClass _interface = outline.getClassFactory().createInterface(jpackage, "Visitable", null);
		setOutput( _interface );
		final JMethod _method = getOutput().method(JMod.PUBLIC, void.class, "accept");
		final JTypeVar returnType = _method.generify("R");
		final JTypeVar exceptionType = _method.generify("E", Throwable.class);
		_method.type(returnType);
		_method._throws(exceptionType);
		final JClass narrowedVisitor = visitor.narrow(returnType, exceptionType);
		_method.param(narrowedVisitor, "aVisitor");
        
        for(ClassOutline classOutline : classes) {
            classOutline.implClass._implements(getOutput());
        }
    }

}
