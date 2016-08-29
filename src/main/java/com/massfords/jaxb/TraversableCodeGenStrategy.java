package com.massfords.jaxb;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.outline.Outline;

import javax.xml.bind.JAXBElement;
import java.util.Set;

/**
 * enum that reports whether a bean property is traversable.
 * VISITABLE = it's definitely traversable and we just need to do a null check
 * NO = it's definitely NOT traversable and we should skip the bean property
 * MAYBE = it's a JAXBElement&lt;?&gt; or Object so we'll test the value with an
 *         instanceof and perform a cast
 * DIRECT = It's one of the externally mapped classes which will
 *          be on the Visitor interface but doesn't have an accept
 *          method.
 */
enum TraversableCodeGenStrategy {
    /**
     * VISITABLE means we just have to test for a null instance.
     * We don't need to do a cast because the type is definitely a Visitable
     */
    VISITABLE {
        @Override
        public void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            JForEach forEach = traverseBlock.forEach(collType, "obj", JExpr.invoke(beanParam, getter));
            forEach.body()._if(JExpr.ref("obj").invoke("getValue").ne(JExpr._null()))._then().invoke(JExpr.ref("obj").invoke("getValue"), "accept").arg(vizParam);
        }

        @Override
        public void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable, Set<JClass> directClasses) {
            JForEach forEach = traverseBlock.forEach(rawType.getTypeParameters().get(0), "bean", JExpr.invoke(beanParam, getter));
            forEach.body().invoke(JExpr.ref("bean"), "accept").arg(vizParam);
        }

        @Override
        public void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            traverseBlock._if(JExpr.invoke(beanParam, getter).ne(JExpr._null()))._then().invoke(JExpr.invoke(beanParam, getter), "accept").arg(vizParam);
        }

        @Override
        public void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            traverseBlock._if(
                    JExpr.invoke(beanParam, getter).ne(JExpr._null()))._then()
                    .invoke(JExpr.invoke(beanParam, getter).invoke("getValue"), "accept").arg(vizParam);
        }
    },
    /**
     * All no-op methods here. We're not going to traverse into the type because it's
     * either a primitive type or not anything we can traverse into.
     */
    NO {
        @Override
        public void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {

        }

        @Override
        public void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {

        }

        @Override
        public void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable, Set<JClass> directClasses) {

        }

        @Override
        public void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {

        }
    },
    /**
     * MAYBE means we just have to test for an instanceof Visitable and cast where it's true
     * This is often the case when elements share a common type.
     * I don't like this approach but it's a necessary evil since you can't always change the
     * schema to better support codegen
     */
    MAYBE {
        @Override
        public void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            JForEach forEach = traverseBlock.forEach(collType, "obj", JExpr.invoke(beanParam, getter));
            forEach.body()._if(JExpr.ref("obj").invoke("getValue")._instanceof(visitable))._then().invoke(JExpr.cast(visitable, JExpr.ref("obj").invoke("getValue")), "accept").arg(vizParam);
        }

        @Override
        public void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            traverseBlock._if(
                    JExpr.invoke(beanParam, getter).ne(JExpr._null())
                            .cand(
                                    JExpr.invoke(beanParam, getter).invoke("getValue")._instanceof(visitable))  )._then()
                    .invoke(JExpr.cast(visitable, JExpr.invoke(beanParam, getter).invoke("getValue")), "accept").arg(vizParam);
        }

        @Override
        public void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable, Set<JClass> directClasses) {

            JClass jaxbElementClass = outline.getCodeModel().ref(JAXBElement.class).narrow(outline.getCodeModel().ref(Object.class).wildcard());

            JForEach forEach = traverseBlock.forEach(rawType.getTypeParameters().get(0), "bean", JExpr.invoke(beanParam, getter));
            JBlock body = forEach.body();
            JFieldRef bean = JExpr.ref("bean");
            JConditional conditional = body._if(bean._instanceof(visitable));
            conditional._then().invoke(JExpr.cast(visitable, bean), "accept").arg(vizParam);

            // if it's a mixed type schema, then it could be returning JAXBElement's here
            // add some code to check to see if the element has a value that is visitable
            // and if so, visit it.
            conditional = conditional._elseif(bean._instanceof(jaxbElementClass));
            conditional._then()._if(JExpr.invoke(JExpr.cast(jaxbElementClass, bean), "getValue")._instanceof(visitable))._then()
                    .invoke(JExpr.cast(visitable, JExpr.invoke(JExpr.cast(jaxbElementClass, bean), "getValue")), "accept").arg(vizParam);
            for(JClass jc : directClasses) {
                // Despite the name below, _elseif doesn't actually produce
                // an else if. Instead, it produces an else with an if
                // in the body. This is syntax issue only, it's semantically
                // equivalent.
                conditional = conditional._elseif(bean._instanceof(jc));
                conditional._then().invoke(vizParam, "visit").arg(JExpr.cast(jc, bean));
            }
        }

        @Override
        public void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            traverseBlock._if(JExpr.invoke(beanParam, getter)._instanceof(visitable))._then().invoke(JExpr.cast(visitable,JExpr.invoke(beanParam, getter)), "accept").arg(vizParam);
        }

    },
    DIRECT {
        @Override
        public void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            // should prob throw an error here. I don't think we should ever have
            // a jaxb element w/ an external class
        }

        @Override
        public void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            // should prob throw an error here. I don't think we should ever have
            // a jaxb element w/ an external class
        }

        @Override
        public void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable, Set<JClass> directClasses) {
            JForEach forEach = traverseBlock.forEach(rawType.getTypeParameters().get(0), "bean", JExpr.invoke(beanParam, getter));
            JBlock body = forEach.body();
            body.invoke(vizParam, "visit").arg(forEach.var());
        }

        @Override
        public void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            traverseBlock._if(
                    JExpr.invoke(beanParam, getter).ne(JExpr._null()))._then()
                    .invoke(vizParam, "visit").arg(JExpr.invoke(beanParam, getter));
        }

    };

    public abstract void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable);
    public abstract void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable);
    public abstract void collection(Outline outline, JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable, Set<JClass> directClasses);
    public abstract void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable);
}
