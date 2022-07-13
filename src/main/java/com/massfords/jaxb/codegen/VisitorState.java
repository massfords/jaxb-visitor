package com.massfords.jaxb.codegen;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Set;

@Value
@Builder(toBuilder = true)
public class VisitorState {
    @NonNull
    Outline outline;
    @NonNull
    Set<ClassOutline> sorted;
    @NonNull
    Set<JClass> directClasses;
    @NonNull
    JDefinedClass visitor;
    @NonNull
    JClass narrowedVisitor;
}
