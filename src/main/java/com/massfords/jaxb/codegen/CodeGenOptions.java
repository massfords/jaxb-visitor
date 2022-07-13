package com.massfords.jaxb.codegen;

import com.sun.codemodel.JPackage;
import lombok.Builder;
import lombok.Value;

import java.util.function.Function;

@Value
@Builder
public class CodeGenOptions {
    boolean noIdrefTraversal;
    boolean useLegacyImports;
    JPackage packageForVisitor;
    Function<String,String> visitMethodNamer;
    Function<String,String> traverseMethodNamer;
}
