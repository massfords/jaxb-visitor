package com.massfords.jaxb.codegen.creators.interfaces;

import com.massfords.jaxb.VisitorPlugin;
import com.massfords.jaxb.codegen.CodeGenOptions;
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
    private Visitable() {
    }

    public static JDefinedClass create(VisitorPlugin.VisitorState state, CodeGenOptions options) {
        final JDefinedClass visitableModel = state.initial().outline().getClassFactory().createInterface(
                options.packageForVisitor(), "Visitable", null);
        annotateGenerated(visitableModel, options);
        final JMethod acceptMethod = visitableModel.method(JMod.NONE, void.class, "accept");
        final GenerifyResults genericTypes = Utils.generify(acceptMethod, options);
        acceptMethod.type(genericTypes.returnType());
        acceptMethod._throws(genericTypes.exceptionType());
        acceptMethod.param(state.narrowedVisitor(), "aVisitor");
        genericTypes.argType().ifPresent(jTypeVar -> acceptMethod.param(jTypeVar, "arg"));

        state.initial().allClasses().forEach(classOutline -> classOutline.implClass._implements(visitableModel));
        return visitableModel;
    }
}
