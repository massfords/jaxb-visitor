package com.massfords.jaxb.codegen;

import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
public interface InitialState {
    Outline outline();
    Set<ClassOutline> sorted();
    Set<JClass> directClasses();
}

