package com.massfords.jaxb.codegen.creators;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JGenerifiable;
import com.sun.codemodel.JType;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlElements;

public final class Utils {
    private Utils() {
    }
    public static GenerifyResults generify(JGenerifiable generifiable, CodeGenOptions options) {
        ImmutableGenerifyResults.Builder builder = ImmutableGenerifyResults.builder();
        builder.returnType(generifiable.generify("R"));
        builder.exceptionType(generifiable.generify("E", Throwable.class));
        if (options.includeArg()) {
            builder.argType(generifiable.generify("A"));
        }
        return builder.build();
    }

    public static void annotateGenerated(JDefinedClass output, CodeGenOptions options) {
        JAnnotationUse annotationUse = options.useLegacyImports()
                ? output.annotate(javax.annotation.Generated.class)
                : output.annotate(Generated.class);
        annotationUse.param("value", "Generated by jaxb-visitor");
    }

    public static boolean isJAXBElement(JType type, CodeGenOptions options) {
        return type.fullName().startsWith(options.getJAXBElementClass().getName());
    }

    public static boolean isXmlElements(JClass jc) {
        String fullName = jc.fullName();
        return fullName.equals(XmlElements.class.getName())
                || fullName.equals("javax.xml.bind.annotation.XmlElements");
    }
}
