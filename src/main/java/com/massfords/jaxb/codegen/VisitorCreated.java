package com.massfords.jaxb.codegen;

import com.sun.codemodel.JDefinedClass;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class VisitorCreated {
    InitialState initial;
    JDefinedClass visitor;
}
