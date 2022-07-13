package com.massfords.jaxb.codegen.creators;

import com.massfords.jaxb.codegen.AllInterfacesCreated;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Collections;

import static com.massfords.jaxb.codegen.ClassDiscoverer.allConcreteClasses;
import static com.massfords.jaxb.codegen.creators.CodeCreator.annotateGenerated;

/**
 * Creates a traversing visitor. This visitor pairs a visitor and a traverser. The result is a visitor that
 * will traverse the entire graph and visit each of the nodes using the provided visitor.
 *
 * @author markford
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraversingVisitorClass {
    public static void createClass(AllInterfacesCreated codeGenState, CodeGenOptions options) {

        JDefinedClass traversingVisitor = codeGenState.getInitialState().getOutline().getClassFactory().createClass(options.getPackageForVisitor(), "TraversingVisitor", null);
        final JTypeVar returnType = traversingVisitor.generify("R");
        final JTypeVar exceptionType = traversingVisitor.generify("E", Throwable.class);
        final JClass narrowedVisitor = codeGenState.getVisitor().narrow(returnType).narrow(exceptionType);
        final JClass narrowedTraverser = codeGenState.getTraverser().narrow(exceptionType);
        traversingVisitor._implements(narrowedVisitor);
        JMethod ctor = traversingVisitor.constructor(JMod.PUBLIC);
        ctor.param(narrowedTraverser, "aTraverser");
        ctor.param(narrowedVisitor, "aVisitor");
        JFieldVar fieldTraverseFirst = traversingVisitor.field(JMod.PRIVATE, Boolean.TYPE, "traverseFirst");
        JFieldVar fieldVisitor = traversingVisitor.field(JMod.PRIVATE, narrowedVisitor, "visitor");
        JFieldVar fieldTraverser = traversingVisitor.field(JMod.PRIVATE, narrowedTraverser, "traverser");
        JFieldVar fieldMonitor = traversingVisitor.field(JMod.PRIVATE, codeGenState.getProgressMonitor(), "progressMonitor");
        addGetterAndSetter(traversingVisitor, fieldTraverseFirst);
        addGetterAndSetter(traversingVisitor, fieldVisitor);
        addGetterAndSetter(traversingVisitor, fieldTraverser);
        addGetterAndSetter(traversingVisitor, fieldMonitor);
        ctor.body().assign(fieldTraverser, JExpr.ref("aTraverser"));
        ctor.body().assign(fieldVisitor, JExpr.ref("aVisitor"));

        annotateGenerated(traversingVisitor);

        allConcreteClasses(codeGenState.getInitialState().getSorted(), Collections.emptySet()).forEach(jc -> generate(traversingVisitor, returnType, exceptionType, jc, options));
        codeGenState.getInitialState().getDirectClasses().forEach(jc -> generateForDirectClass(traversingVisitor, returnType, exceptionType, jc, options));
    }

    private static void generateForDirectClass(JDefinedClass traversingVisitor, JTypeVar returnType, JTypeVar exceptionType, JClass implClass, CodeGenOptions options) {
        // add method impl to traversing visitor
        JMethod travViz;
        String visitMethodName = options.getVisitMethodNamer().apply(implClass.name());
        travViz = traversingVisitor.method(JMod.PUBLIC, returnType, visitMethodName);
        travViz._throws(exceptionType);
        JVar beanVar = travViz.param(implClass, "aBean");
        travViz.annotate(Override.class);
        JBlock travVizBloc = travViz.body();

        addTraverseBlock(travViz, beanVar, true, options);

        JVar retVal = travVizBloc.decl(returnType, "returnVal");

        travVizBloc.assign(retVal, JExpr.invoke(JExpr.invoke("getVisitor"), visitMethodName).arg(beanVar));

        travVizBloc._if(JExpr.ref("progressMonitor").ne(JExpr._null()))._then().invoke(JExpr.ref("progressMonitor"), "visited").arg(beanVar);

        addTraverseBlock(travViz, beanVar, false, options);

        travVizBloc._return(retVal);
    }

    private static void generate(JDefinedClass traversingVisitor, JTypeVar returnType, JTypeVar exceptionType, JClass implClass, CodeGenOptions options) {
        // add method impl to traversing visitor
        JMethod travViz;
        travViz = traversingVisitor.method(JMod.PUBLIC, returnType, options.getVisitMethodNamer().apply(implClass.name()));
        travViz._throws(exceptionType);
        JVar beanVar = travViz.param(implClass, "aBean");
        travViz.annotate(Override.class);
        JBlock travVizBloc = travViz.body();

        addTraverseBlock(travViz, beanVar, true, options);

        JVar retVal = travVizBloc.decl(returnType, "returnVal");
        travVizBloc.assign(retVal,
                JExpr.invoke(beanVar, "accept").arg(JExpr.invoke("getVisitor")));
        travVizBloc._if(JExpr.ref("progressMonitor").ne(JExpr._null()))._then().invoke(JExpr.ref("progressMonitor"), "visited").arg(beanVar);

        // case to traverse after the visit
        addTraverseBlock(travViz, beanVar, false, options);
        travVizBloc._return(retVal);
    }

    private static void addTraverseBlock(JMethod travViz, JVar beanVar, boolean flag, CodeGenOptions options) {
        JBlock travVizBloc = travViz.body();

        // case to traverse before the visit
        JBlock block = travVizBloc._if(JExpr.ref("traverseFirst").eq(JExpr.lit(flag)))._then();
        String traverseMethodName = options.getTraverseMethodNamer().apply(beanVar.type().name());
        block.invoke(JExpr.invoke("getTraverser"), traverseMethodName).arg(beanVar).arg(JExpr._this());
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
