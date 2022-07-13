package com.massfords.jaxb.codegen;

import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder(toBuilder = true)
public class InitialState {
    Outline outline;
    Set<ClassOutline> sorted;
    Set<JClass> directClasses;
}
