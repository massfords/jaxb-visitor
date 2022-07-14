package com.massfords.jaxb.codegen.creators;

import com.sun.codemodel.JTypeVar;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface GenerifyResults {
    JTypeVar returnType();
    JTypeVar exceptionType();
    Optional<JTypeVar> argType();
}
