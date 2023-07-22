package com.massfords.jaxb.codegen;

import com.massfords.jaxb.codegen.creators.Utils;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JGenerable;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.bean.field.UntypedListField;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mford
 */
public final class ClassDiscoverer {

    private ClassDiscoverer() {
    }

    /**
     * Finds all external class references
     *
     * @param outline root of the generated code
     * @param classes set of generated classes
     * @return set of external classes
     */
    public static Set<JClass> discoverDirectClasses(Outline outline, Collection<? extends ClassOutline> classes) {

        Set<String> directClassNames = new LinkedHashSet<>();
        classes.forEach(classOutline -> {
            // for each field, if it's a bean, then visit it
            List<FieldOutline> fields = findAllDeclaredAndInheritedFields(classOutline);
            fields.forEach(fieldOutline -> {
                JType rawType = fieldOutline.getRawType();
                CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
                boolean isCollection = propertyInfo.isCollection();
                if (isCollection) {
                    JClass collClazz = (JClass) rawType;
                    JClass collType = collClazz.getTypeParameters().get(0);
                    addIfDirectClass(directClassNames, collType);
                } else {
                    addIfDirectClass(directClassNames, rawType);
                }
                parseXmlAnnotations(outline, fieldOutline, directClassNames);
            });
        });

        return directClassNames
                .stream()
                .map(cn -> outline.getCodeModel().directClass(cn))
                .collect(Collectors.toCollection(LinkedHashSet::new));

    }

    /**
     * Parse the annotations on the field to see if there is an XmlElements
     * annotation on it. If so, we'll check this annotation to see if it
     * refers to any classes that are external from our code schema compile.
     * If we find any, then we'll add them to our visitor.
     *
     * @param outline       root of the generated code
     * @param field         parses the xml annotations looking for an external class
     * @param directClasses set of direct classes to append to
     */
    private static void parseXmlAnnotations(Outline outline, FieldOutline field, Set<String> directClasses) {
        if (field instanceof UntypedListField) {
            JFieldVar jfv;
            try {
                jfv = (JFieldVar) FieldHack.getField().get(field);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            jfv.annotations().stream()
                    .filter(jau -> Utils.isXmlElements(jau.getAnnotationClass()))
                    .map(jau -> (JAnnotationArrayMember) jau.getAnnotationMembers().get("value"))
                    .flatMap(value -> value.annotations().stream())
                    .forEach(anno -> {
                        if (anno.getAnnotationMembers().get("type") != null) {
                            handleXmlElement(outline, directClasses, anno.getAnnotationMembers().get("type"));
                        } else if (jfv.type().isArray()) {
                            handleXmlElement(outline, directClasses, jfv.type().elementType());
                        } else {
                            handleXmlElement(outline, directClasses, ((JClass) jfv.type()).getTypeParameters().get(0));
                        }
                    });
        }
    }

    /**
     * Handles the extraction of the schema type from the XmlElement
     * annotation. This was surprisingly difficult. Apparently the
     * model doesn't provide access to the annotation we're referring to
     * so I need to print it and read the string back. Even the formatter
     * itself is final!
     *
     * @param outline       root of the generated code
     * @param directClasses set of classes to append to
     * @param type          annotation we're analysing
     */
    private static void handleXmlElement(Outline outline, Set<String> directClasses, JGenerable type) {
        StringWriter sw = new StringWriter();
        JFormatter jf = new JFormatter(new PrintWriter(sw));
        type.generate(jf);
        String s = sw.toString();
        if (s.endsWith(".class")) {
            s = s.substring(0, s.length() - ".class".length());
        }
        if (!s.startsWith("java") && outline.getCodeModel()._getClass(s) == null && !foundWithinOutline(s, outline)) {
            directClasses.add(s);
        }
    }

    private static boolean foundWithinOutline(String s, Outline outline) {
        return outline.getClasses()
                .stream().map(co -> co.implClass.binaryName().replaceAll("\\$", "."))
                .anyMatch(name -> name.equals(s));
    }

    private static void addIfDirectClass(Set<String> directClassNames, JType collType) {
        if (collType.getClass().getName().equals("com.sun.codemodel.JDirectClass")) {
            //Skip if the `direct`class is also available as JDefinedClass (see ISSUE-12).
            if (collType.owner()._getClass(collType.fullName()) == null) {
                directClassNames.add(collType.fullName());
            }
        }
    }

    public static List<FieldOutline> findAllDeclaredAndInheritedFields(ClassOutline classOutline) {
        List<FieldOutline> fields = new LinkedList<>();
        ClassOutline currentClassOutline = classOutline;
        while (currentClassOutline != null) {
            fields.addAll(Arrays.asList(currentClassOutline.getDeclaredFields()));
            currentClassOutline = currentClassOutline.getSuperClass();
        }
        return fields;
    }

    private static final JType[] NONE = new JType[0];

    /**
     * Borrowed this code from jaxb-commons project
     *
     * @param fieldOutline reference to a field
     * @return Getter for the given field or null
     */
    public static JMethod getter(FieldOutline fieldOutline) {
        final JDefinedClass theClass = fieldOutline.parent().implClass;
        final String publicName = fieldOutline.getPropertyInfo().getName(true);
        final JMethod getgetter = theClass.getMethod("get" + publicName, NONE);
        if (getgetter != null) {
            return getgetter;
        } else {
            return theClass
                    .getMethod("is" + publicName, NONE);
        }
    }

    public static JFieldVar field(FieldOutline fieldOutline) {
        final JDefinedClass theClass = fieldOutline.parent().implClass;
        final String privateName = fieldOutline.getPropertyInfo().getName(false);

        return theClass.fields().get(privateName);
    }

    /**
     * Returns all of the concrete classes in the system
     *
     * @param classes collection of classes to examine
     * @return List of concrete classes
     */
    public static List<JClass> allConcreteClasses(Set<ClassOutline> classes) {
        return allConcreteClasses(classes, Collections.emptySet());
    }

    /**
     * Returns all of the concrete classes plus the direct classes passed in
     *
     * @param classes       collection of clases to test to see if they're abstract or concrete
     * @param directClasses set of classes to append to the list of concrete classes
     * @return list of concrete classes
     */
    public static List<JClass> allConcreteClasses(Collection<ClassOutline> classes, Collection<JClass> directClasses) {
        List<JClass> results = new ArrayList<>();
        classes.stream()
                .filter(classOutline -> !classOutline.target.isAbstract())
                .forEach(classOutline -> {
                    JClass implClass = classOutline.implClass;
                    results.add(implClass);
                });
        results.addAll(directClasses);

        return results;
    }
}
