package com.massfords.jaxb.codegen.creators.classes;

import com.massfords.jaxb.codegen.AllInterfacesCreated;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.creators.GenerifyResults;
import com.massfords.jaxb.codegen.creators.Utils;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.massfords.jaxb.codegen.ClassDiscoverer.allConcreteClasses;
import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;

/**
 * Creates a traversing visitor. This visitor pairs a visitor and a traverser. The result is a visitor that
 * will traverse the entire graph and visit each of the nodes using the provided visitor.
 *
 * @author markford
 */
public final class TraversingVisitor {
    public static void createClass(AllInterfacesCreated state, CodeGenOptions options) {

        JDefinedClass traversingVisitor = state.outline().getClassFactory().createClass(options.packageForVisitor(), "TraversingVisitor", null);
        final GenerifyResults generics = Utils.generify(traversingVisitor, options);
        final JClass narrowedTraverser =  state.traverser().narrow(
                Stream.of(generics.exceptionType(), generics.argType().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        traversingVisitor._implements(state.narrowedVisitor());
        JMethod ctor = traversingVisitor.constructor(JMod.PUBLIC);
        ctor.param(narrowedTraverser, "aTraverser");
        ctor.param(state.narrowedVisitor(), "aVisitor");
        JFieldVar fieldTraverseFirst = traversingVisitor.field(JMod.PRIVATE, Boolean.TYPE, "traverseFirst");
        JFieldVar fieldVisitor = traversingVisitor.field(JMod.PRIVATE, state.narrowedVisitor(), "visitor");
        JFieldVar fieldTraverser = traversingVisitor.field(JMod.PRIVATE, narrowedTraverser, "traverser");
        JFieldVar fieldMonitor = traversingVisitor.field(JMod.PRIVATE, state.progressMonitor(), "progressMonitor");
        addGetterAndSetter(traversingVisitor, fieldTraverseFirst);
        addGetterAndSetter(traversingVisitor, fieldVisitor);
        addGetterAndSetter(traversingVisitor, fieldTraverser);
        addGetterAndSetter(traversingVisitor, fieldMonitor);
        ctor.body().assign(fieldTraverser, JExpr.ref("aTraverser"));
        ctor.body().assign(fieldVisitor, JExpr.ref("aVisitor"));

        annotateGenerated(traversingVisitor, options);

        allConcreteClasses(state.allClasses(), Collections.emptySet()).forEach(jc -> generate(traversingVisitor, generics, jc, options));
        state.directClasses().forEach(jc -> generateForDirectClass(traversingVisitor, generics, jc, options));
    }

    private static void generateForDirectClass(JDefinedClass traversingVisitor, GenerifyResults generics, JClass implClass, CodeGenOptions options) {
        // add method impl to traversing visitor
        JMethod travViz;
        String visitMethodName = options.visitMethodNamer().apply(implClass.name());
        travViz = traversingVisitor.method(JMod.PUBLIC, generics.returnType(), visitMethodName);
        travViz._throws(generics.exceptionType());
        JVar beanVar = travViz.param(implClass, "aBean");
        travViz.annotate(Override.class);
        JBlock travVizBloc = travViz.body();

        JVar argVar = generics.argType().map(jTypeVar -> travViz.param(jTypeVar, "arg")).orElse(null);

        addTraverseBlock(travViz, beanVar, argVar,true, options);

        JVar retVal = travVizBloc.decl(generics.returnType(), "returnVal");

        travVizBloc.assign(retVal, JExpr.invoke(JExpr.invoke("getVisitor"), visitMethodName).arg(beanVar));

        travVizBloc._if(JExpr.ref("progressMonitor").ne(JExpr._null()))._then().invoke(JExpr.ref("progressMonitor"), "visited").arg(beanVar);

        addTraverseBlock(travViz, beanVar, argVar,false, options);

        travVizBloc._return(retVal);
    }

    private static void generate(JDefinedClass traversingVisitor, GenerifyResults generics, JClass implClass, CodeGenOptions options) {
        // add method impl to traversing visitor
        JMethod travViz;
        travViz = traversingVisitor.method(JMod.PUBLIC, generics.returnType(), options.visitMethodNamer().apply(implClass.name()));
        travViz._throws(generics.exceptionType());
        JVar beanVar = travViz.param(implClass, "aBean");
        JVar argVar = generics.argType().map(jTypeVar -> travViz.param(jTypeVar, "arg")).orElse(null);
        travViz.annotate(Override.class);
        JBlock travVizBloc = travViz.body();

        addTraverseBlock(travViz, beanVar, argVar, true, options);

        JVar retVal = travVizBloc.decl(generics.returnType(), "returnVal");
        JInvocation invocation = JExpr.invoke(beanVar, "accept").arg(JExpr.invoke("getVisitor"));
        JInvocation callAccept = Optional.ofNullable(argVar).map(invocation::arg).orElse(invocation);
        travVizBloc.assign(retVal, callAccept);
        travVizBloc._if(JExpr.ref("progressMonitor").ne(JExpr._null()))._then().invoke(JExpr.ref("progressMonitor"), "visited").arg(beanVar);

        // case to traverse after the visit
        addTraverseBlock(travViz, beanVar, argVar, false, options);
        travVizBloc._return(retVal);
    }

    private static void addTraverseBlock(JMethod travViz, JVar beanVar, JVar argVar, boolean flag, CodeGenOptions options) {
        JBlock travVizBloc = travViz.body();

        // case to traverse before the visit
        JBlock block = travVizBloc._if(JExpr.ref("traverseFirst").eq(JExpr.lit(flag)))._then();
        String traverseMethodName = options.traverseMethodNamer().apply(beanVar.type().name());
        JInvocation invoke = block.invoke(JExpr.invoke("getTraverser"), traverseMethodName).arg(beanVar).arg(JExpr._this());
        if (argVar != null) {
            invoke.arg(argVar);
        }
        block._if(JExpr.ref("progressMonitor").ne(JExpr._null()))._then().invoke(JExpr.ref("progressMonitor"), "traversed").arg(beanVar);
    }

    /**
     * Convenience method to add a getter and setter method for the given field.
     *
     * @param traversingVisitor
     * @param field
     */
    private static void addGetterAndSetter(JDefinedClass traversingVisitor, JFieldVar field) {
        String propName = Character.toUpperCase(field.name().charAt(0)) + field.name().substring(1);
        traversingVisitor.method(JMod.PUBLIC, field.type(), "get" + propName).body()._return(field);
        JMethod setVisitor = traversingVisitor.method(JMod.PUBLIC, void.class, "set" + propName);
        JVar visParam = setVisitor.param(field.type(), "aVisitor");
        setVisitor.body().assign(field, visParam);
    }
}
