package com.massfords.jaxb.codegen;

import com.sun.codemodel.JDefinedClass;
import org.immutables.value.Value;

@Value.Immutable
public interface AllInterfacesCreated extends VisitorState {
    JDefinedClass visitable();
    JDefinedClass traverser();
    JDefinedClass progressMonitor();
}
