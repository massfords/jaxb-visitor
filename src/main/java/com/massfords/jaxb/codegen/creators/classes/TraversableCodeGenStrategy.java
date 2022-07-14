package com.massfords.jaxb.codegen.creators.classes;

import com.massfords.jaxb.codegen.AllInterfacesCreated;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
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
        public void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            JForEach forEach = traverseBlock.forEach(collType, "obj", JExpr.invoke(beanParam, getter));
            forEach.body()._if(JExpr.ref("obj").invoke("getValue").ne(JExpr._null()))._then().invoke(JExpr.ref("obj").invoke("getValue"), "accept").arg(vizParam);
        }

        @Override
        public void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            JForEach forEach = traverseBlock.forEach(rawType.getTypeParameters().get(0), "bean", JExpr.invoke(beanParam, getter));
            addParams(forEach.body().invoke(JExpr.ref("bean"), "accept"), vizParam, argParam);
        }

        @Override
        public void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            addParams(traverseBlock._if(JExpr.invoke(beanParam, getter).ne(JExpr._null()))._then().invoke(JExpr.invoke(beanParam, getter), "accept"), vizParam, argParam);
        }

        @Override
        public void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            addParams(traverseBlock._if(
                            JExpr.invoke(beanParam, getter).ne(JExpr._null()))._then()
                    .invoke(JExpr.invoke(beanParam, getter).invoke("getValue"), "accept"), vizParam, argParam);
        }
    },
    /**
     * All no-op methods here. We're not going to traverse into the type because it's
     * either a primitive type or not anything we can traverse into.
     */
    NO {
        @Override
        public void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {

        }

        @Override
        public void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {

        }

        @Override
        public void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {

        }

        @Override
        public void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {

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
        public void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            JForEach forEach = traverseBlock.forEach(collType, "obj", JExpr.invoke(beanParam, getter));
            forEach.body()._if(JExpr.ref("obj").invoke("getValue")._instanceof(state.visitable()))._then().invoke(JExpr.cast(state.visitable(), JExpr.ref("obj").invoke("getValue")), "accept").arg(vizParam);
        }

        @Override
        public void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            addParams(traverseBlock._if(
                            JExpr.invoke(beanParam, getter).ne(JExpr._null())
                                    .cand(
                                            JExpr.invoke(beanParam, getter).invoke("getValue")._instanceof(state.visitable())))._then()
                    .invoke(JExpr.cast(state.visitable(), JExpr.invoke(beanParam, getter).invoke("getValue")), "accept"), vizParam, argParam);
        }

        @Override
        public void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {

            JClass jaxbElementClass = outline.getCodeModel().ref(options.getJAXBElementClass()).narrow(outline.getCodeModel().ref(Object.class).wildcard());

            JForEach forEach = traverseBlock.forEach(rawType.getTypeParameters().get(0), "bean", JExpr.invoke(beanParam, getter));
            JBlock body = forEach.body();
            JFieldRef bean = JExpr.ref("bean");
            JConditional conditional = body._if(bean._instanceof(state.visitable()));
            addParams(conditional._then().invoke(JExpr.cast(state.visitable(), bean), "accept"), vizParam, argParam);

            // if it's a mixed type schema, then it could be returning JAXBElement's here
            // add some code to check to see if the element has a value that is visitable
            // and if so, visit it.
            conditional = conditional._elseif(bean._instanceof(jaxbElementClass));
            addParams(conditional._then()._if(JExpr.invoke(JExpr.cast(jaxbElementClass, bean), "getValue")._instanceof(state.visitable()))._then()
                    .invoke(JExpr.cast(state.visitable(), JExpr.invoke(JExpr.cast(jaxbElementClass, bean), "getValue")), "accept"), vizParam, argParam);
            for (JClass jc : state.directClasses()) {
                // Despite the name below, _elseif doesn't actually produce
                // an else if. Instead, it produces an else with an if
                // in the body. This is syntax issue only, it's semantically
                // equivalent.
                conditional = conditional._elseif(bean._instanceof(jc));
                conditional._then().invoke(vizParam, "visit").arg(JExpr.cast(jc, bean));
            }
        }

        @Override
        public void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            traverseBlock._if(JExpr.invoke(beanParam, getter)._instanceof(state.visitable()))._then().invoke(JExpr.cast(state.visitable(), JExpr.invoke(beanParam, getter)), "accept").arg(vizParam);
        }

    },
    DIRECT {
        @Override
        public void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            // should prob throw an error here. I don't think we should ever have
            // a jaxb element w/ an external class
        }

        @Override
        public void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            // should prob throw an error here. I don't think we should ever have
            // a jaxb element w/ an external class
        }

        @Override
        public void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            JForEach forEach = traverseBlock.forEach(rawType.getTypeParameters().get(0), "bean", JExpr.invoke(beanParam, getter));
            JBlock body = forEach.body();
            body.invoke(vizParam, "visit").arg(forEach.var());
        }

        @Override
        public void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options) {
            traverseBlock._if(
                            JExpr.invoke(beanParam, getter).ne(JExpr._null()))._then()
                    .invoke(vizParam, "visit").arg(JExpr.invoke(beanParam, getter));
        }

    };

    public abstract void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options);

    public abstract void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options);

    public abstract void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options);

    public abstract void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JVar argParam, AllInterfacesCreated state, CodeGenOptions options);

    private static void addParams(JInvocation invocation, JVar vizParam, JVar argParam) {
        if (argParam == null) {
            invocation.arg(vizParam);
            return;
        }
        invocation.arg(vizParam).arg(argParam);
    }
}
