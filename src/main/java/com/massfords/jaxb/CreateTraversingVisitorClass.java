package com.massfords.jaxb;

import com.sun.codemodel.*;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.Set;

/**
 * Creates a traversing visitor. This visitor pairs a visitor and a traverser. The result is a visitor that 
 * will traverse the entire graph and visit each of the nodes using the provided visitor.
 * 
 * @author markford
 */
public class CreateTraversingVisitorClass extends CodeCreator {

    private JDefinedClass progressMonitor;
    private JDefinedClass visitor;
    private JDefinedClass traverser;

    public CreateTraversingVisitorClass(JDefinedClass visitor, JDefinedClass progressMonitor,
                                        JDefinedClass traverser, Outline outline, JPackage jPackage) {
        super(outline, jPackage);
        this.visitor = visitor;
        this.traverser = traverser;
        this.progressMonitor = progressMonitor;
    }

    @Override
    protected void run(Set<ClassOutline> classes) {

        JDefinedClass traversingVisitor = getOutline().getClassFactory().createClass(getPackage(), "TraversingVisitor", null);
        final JTypeVar returnType = traversingVisitor.generify("R");
        final JTypeVar exceptionType = traversingVisitor.generify("E", Throwable.class);
        final JClass narrowedVisitor = visitor.narrow(returnType).narrow(exceptionType);
        final JClass narrowedTraverser = traverser.narrow(exceptionType);
        traversingVisitor._implements(narrowedVisitor);
        JMethod ctor = traversingVisitor.constructor(JMod.PUBLIC);
        ctor.param(narrowedTraverser, "aTraverser");
        ctor.param(narrowedVisitor, "aVisitor");
        JFieldVar fieldTraverseFirst = traversingVisitor.field(JMod.PRIVATE, Boolean.TYPE, "traverseFirst");
        JFieldVar fieldVisitor = traversingVisitor.field(JMod.PRIVATE, narrowedVisitor, "visitor");
        JFieldVar fieldTraverser = traversingVisitor.field(JMod.PRIVATE, narrowedTraverser, "traverser");
        JFieldVar fieldMonitor = traversingVisitor.field(JMod.PRIVATE, progressMonitor, "progressMonitor");
        addGetterAndSetter(traversingVisitor, fieldTraverseFirst);
        addGetterAndSetter(traversingVisitor, fieldVisitor);
        addGetterAndSetter(traversingVisitor, fieldTraverser);
        addGetterAndSetter(traversingVisitor, fieldMonitor);
        ctor.body().assign(fieldTraverser, JExpr.ref("aTraverser"));
        ctor.body().assign(fieldVisitor, JExpr.ref("aVisitor"));
        
        setOutput(traversingVisitor);
        
        for(ClassOutline classOutline : classes) {
            if (!classOutline.target.isAbstract()) {
                // add method impl to traversing visitor
                JMethod travViz = traversingVisitor.method(JMod.PUBLIC, returnType, "visit");
                travViz._throws(exceptionType);
                JVar beanVar = travViz.param(classOutline.implClass, "aBean");
                travViz.annotate(Override.class);
                JBlock travVizBloc = travViz.body();

                addTraverseBlock(travViz, beanVar, true);

                JVar retVal = travVizBloc.decl(returnType, "returnVal");
                travVizBloc.assign(retVal,
                        JExpr.invoke(beanVar,"accept").arg(JExpr.invoke("getVisitor")));
                travVizBloc._if(JExpr.ref("progressMonitor").ne(JExpr._null()))._then().invoke(JExpr.ref("progressMonitor"), "visited").arg(beanVar);

                // case to traverse after the visit
                addTraverseBlock(travViz, beanVar, false);
                travVizBloc._return(retVal);
            }

        }
    }

    private void addTraverseBlock(JMethod travViz, JVar beanVar, boolean flag) {
        JBlock travVizBloc = travViz.body();

        // case to traverse before the visit
        JBlock block = travVizBloc._if(JExpr.ref("traverseFirst").eq(JExpr.lit(flag)))._then();
        block.invoke(JExpr.invoke("getTraverser"), "traverse").arg(beanVar).arg(JExpr._this());
        block._if(JExpr.ref("progressMonitor").ne(JExpr._null()))._then().invoke(JExpr.ref("progressMonitor"), "traversed").arg(beanVar);
    }

    /**
     * Convenience method to add a getter and setter method for the given field.
     * 
     * @param traversingVisitor
     * @param field
     */
    private void addGetterAndSetter(JDefinedClass traversingVisitor, JFieldVar field) {
        String propName = Character.toUpperCase(field.name().charAt(0)) + field.name().substring(1);
        traversingVisitor.method(JMod.PUBLIC, field.type(), "get" + propName).body()._return(field);
        JMethod setVisitor = traversingVisitor.method(JMod.PUBLIC, void.class, "set" + propName);
        JVar visParam = setVisitor.param(field.type(), "aVisitor");
        setVisitor.body().assign(field, visParam);
    }
}
