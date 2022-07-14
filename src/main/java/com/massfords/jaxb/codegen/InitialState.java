package com.massfords.jaxb.codegen;

import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

@Value.Immutable
@Value.Style(strictBuilder = true)
public interface InitialState {
    Outline outline();

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
    Set<JClass> directClasses();
}

