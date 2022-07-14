package com.massfords.jaxb.codegen.creators.classes;

import com.massfords.jaxb.codegen.AllInterfacesCreated;
import com.massfords.jaxb.codegen.ClassDiscoverer;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import jakarta.xml.bind.annotation.XmlIDREF;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.massfords.jaxb.codegen.ClassDiscoverer.findAllDeclaredAndInheritedFields;
import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;
import static com.massfords.jaxb.codegen.creators.Utils.isJAXBElement;


/**
 * Creates an implementation of the traverser that traverses the beans in depth first order
 * according to the order returned from the field iterator within the code model.
 * <p>
 * The default traverser will traverse each of the child beans that are not null.
 *
 * @author markford
 */
public final class DepthFirstTraverser {

    public static void createClass(AllInterfacesCreated codeGenState, CodeGenOptions options) {
        Outline outline = codeGenState.outline();

        // create the class
        JDefinedClass defaultTraverser = outline.getClassFactory().createClass(options.packageForVisitor(),
                "DepthFirstTraverserImpl", null);
        JDefinedClass scratch = outline.getClassFactory().createInterface(options.packageForVisitor(), "scratch", null);
        try {
            final JTypeVar exceptionType = defaultTraverser.generify("E", Throwable.class);
            final JTypeVar argType = options.includeArg() ? defaultTraverser.generify("A") : null;

            JClass narrowedVisitor = codeGenState.visitor()
                    .narrow(Stream.of(scratch.generify("?"), exceptionType, argType)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
            JClass narrowedTraverser = codeGenState.traverser().narrow(
                    Stream.of(exceptionType, argType)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
            defaultTraverser._implements(narrowedTraverser);

            annotateGenerated(defaultTraverser, options);

            Map<String, JClass> dcMap = codeGenState.directClasses()
                    .stream()
                    .collect(Collectors.toMap(JType::fullName, Function.identity()));


            codeGenState.allClasses().stream()
                    .filter(classOutline -> !classOutline.target.isAbstract())
                    .forEach(classOutline -> {
                        // add the bean to the traverserImpl
                        JMethod traverseMethodImpl;
                        String traverseMethodName = options.traverseMethodNamer().apply(classOutline.implClass.name());
                        traverseMethodImpl = defaultTraverser.method(JMod.PUBLIC, void.class, traverseMethodName);
                        traverseMethodImpl._throws(exceptionType);
                        JVar beanParam = traverseMethodImpl.param(classOutline.implClass, "aBean");
                        JVar vizParam = traverseMethodImpl.param(narrowedVisitor, "aVisitor");
                        JVar argParam = argType != null ? traverseMethodImpl.param(argType, "arg") : null;
                        traverseMethodImpl.annotate(Override.class);
                        JBlock traverseBlock = traverseMethodImpl.body();
                        // for each field, if it's a bean, then visit it
                        findAllDeclaredAndInheritedFields(classOutline).forEach(fieldOutline -> {
                            JType rawType = fieldOutline.getRawType();
                            JMethod getter = ClassDiscoverer.getter(fieldOutline);
                            if (getter != null && !(options.noIdrefTraversal() && isIdrefField(fieldOutline))) {
                                boolean isJAXBElement = isJAXBElement(getter.type(), options);
                                CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
                                boolean isCollection = propertyInfo.isCollection();
                                if (isCollection) {
                                    JClass collClazz = (JClass) rawType;
                                    JClass collType = collClazz.getTypeParameters().get(0);
                                    TraversableCodeGenStrategy t = getTraversableStrategy(collType, dcMap, codeGenState);
                                    if (collType.name().startsWith("JAXBElement")) {
                                        t.jaxbElementCollection(traverseBlock, collType, beanParam, getter, vizParam, argParam, codeGenState, options);
                                    } else {
                                        t.collection(outline, traverseBlock, (JClass) rawType, beanParam, getter, vizParam, argParam, codeGenState, options);
                                    }
                                } else {
                                    TraversableCodeGenStrategy t = getTraversableStrategy(rawType, dcMap, codeGenState);
                                    if (isJAXBElement) {
                                        t.jaxbElement(traverseBlock, (JClass) rawType, beanParam, getter, vizParam, argParam, codeGenState, options);
                                    } else {
                                        t.bean(traverseBlock, beanParam, getter, vizParam, argParam, codeGenState, options);
                                    }
                                }
                            }
                        });
                    });
            codeGenState.directClasses().forEach(dc -> {
                JMethod traverseMethodImpl = defaultTraverser.method(JMod.PUBLIC, void.class, "traverse");
                traverseMethodImpl._throws(exceptionType);
                traverseMethodImpl.param(dc, "aBean");
                traverseMethodImpl.param(narrowedVisitor, "aVisitor");
                if (argType != null) {
                    traverseMethodImpl.param(argType, "arg");
                }
                traverseMethodImpl.annotate(Override.class);
                JBlock traverseBlock = traverseMethodImpl.body();
                String[] source = {"// details about %s are not known at compile time.",
                        "// For now, applications using external classes will have to",
                        "// implement their own traversal logic."};
                Arrays.stream(source).forEach(s -> traverseBlock.directStatement(String.format(s, dc.fullName())));
            });
        } finally {
            options.packageForVisitor().remove(scratch);
        }
    }

    private static boolean isIdrefField(FieldOutline fieldOutline) {
        JFieldVar field = ClassDiscoverer.field(fieldOutline);
        if (field == null) {
            return false;
        }
        return field.annotations().stream().anyMatch(use -> {
            if (use.getAnnotationClass().fullName().equals(XmlIDREF.class.getName())) {
                return true;
            }
            return use.getAnnotationClass().fullName().equals("javax.xml.bind.annotation.XmlIDREF");
        });
    }

    /**
     * Tests to see if the rawType is traversable
     *
     * @param rawType       type to inspect
     * @param directClasses used to filter direct classes
     * @return TraversableCodeGenStrategy VISITABLE, NO, MAYBE, DIRECT
     */
    private static TraversableCodeGenStrategy getTraversableStrategy(JType rawType, Map<String, JClass> directClasses, AllInterfacesCreated state) {

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
            return TraversableCodeGenStrategy.MAYBE;
        } else if (state.visitable().isAssignableFrom(clazz)) {
            // it's a real type. if it's one of ours, then it'll be assignable from Visitable
            return TraversableCodeGenStrategy.VISITABLE;
        } else if (directClasses.containsKey(name)) {
            return TraversableCodeGenStrategy.DIRECT;
        } else {
            return TraversableCodeGenStrategy.NO;
        }
    }

}

