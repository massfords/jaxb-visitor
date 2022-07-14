package com.massfords.jaxb.codegen.creators.classes;

import com.massfords.jaxb.codegen.AllInterfacesCreated;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.outline.Outline;

/**
 * enum that reports whether a bean property is traversable.
 * VISITABLE = it's definitely traversable, and we just need to do a null check
 * NO = it's definitely NOT traversable, and we should skip the bean property
 * MAYBE = it's a JAXBElement&lt;?&gt; or Object, so we'll test the value with an
 * instanceof and perform a cast
 * DIRECT = It's one of the externally mapped classes which will
 * be on the Visitor interface but doesn't have an accept
 * method.
 */
public enum TraversableCodeGenStrategy {
    /**
     * VISITABLE means we just have to test for a null instance.
     * We don't need to do a cast because the type is definitely a Visitable
     */
    VISITABLE {
        @Override
        public void jaxbElementCollection(TraversalContext context, JClass collType, JMethod getter) {
            JForEach forEach = context.traverseBlock()
                    .forEach(collType, "obj", JExpr.invoke(context.beanParam(), getter));
            forEach.body()._if(JExpr.ref("obj").invoke("getValue").ne(JExpr._null()))
                    ._then()
                    .invoke(JExpr.ref("obj").invoke("getValue"), "accept").arg(context.vizParam());
        }

        @Override
        public void collection(TraversalContext context,
                               JClass rawType, JMethod getter) {
            JForEach forEach = context.traverseBlock().forEach(rawType.getTypeParameters().get(0), "bean",
                    JExpr.invoke(context.beanParam(), getter));
            addParams(context, forEach.body().invoke(JExpr.ref("bean"), "accept"));
        }

        @Override
        public void bean(TraversalContext context, JMethod getter) {
            addParams(context, context.traverseBlock()._if(JExpr.invoke(context.beanParam(), getter).ne(JExpr._null()))
                    ._then().invoke(JExpr.invoke(context.beanParam(), getter), "accept"));
        }

        @Override
        public void jaxbElement(TraversalContext context, JClass rawType, JMethod getter) {
            addParams(context, context.traverseBlock()._if(
                            JExpr.invoke(context.beanParam(), getter).ne(JExpr._null()))._then()
                    .invoke(JExpr.invoke(context.beanParam(), getter).invoke("getValue"), "accept"));
        }
    },
    /**
     * All no-op methods here. We're not going to traverse into the type because it's
     * either a primitive type or not anything we can traverse into.
     */
    NO {
        @Override
        public void jaxbElementCollection(TraversalContext context, JClass collType, JMethod getter) {

        }

        @Override
        public void jaxbElement(TraversalContext context, JClass rawType, JMethod getter) {

        }

        @Override
        public void collection(TraversalContext context, JClass rawType, JMethod getter) {

        }

        @Override
        public void bean(TraversalContext context, JMethod getter) {

        }
    },
    /**
     * MAYBE means we just have to test for an instanceof Visitable and cast where it's true
     * This is often the case when elements share a common type.
     * I don't like this approach, but it's a necessary evil since you can't always change the
     * schema to better support codegen
     */
    MAYBE {
        @Override
        public void jaxbElementCollection(TraversalContext context, JClass collType, JMethod getter) {
            JForEach forEach = context.traverseBlock()
                    .forEach(collType, "obj", JExpr.invoke(context.beanParam(), getter));
            AllInterfacesCreated state = context.shared().state();
            forEach.body()._if(JExpr.ref("obj").invoke("getValue")
                            ._instanceof(state.visitable()))
                    ._then()
                    .invoke(JExpr.cast(state.visitable(), JExpr.ref("obj").invoke("getValue")), "accept")
                    .arg(context.vizParam());
        }

        @Override
        public void jaxbElement(TraversalContext context, JClass rawType, JMethod getter) {
            AllInterfacesCreated state = context.shared().state();
            addParams(context, context.traverseBlock()._if(
                            JExpr.invoke(context.beanParam(), getter).ne(JExpr._null())
                                    .cand(
                                            JExpr.invoke(context.beanParam(), getter).invoke("getValue")
                                                    ._instanceof(state.visitable()))).
                    _then()
                    .invoke(JExpr.cast(state.visitable(), JExpr.invoke(context.beanParam(), getter)
                            .invoke("getValue")), "accept"));
        }

        @Override
        public void collection(TraversalContext context, JClass rawType, JMethod getter) {

            Outline outline = context.shared().state().initial().outline();
            JClass jaxbElementClass = outline.getCodeModel().ref(context.shared().options().getJAXBElementClass())
                    .narrow(outline.getCodeModel()
                            .ref(Object.class).wildcard());

            JForEach forEach = context.traverseBlock().forEach(rawType.getTypeParameters().get(0), "bean",
                    JExpr.invoke(context.beanParam(), getter));
            JBlock body = forEach.body();
            JFieldRef bean = JExpr.ref("bean");
            AllInterfacesCreated state = context.shared().state();
            JConditional conditional = body._if(bean._instanceof(state.visitable()));
            addParams(context, conditional._then().invoke(JExpr.cast(state.visitable(), bean), "accept"));

            // if it's a mixed type schema, then it could be returning JAXBElement's here
            // add some code to check to see if the element has a value that is visitable
            // and if so, visit it.
            conditional = conditional._elseif(bean._instanceof(jaxbElementClass));
            addParams(context, conditional._then()
                    ._if(JExpr.invoke(JExpr.cast(jaxbElementClass, bean), "getValue").
                            _instanceof(state.visitable()))
                    ._then()
                    .invoke(JExpr.cast(state.visitable(),
                            JExpr.invoke(JExpr.cast(jaxbElementClass, bean), "getValue")), "accept"));
            for (JClass jc : state.initial().directClasses()) {
                // Despite the name below, _elseif doesn't actually produce
                // an else if. Instead, it produces an else with an if
                // in the body. This is syntax issue only, it's semantically
                // equivalent.
                conditional = conditional._elseif(bean._instanceof(jc));
                conditional._then().invoke(context.vizParam(), "visit").arg(JExpr.cast(jc, bean));
            }
        }

        @Override
        public void bean(TraversalContext context, JMethod getter) {
            context.traverseBlock()._if(JExpr.invoke(context.beanParam(), getter)
                            ._instanceof(context.shared().state().visitable()))
                    ._then()
                    .invoke(JExpr.cast(context.shared().state().visitable(),
                            JExpr.invoke(context.beanParam(), getter)), "accept")
                    .arg(context.vizParam());
        }

    },
    DIRECT {
        @Override
        public void jaxbElementCollection(TraversalContext context, JClass collType, JMethod getter) {
            // should prob throw an error here. I don't think we should ever have
            // a jaxb element w/ an external class
        }

        @Override
        public void jaxbElement(TraversalContext context, JClass rawType, JMethod getter) {
            // should prob throw an error here. I don't think we should ever have
            // a jaxb element w/ an external class
        }

        @Override
        public void collection(TraversalContext context, JClass rawType, JMethod getter) {
            JForEach forEach = context.traverseBlock().forEach(rawType.getTypeParameters().get(0), "bean",
                    JExpr.invoke(context.beanParam(), getter));
            JBlock body = forEach.body();
            body.invoke(context.vizParam(), "visit").arg(forEach.var());
        }

        @Override
        public void bean(TraversalContext context, JMethod getter) {
            context.traverseBlock()._if(
                            JExpr.invoke(context.beanParam(), getter).ne(JExpr._null()))._then()
                    .invoke(context.vizParam(), "visit").arg(JExpr.invoke(context.beanParam(), getter));
        }
    };

    public abstract void jaxbElementCollection(TraversalContext context, JClass collType, JMethod getter);

    public abstract void jaxbElement(TraversalContext context, JClass rawType, JMethod getter);

    public abstract void collection(TraversalContext context, JClass rawType, JMethod getter);

    public abstract void bean(TraversalContext context, JMethod getter);

    private static void addParams(TraversalContext context, JInvocation invocation) {
        invocation.arg(context.vizParam());
        context.argParam().map(invocation::arg);
    }
}
