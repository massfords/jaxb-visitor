package com.massfords.jaxb.codegen.creators.classes;

import com.massfords.jaxb.VisitorPlugin;
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
import org.immutables.value.Value;

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
    private TraversingVisitor() {
    }

    @Value.Immutable
    public interface TraversingVisitorContext {
        JDefinedClass traversingVisitor();

        GenerifyResults generics();

        CodeGenOptions options();
    }

    public static void createClass(VisitorPlugin.AllInterfacesCreatedState state, CodeGenOptions options) {

        JDefinedClass traversingVisitor = state.initial().outline().getClassFactory().createClass(
                options.packageForVisitor(), "TraversingVisitor", null);
        final GenerifyResults generics = Utils.generify(traversingVisitor, options);
        final JClass narrowedTraverser = state.traverser().narrow(
                Stream.of(generics.exceptionType(), generics.argType().orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));

        TraversingVisitorContext context = ImmutableTraversingVisitorContext.builder()
                .traversingVisitor(traversingVisitor)
                .generics(generics)
                .options(options)
                .build();


        traversingVisitor._implements(state.narrowedVisitor());
        JMethod ctor = traversingVisitor.constructor(JMod.PUBLIC);
        ctor.param(narrowedTraverser, "aTraverser");
        ctor.param(state.narrowedVisitor(), "aVisitor");
        JFieldVar fieldTraverseFirst = traversingVisitor.field(JMod.PRIVATE, Boolean.TYPE, "traverseFirst");
        JFieldVar fieldVisitor = traversingVisitor.field(JMod.PRIVATE, state.narrowedVisitor(), "visitor");
        JFieldVar fieldTraverser = traversingVisitor.field(JMod.PRIVATE, narrowedTraverser, "traverser");
        JFieldVar fieldMonitor = traversingVisitor.field(JMod.PRIVATE, state.progressMonitor(), "progressMonitor");
        addGetterAndSetter(context, fieldTraverseFirst);
        addGetterAndSetter(context, fieldVisitor);
        addGetterAndSetter(context, fieldTraverser);
        addGetterAndSetter(context, fieldMonitor);
        ctor.body().assign(fieldTraverser, JExpr.ref("aTraverser"));
        ctor.body().assign(fieldVisitor, JExpr.ref("aVisitor"));

        annotateGenerated(traversingVisitor, options);

        allConcreteClasses(state.initial().allClasses(), Collections.emptySet()).forEach(jc -> generate(context, jc));
        state.initial().directClasses().forEach(jc -> generateForDirectClass(context, jc));
    }

    private static void generateForDirectClass(TraversingVisitorContext context, JClass implClass) {
        String visitMethodName = context.options().visitMethodNamer().apply(implClass.name());
        GenerifyResults generics = context.generics();
        JMethod travViz = context.traversingVisitor().method(JMod.PUBLIC, generics.returnType(), visitMethodName);
        travViz._throws(generics.exceptionType());
        JVar beanVar = travViz.param(implClass, "aBean");
        travViz.annotate(Override.class);
        JBlock travVizBloc = travViz.body();

        JVar argVar = generics.argType().map(jTypeVar -> travViz.param(jTypeVar, "arg")).orElse(null);

        addTraverseBlock(context, travViz, beanVar, argVar, true);

        JVar retVal = travVizBloc.decl(generics.returnType(), "returnVal");

        travVizBloc.assign(retVal, JExpr.invoke(JExpr.invoke("getVisitor"), visitMethodName).arg(beanVar));

        travVizBloc._if(JExpr.ref("progressMonitor").ne(JExpr._null()))
                ._then()
                .invoke(JExpr.ref("progressMonitor"), "visited").arg(beanVar);

        addTraverseBlock(context, travViz, beanVar, argVar, false);

        travVizBloc._return(retVal);
    }

    private static void generate(TraversingVisitorContext context, JClass implClass) {
        // add method impl to traversing visitor
        GenerifyResults generics = context.generics();
        JMethod travViz = context.traversingVisitor().method(JMod.PUBLIC, generics.returnType(),
                context.options().visitMethodNamer().apply(implClass.name()));
        travViz._throws(generics.exceptionType());
        JVar beanVar = travViz.param(implClass, "aBean");
        JVar argVar = generics.argType().map(jTypeVar -> travViz.param(jTypeVar, "arg")).orElse(null);
        travViz.annotate(Override.class);
        JBlock travVizBloc = travViz.body();

        addTraverseBlock(context, travViz, beanVar, argVar, true);

        JVar retVal = travVizBloc.decl(generics.returnType(), "returnVal");
        JInvocation invocation = JExpr.invoke(beanVar, "accept").arg(JExpr.invoke("getVisitor"));
        JInvocation callAccept = Optional.ofNullable(argVar).map(invocation::arg).orElse(invocation);
        travVizBloc.assign(retVal, callAccept);
        travVizBloc._if(JExpr.ref("progressMonitor").ne(JExpr._null()))
                ._then()
                .invoke(JExpr.ref("progressMonitor"), "visited").arg(beanVar);

        // case to traverse after the visit
        addTraverseBlock(context, travViz, beanVar, argVar, false);
        travVizBloc._return(retVal);
    }

    private static void addTraverseBlock(TraversingVisitorContext context,
                                         JMethod travViz, JVar beanVar, JVar argVar, boolean flag) {
        JBlock travVizBloc = travViz.body();

        // case to traverse before the visit
        JBlock block = travVizBloc._if(JExpr.ref("traverseFirst").eq(JExpr.lit(flag)))._then();
        String traverseMethodName = context.options().traverseMethodNamer().apply(beanVar.type().name());
        JInvocation invoke = block.invoke(JExpr.invoke("getTraverser"), traverseMethodName)
                .arg(beanVar)
                .arg(JExpr._this());
        if (argVar != null) {
            invoke.arg(argVar);
        }
        block._if(JExpr.ref("progressMonitor").ne(JExpr._null())).
                _then()
                .invoke(JExpr.ref("progressMonitor"), "traversed").arg(beanVar);
    }

    /**
     * Convenience method to add a getter and setter method for the given field.
     */
    private static void addGetterAndSetter(TraversingVisitorContext context, JFieldVar field) {
        String propName = Character.toUpperCase(field.name().charAt(0)) + field.name().substring(1);
        context.traversingVisitor().method(JMod.PUBLIC, field.type(), "get" + propName).body()._return(field);
        JMethod setVisitor = context.traversingVisitor().method(JMod.PUBLIC, void.class, "set" + propName);
        JVar visParam = setVisitor.param(field.type(), "aVisitor");
        setVisitor.body().assign(field, visParam);
    }
}
