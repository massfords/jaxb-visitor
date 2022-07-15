package com.massfords.jaxb.codegen.states;

import com.massfords.jaxb.VisitorPlugin;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(strictBuilder = true)
public interface VisitorState extends VisitorPlugin.VisitorState {
}
