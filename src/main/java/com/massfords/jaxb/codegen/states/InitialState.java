package com.massfords.jaxb.codegen.states;

import com.massfords.jaxb.VisitorPlugin;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.ClassOutline;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Value.Immutable
@Value.Style(stagedBuilder = true)
public interface InitialState extends VisitorPlugin.InitialState {

    Map<String, JClass> directClassesByName();

    @Value.Derived
    default Collection<ClassOutline> allClasses() {
        Set<ClassOutline> sorted = new TreeSet<>((aOne, aTwo) -> {
            String one = aOne.implClass.fullName();
            String two = aTwo.implClass.fullName();
            return one.compareTo(two);
        });
        sorted.addAll(outline().getClasses());
        return sorted;
    }
}

