package com.massfords.jaxb.codegen.states;

import com.massfords.jaxb.VisitorPlugin;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.ClassOutline;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value.Immutable
@Value.Style(strictBuilder = true)
public interface InitialState extends VisitorPlugin.InitialState {

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

    @Value.Lazy
    default Map<String, JClass> directClassesByName() {
        return directClasses()
                .stream()
                .collect(Collectors.toMap(JType::fullName, Function.identity()));
    }
}

