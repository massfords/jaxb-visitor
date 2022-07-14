package com.massfords.jaxb.codegen;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(strictBuilder = true)
public interface AllInterfacesCreated extends VisitorState {
    JDefinedClass visitable();
    JDefinedClass traverser();
    JDefinedClass progressMonitor();
    VisitorState visitorState();

    default JDefinedClass visitor() {
        return visitorState().visitor();
    }
    default JClass narrowedVisitor() {
        return visitorState().narrowedVisitor();
    }
    default InitialState initialState() {
        return visitorState().initialState();
    }

}
