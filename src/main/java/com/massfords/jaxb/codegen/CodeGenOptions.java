package com.massfords.jaxb.codegen;

import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import jakarta.xml.bind.JAXBElement;
import lombok.Builder;
import lombok.SneakyThrows;
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

    @SneakyThrows
    public Class<?> getJAXBElementClass() {
        if (isUseLegacyImports()){
            return javax.xml.bind.JAXBElement.class;
        }
        return JAXBElement.class;
    }

    public boolean isJAXBElement(JType type) {
        return type.fullName().startsWith(getJAXBElementClass().getName());
    }

}
