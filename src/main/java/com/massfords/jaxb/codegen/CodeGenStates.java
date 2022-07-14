package com.massfords.jaxb.codegen;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

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
