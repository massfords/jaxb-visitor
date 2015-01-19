package com.massfords.jaxb;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

/**
 * enum that reports whether a bean property is traversable.
 * YES = it's definitely traversable and we just need to do a null check
 * NO = it's definitely NOT traversable and we should skip the bean property
 * MAYBE = it's a JAXBElement<?> or Object so we'll test the value with an
 *         instanceof and perform a cast
 */
public enum TraversableCodeGenStrategy {
    /**
     * YES means we just have to test for a null instance.
     * We don't need to do a cast because the type is definitely a Visitable
     */
    YES{
        @Override
        public void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            JForEach forEach = traverseBlock.forEach(collType, "obj", JExpr.invoke(beanParam, getter));
            forEach.body()._if(JExpr.ref("obj").invoke("getValue").ne(JExpr._null()))._then().invoke(JExpr.ref("obj").invoke("getValue"), "accept").arg(vizParam);
        }

        @Override
        public void collection(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
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
        public void collection(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {

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
        public void collection(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            JForEach forEach = traverseBlock.forEach(rawType.getTypeParameters().get(0), "bean", JExpr.invoke(beanParam, getter));
            JBlock body = forEach.body();
            body._if(JExpr.ref("bean")._instanceof(visitable))._then()
                    .invoke(JExpr.cast(visitable, JExpr.ref("bean")), "accept").arg(vizParam);
        }

        @Override
        public void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable) {
            traverseBlock._if(JExpr.invoke(beanParam, getter)._instanceof(visitable))._then().invoke(JExpr.cast(visitable,JExpr.invoke(beanParam, getter)), "accept").arg(vizParam);
        }

    };

    public abstract void jaxbElementCollection(JBlock traverseBlock, JClass collType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable);
    public abstract void jaxbElement(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable);
    public abstract void collection(JBlock traverseBlock, JClass rawType, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable);
    public abstract void bean(JBlock traverseBlock, JVar beanParam, JMethod getter, JVar vizParam, JDefinedClass visitable);
}
