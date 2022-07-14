package com.massfords.jaxb.codegen;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface CodeGenStates {
    interface InitialState {
        Outline outline();

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

        @Value.Lazy
        default Map<String, JClass> directClassesByName() {
            return directClasses()
                    .stream()
                    .collect(Collectors.toMap(JType::fullName, Function.identity()));
        }

    }

    interface VisitorState {
        JDefinedClass visitor();
        JClass narrowedVisitor();
        InitialState initial();
    }

    interface AllInterfacesCreatedState extends VisitorState {
        JDefinedClass visitable();
        JDefinedClass traverser();
        JDefinedClass progressMonitor();
    }

}
