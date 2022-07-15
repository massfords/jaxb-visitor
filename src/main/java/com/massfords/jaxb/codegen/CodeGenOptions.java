package com.massfords.jaxb.codegen;

import com.sun.codemodel.JPackage;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlTransient;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.function.Function;

@Value.Immutable
@Value.Style(strictBuilder = true)
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

    default Class<? extends Annotation> getXmlTransient() {
        if (useLegacyImports()) {
            return javax.xml.bind.annotation.XmlTransient.class;
        }
        return XmlTransient.class;
    }

    default Class<?> getUnmarshallerClass() {
        if (useLegacyImports()) {
            return javax.xml.bind.Unmarshaller.class;
        }
        return Unmarshaller.class;
    }

    default Class<?> getXmlElementDecl() {
        if (useLegacyImports()) {
            return javax.xml.bind.annotation.XmlElementDecl.class;
        }
        return XmlElementDecl.class;
    }
}
