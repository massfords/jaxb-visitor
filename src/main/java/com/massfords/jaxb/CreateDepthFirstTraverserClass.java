package com.massfords.jaxb;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.massfords.jaxb.ClassDiscoverer.findAllDeclaredAndInheritedFields;


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
                                          Outline outline,
                                          JPackage jPackageackage) {
        super(outline, jPackageackage);
        this.visitor = visitor;
        this.traverser = traverser;
        this.visitable = visitable;
    }

    @Override
    protected void run(Set<ClassOutline> classes, Set<JClass> directClasses) {
    	
    	// create the class
        JDefinedClass defaultTraverser = getOutline().getClassFactory().createClass(getPackage(),
                "DepthFirstTraverserImpl", null);
        JDefinedClass scratch = getOutline().getClassFactory().createInterface(getPackage(), "scratch", null);
        try {
            final JTypeVar exceptionType = defaultTraverser.generify("E", Throwable.class);

            JClass narrowedVisitor = visitor.narrow(scratch.generify("?")).narrow(exceptionType);
            JClass narrowedTraverser = traverser.narrow(exceptionType);
            defaultTraverser._implements(narrowedTraverser);

            setOutput(defaultTraverser);

            Map<String, JClass> dcMap = new HashMap<>();
            for (JClass dc : directClasses) {
                dcMap.put(dc.fullName(), dc);
            }

            for (ClassOutline classOutline : classes) {
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
                for (FieldOutline fieldOutline : fields) {
                    JType rawType = fieldOutline.getRawType();
                    JMethod getter = ClassDiscoverer.getter(fieldOutline);
                    if (getter != null) {
                        boolean isJAXBElement = ClassDiscoverer.isJAXBElement(getter.type());
                        CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
                        boolean isCollection = propertyInfo.isCollection();
                        if (isCollection) {
                            JClass collClazz = (JClass) rawType;
                            JClass collType = collClazz.getTypeParameters().get(0);
                            TraversableCodeGenStrategy t = getTraversableStrategy(collType, dcMap);
                            if (collType.name().startsWith("JAXBElement")) {
                                t.jaxbElementCollection(traverseBlock, collType, beanParam, getter, vizParam, visitable);
                            } else {
                                t.collection(traverseBlock, (JClass) rawType, beanParam, getter, vizParam, visitable, directClasses);
                            }
                        } else {
                            TraversableCodeGenStrategy t = getTraversableStrategy(rawType, dcMap);
                            if (isJAXBElement) {
                                t.jaxbElement(traverseBlock, (JClass) rawType, beanParam, getter, vizParam, visitable);
                            } else {
                                t.bean(traverseBlock, beanParam, getter, vizParam, visitable);
                            }
                        }
                    }
                }
            }

            for (JClass dc : directClasses) {
                JMethod traverseMethodImpl = defaultTraverser.method(JMod.PUBLIC, void.class, "traverse");
                traverseMethodImpl._throws(exceptionType);
                traverseMethodImpl.param(dc, "aBean");
                traverseMethodImpl.param(narrowedVisitor, "aVisitor");
                traverseMethodImpl.annotate(Override.class);
                JBlock traverseBlock = traverseMethodImpl.body();
                String[] source = {"// details about %s are not known at compile time.",
                        "// For now, applications using external classes will have to",
                        "// implement their own traversal logic."};
                for (String s : source) {
                    traverseBlock.directStatement(String.format(s, dc.fullName()));
                }

            }
        } finally {
            getPackage().remove(scratch);
        }
    }

    /**
	 * Tests to see if the rawType is traversable
     *
     * @return TraversableCodeGenStrategy VISITABLE, NO, MAYBE, DIRECT
	 * 
	 * @param rawType
	 */
	private TraversableCodeGenStrategy getTraversableStrategy(JType rawType, Map<String,JClass> directClasses) {

        if (rawType.isPrimitive()) {
            // primitive types are never traversable
            return TraversableCodeGenStrategy.NO;
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
            return TraversableCodeGenStrategy.MAYBE;
        } else if (clazz.isInterface()) {
            // if it is an interface (like Serializable) it could also be anything
            // handle it like java.lang.Object
            return  TraversableCodeGenStrategy.MAYBE;
        } else if (visitable.isAssignableFrom(clazz)) {
            // it's a real type. if it's one of ours, then it'll be assignable from Visitable
            return TraversableCodeGenStrategy.VISITABLE;
        } else if (directClasses.containsKey(name)) {
            return TraversableCodeGenStrategy.DIRECT;
        } else {
            return TraversableCodeGenStrategy.NO;
        }
    }

}

