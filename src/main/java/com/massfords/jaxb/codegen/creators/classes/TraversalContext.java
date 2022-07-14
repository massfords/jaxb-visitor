package com.massfords.jaxb.codegen.creators.classes;

import com.massfords.jaxb.codegen.AllInterfacesCreated;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
interface TraversalContext {
    JVar vizParam();

    Optional<JVar> argParam();

    AllInterfacesCreated state();

    CodeGenOptions options();

    JBlock traverseBlock();
}
