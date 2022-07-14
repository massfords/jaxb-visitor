package com.massfords.jaxb.codegen;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(strictBuilder = true)
public interface InitialState extends CodeGenStates.InitialState {
}

