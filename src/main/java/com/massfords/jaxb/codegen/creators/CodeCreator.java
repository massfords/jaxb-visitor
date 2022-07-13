package com.massfords.jaxb.codegen.creators;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import jakarta.annotation.Generated;

public final class CodeCreator {
    static void annotateGenerated(JDefinedClass output) {
        JAnnotationUse annotationUse = output.annotate(Generated.class);
        annotationUse.param("value", "Generated by jaxb-visitor");
    }
}
