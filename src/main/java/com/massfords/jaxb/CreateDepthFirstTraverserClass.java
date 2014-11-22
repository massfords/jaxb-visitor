package com.massfords.jaxb;

import com.sun.codemodel.*;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

import javax.xml.bind.JAXBElement;

import java.util.*;

/**
 * Creates an implementation of the traverser that traverses the beans in depth first order 
 * according to the order returned from the field iterator within the code model.
 * 
 * The default traverser will traverse each of the child beans that are not null. 
 * 
 * @author markford
 */
public class CreateDepthFirstTraverserClass extends CodeCreator {
    
    private JDefinedClass visitor;
    private JDefinedClass traverser;
    private JDefinedClass visitable;

    public CreateDepthFirstTraverserClass(JDefinedClass visitor, JDefinedClass traverser, JDefinedClass visitable,
                                          Outline outline, JPackage jPackageackage) {
        super(outline, jPackageackage);
        this.visitor = visitor;
        this.traverser = traverser;
        this.visitable = visitable;
    }

    @Override
    protected void run(Set<ClassOutline> classes) {
    	
    	// create the class
        JDefinedClass defaultTraverser = getOutline().getClassFactory().createClass(getPackage(),
                "DepthFirstTraverserImpl", null);
        JDefinedClass scratch = getOutline().getClassFactory().createInterface(getPackage(), "scratch", null);
        
        final JTypeVar exceptionType = defaultTraverser.generify("E", Throwable.class);
        
        JClass narrowedVisitor = visitor.narrow(scratch.generify("?")).narrow(exceptionType);
        JClass narrowedTraverser = traverser.narrow(exceptionType);
        defaultTraverser._implements(narrowedTraverser);

        setOutput( defaultTraverser );
        
        for(ClassOutline classOutline : classes) {
            if (classOutline.target.isAbstract()) {
                continue;
            }
            // add the bean to the traverserImpl
            JMethod traverseMethodImpl = defaultTraverser.method(JMod.PUBLIC, void.class, "traverse");
            traverseMethodImpl._throws(exceptionType);
            JVar beanParam = traverseMethodImpl.param(classOutline.implClass, "aBean");
            JVar vizParam = traverseMethodImpl.param(narrowedVisitor, "aVisitor");
            traverseMethodImpl.annotate(Override.class);
            JBlock traverseBlock = traverseMethodImpl.body();
            // for each field, if it's a bean, then visit it
            List<FieldOutline> fields = findAllDeclaredAndInheritedFields(classOutline);
            for(FieldOutline fieldOutline : fields) {
                JType rawType = fieldOutline.getRawType();
                JMethod getter = getter(fieldOutline);
                boolean isJAXBElement = isJAXBElement(getter.type());
                CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
                boolean isCollection = propertyInfo.isCollection();
                if (isCollection) {
                    JClass collClazz = (JClass) rawType;
                    JClass collType = collClazz.getTypeParameters().get(0);
                    Traversable t = isTraversable(collType);
                    if (collType.name().startsWith("JAXBElement")) {
                        if (t == Traversable.YES) {
                            // YES means we just have to test for a null instance.
                            // We don't need to do a cast because the type is definitely a Visitable
                            JForEach forEach = traverseBlock.forEach(collType, "obj", JExpr.invoke(beanParam, getter));
                            forEach.body()._if(JExpr.ref("obj").invoke("getValue").ne(JExpr._null()))._then().invoke(JExpr.ref("obj").invoke("getValue"), "accept").arg(vizParam);
                        } else if (t == Traversable.MAYBE) {
                            // MAYBE means we just have to test for an instanceof Visitable and cast where it's true
                            // This is often the case when elements share a common type.
                            // I don't like this approach but it's a necessary evil since you can't always change the
                            // schema to better support codegen
                            JForEach forEach = traverseBlock.forEach(collType, "obj", JExpr.invoke(beanParam, getter));
                            forEach.body()._if(JExpr.ref("obj").invoke("getValue")._instanceof(visitable))._then().invoke(JExpr.cast(visitable, JExpr.ref("obj").invoke("getValue")), "accept").arg(vizParam);
                        }
                    } else {
                        if (t == Traversable.YES) {
                            // YES means we just have to test for a null instance.
                            // We don't need to do a cast because the type is definitely a Visitable
                            JForEach forEach = traverseBlock.forEach(((JClass)rawType).getTypeParameters().get(0), "bean", JExpr.invoke(beanParam, getter));
                            forEach.body().invoke(JExpr.ref("bean"), "accept").arg(vizParam);
                        } else if (t == Traversable.MAYBE) {
                            // MAYBE means we just have to test for an instanceof Visitable and cast where it's true
                            // This is often the case when elements share a common type.
                            // I don't like this approach but it's a necessary evil since you can't always change the
                            // schema to better support codegen
                            JForEach forEach = traverseBlock.forEach(((JClass)rawType).getTypeParameters().get(0), "bean", JExpr.invoke(beanParam, getter));
                            forEach.body()._if(JExpr.ref("bean")._instanceof(visitable))._then()
                                    .invoke(JExpr.cast(visitable, JExpr.ref("bean")), "accept").arg(vizParam);
                        }

                    }
                } else if (isJAXBElement) {
                    Traversable t = isTraversable(rawType);
                    if (t== Traversable.YES) {
                        // YES means we just have to test for a null instance.
                        // We don't need to do a cast because the type is definitely a Visitable
                        traverseBlock._if(
                        JExpr.invoke(beanParam, getter).ne(JExpr._null()))._then()
                        .invoke(JExpr.invoke(beanParam, getter).invoke("getValue"), "accept").arg(vizParam);
                    } else if (t == Traversable.MAYBE) {
                        // MAYBE means we just have to test for an instanceof Visitable and cast where it's true
                        // This is often the case when elements share a common type.
                        // I don't like this approach but it's a necessary evil since you can't always change the
                        // schema to better support codegen
                        traverseBlock._if(
                                JExpr.invoke(beanParam, getter).ne(JExpr._null())
                                .cand(
                                JExpr.invoke(beanParam, getter).invoke("getValue")._instanceof(visitable))  )._then()
                                .invoke(JExpr.cast(visitable, JExpr.invoke(beanParam, getter).invoke("getValue")), "accept").arg(vizParam);
                    }
                } else {
                    Traversable t = isTraversable(rawType);
                    if (t== Traversable.YES) {
                        // YES means we just have to test for a null instance.
                        // We don't need to do a cast because the type is definitely a Visitable
                        traverseBlock._if(JExpr.invoke(beanParam, getter).ne(JExpr._null()))._then().invoke(JExpr.invoke(beanParam, getter), "accept").arg(vizParam);
                    } else if (t == Traversable.MAYBE) {
                        // MAYBE means we just have to test for an instanceof Visitable and cast where it's true
                        // This is often the case when elements share a common type.
                        // I don't like this approach but it's a necessary evil since you can't always change the
                        // schema to better support codegen
                        traverseBlock._if(JExpr.invoke(beanParam, getter)._instanceof(visitable))._then().invoke(JExpr.cast(visitable,JExpr.invoke(beanParam, getter)), "accept").arg(vizParam);
                    }
                }
            }
        }
        getPackage().remove(scratch);
    }

