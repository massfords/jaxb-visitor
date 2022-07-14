package com.massfords.jaxb.codegen.creators.classes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
interface TraversalContext {
    JVar vizParam();

    Optional<JVar> argParam();

    JBlock traverseBlock();

    SharedDepthFirstTraversalContext shared();

    JVar beanParam();
}
