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
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import jakarta.xml.bind.annotation.XmlIDREF;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
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
    private DepthFirstTraverser() {
    }

    public static void createClass(AllInterfacesCreated state, CodeGenOptions options) {
        Outline outline = state.initial().outline();

        // create the class
        JDefinedClass defaultTraverser = outline.getClassFactory().createClass(options.packageForVisitor(),
                "DepthFirstTraverserImpl", null);
        JDefinedClass scratch = outline.getClassFactory().createInterface(options.packageForVisitor(), "scratch", null);
        try {
            final JTypeVar exceptionType = defaultTraverser.generify("E", Throwable.class);
            final JTypeVar argType = options.includeArg() ? defaultTraverser.generify("A") : null;

            JClass narrowedVisitor = state.visitor()
                    .narrow(Stream.of(scratch.generify("?"), exceptionType, argType)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
            JClass narrowedTraverser = state.traverser().narrow(
                    Stream.of(exceptionType, argType)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
            defaultTraverser._implements(narrowedTraverser);

            SharedDepthFirstTraversalContext models = ImmutableSharedDepthFirstTraversalContext.builder()
                    .argType(Optional.ofNullable(argType))
                    .defaultTraverser(defaultTraverser)
                    .narrowedVisitor(narrowedVisitor)
                    .exceptionType(exceptionType)
                    .narrowedTraverser(narrowedTraverser)
                    .options(options)
                    .state(state)
                    .build();

            annotateGenerated(defaultTraverser, options);

            state.initial().allClasses().stream()
                    .filter(classOutline -> !classOutline.target.isAbstract())
                    .forEach(classOutline -> {
                        // add the bean to the traverserImpl
                        traverseGeneratedBean(models, classOutline);
                    });

            state.initial().directClasses().forEach(dc -> {
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

    private static void traverseGeneratedBean(SharedDepthFirstTraversalContext shared,
                                              ClassOutline classOutline) {
        String traverseMethodName = shared.options().traverseMethodNamer().apply(classOutline.implClass.name());
        JMethod traverseMethodImpl = shared.defaultTraverser().method(
                JMod.PUBLIC, void.class, traverseMethodName);
        traverseMethodImpl._throws(shared.exceptionType());
        JVar beanParam = traverseMethodImpl.param(classOutline.implClass, "aBean");
        JVar vizParam = traverseMethodImpl.param(shared.narrowedVisitor(), "aVisitor");
        Optional<JVar> argParam = shared.argType().map(argType -> traverseMethodImpl.param(argType, "arg"));
        traverseMethodImpl.annotate(Override.class);
        JBlock traverseBlock = traverseMethodImpl.body();
        ImmutableTraversalContext.Builder builder = ImmutableTraversalContext.builder();
        TraversalContext tContext = builder
                .shared(shared)
                .traverseBlock(traverseBlock)
                .vizParam(vizParam)
                .argParam(argParam)
                .beanParam(beanParam)
                .build();
        // for each field, if it's a bean, then visit it
        findAllDeclaredAndInheritedFields(classOutline)
                .forEach(fieldOutline -> {
                    JType rawType = fieldOutline.getRawType();
                    JMethod getter = ClassDiscoverer.getter(fieldOutline);
                    if (getter != null && !(shared.options().noIdrefTraversal() && isIdrefField(fieldOutline))) {
                        boolean isJAXBElement = isJAXBElement(getter.type(), shared.options());
                        CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
                        boolean isCollection = propertyInfo.isCollection();
                        if (isCollection) {
                            JClass collClazz = (JClass) rawType;
                            JClass collType = collClazz.getTypeParameters().get(0);
                            TraversableCodeGenStrategy t = getTraversableStrategy(tContext, collType);
                            if (collType.name().startsWith("JAXBElement")) {
                                t.jaxbElementCollection(tContext, collType, getter);
                            } else {
                                t.collection(tContext, (JClass) rawType, getter);
                            }
                        } else {
                            TraversableCodeGenStrategy t = getTraversableStrategy(
                                    tContext, rawType);
                            if (isJAXBElement) {
                                t.jaxbElement(tContext, (JClass) rawType, getter);
                            } else {
                                t.bean(tContext, getter);
                            }
                        }
                    }
                });
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
     */
    private static TraversableCodeGenStrategy getTraversableStrategy(TraversalContext traversalContext, JType rawType) {

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
        } else if (traversalContext.shared().state().visitable().isAssignableFrom(clazz)) {
            // it's a real type. if it's one of ours, then it'll be assignable from Visitable
            return TraversableCodeGenStrategy.VISITABLE;
        } else if (traversalContext.shared().state().initial().directClassesByName().containsKey(name)) {
            return TraversableCodeGenStrategy.DIRECT;
        } else {
            return TraversableCodeGenStrategy.NO;
        }
    }
}