    protected List<FieldOutline> findAllDeclaredAndInheritedFields(ClassOutline classOutline) {
        List<FieldOutline> fields = new LinkedList<>();
        ClassOutline currentClassOutline = classOutline;
        while(currentClassOutline != null) {
            fields.addAll(Arrays.asList(currentClassOutline.getDeclaredFields()));
            currentClassOutline = currentClassOutline.getSuperClass();
        }
        return fields;
    }

    /**
     * Returns true if the type is a JAXBElement. In the case of JAXBElements, we want to traverse its
     * underlying value as opposed to the JAXBElement.
     * @param type
     */
    private boolean isJAXBElement(JType type) {
        //noinspection RedundantIfStatement
        if (type.fullName().startsWith(JAXBElement.class.getName())) {
    		return true;
    	}
		return false;
	}

    /**
     * enum that reports whether a bean property is traversable.
     * YES = it's definitely traversable and we just need to do a null check
     * NO = it's definitely NOT traversable and we should skip the bean property
     * MAYBE = it's a JAXBElement<?> or Object so we'll test the value with an instanceof and perform a cast
     */
    public enum Traversable {YES, NO, MAYBE}

	/**
	 * Tests to see if the rawType is traversable
     *
     * @return Traversable YES, NO, MAYBE
	 * 
	 * @param rawType
	 */
	private Traversable isTraversable(JType rawType) {

        if (rawType.isPrimitive()) {
            // primitive types are never traversable
            return Traversable.NO;
        }
        JClass clazz = (JClass) rawType;
        if (clazz.isParameterized()) {
            // if it's a parameterized type, then we should use the parameter
            clazz = clazz.getTypeParameters().get(0);
            if (clazz.name().startsWith("?")) {
                // when we have a wildcard we should use the bounding class.
                clazz = clazz._extends();
            }
        }
        String name = clazz.fullName();
        if (name.equals("java.lang.Object")) {
            // it could be anything so we'll test with an instanceof in the generated code
            return Traversable.MAYBE;
        } if (clazz.isInterface()) { 
            // if it is an interface (like Serializable) it could also be anything
            // handle it like java.lang.Object
            return  Traversable.MAYBE;
        } else {
            // it's a real type. if it's one of ours, then it'll be assignable from Visitable
            return visitable.isAssignableFrom(clazz) ? Traversable.YES : Traversable.NO;
        }
    }

    private static final JType[] NONE = new JType[0];
    /**
     * Borrowed this code from jaxb-commons project
     * 
     * @param fieldOutline
     */
    private static JMethod getter(FieldOutline fieldOutline) {
        final JDefinedClass theClass = fieldOutline.parent().implClass;
        final String publicName = fieldOutline.getPropertyInfo().getName(true);
        final JMethod getgetter = theClass.getMethod("get" + publicName, NONE);
        if (getgetter != null) {
            return getgetter;
        } else {
            final JMethod isgetter = theClass
                    .getMethod("is" + publicName, NONE);
            if (isgetter != null) {
                return isgetter;
            } else {
                return null;
            }
        }
    }
}

