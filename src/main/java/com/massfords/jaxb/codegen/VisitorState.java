package com.massfords.jaxb.codegen;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import org.immutables.value.Value;

@Value.Immutable
public interface VisitorState extends InitialState {
    JDefinedClass visitor();
    JClass narrowedVisitor();
}
