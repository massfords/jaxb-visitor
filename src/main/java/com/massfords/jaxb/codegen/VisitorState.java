package com.massfords.jaxb.codegen;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.Set;

@Value.Immutable
@Value.Style(strictBuilder = true)
public interface VisitorState extends InitialState {
    JDefinedClass visitor();
    JClass narrowedVisitor();
    InitialState initialState();

    default Outline outline() {
        return initialState().outline();
    }
    default Collection<ClassOutline> allClasses() {
        return initialState().allClasses();
    }
    default Set<JClass> directClasses() {
        return initialState().directClasses();
    }

}
