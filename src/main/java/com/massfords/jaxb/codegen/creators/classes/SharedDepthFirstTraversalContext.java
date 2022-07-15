package com.massfords.jaxb.codegen.creators.classes;

import com.massfords.jaxb.VisitorPlugin;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JTypeVar;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(strictBuilder = true)
public interface SharedDepthFirstTraversalContext {
    JDefinedClass defaultTraverser();
    JClass narrowedVisitor();
    JClass narrowedTraverser();
    JTypeVar exceptionType();
    Optional<JTypeVar> argType();

    CodeGenOptions options();

    VisitorPlugin.AllInterfacesCreatedState state();

}
