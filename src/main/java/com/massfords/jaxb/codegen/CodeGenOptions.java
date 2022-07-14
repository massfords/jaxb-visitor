package com.massfords.jaxb.codegen;

import com.sun.codemodel.JPackage;
import jakarta.xml.bind.JAXBElement;
import org.immutables.value.Value;

import java.util.function.Function;

@Value.Immutable
public interface CodeGenOptions {
    boolean noIdrefTraversal();

    boolean useLegacyImports();

    JPackage packageForVisitor();

    Function<String, String> visitMethodNamer();

    Function<String, String> traverseMethodNamer();

    boolean includeArg();

    @Value.Derived
    default Class<?> getJAXBElementClass() {
        if (useLegacyImports()) {
            return javax.xml.bind.JAXBElement.class;
        }
        return JAXBElement.class;
    }
}
