package com.massfords.jaxb;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElements;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author mford
 */
public class ClassDiscoverer {

    public static Set<JClass> discoverDirectClasses(Outline outline, Set<ClassOutline> classes) throws IllegalAccessException {

        Set<String> directClassNames = new LinkedHashSet<>();
        for(ClassOutline classOutline : classes) {
            // for each field, if it's a bean, then visit it
            List<FieldOutline> fields = findAllDeclaredAndInheritedFields(classOutline);
            for(FieldOutline fieldOutline : fields) {
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
            }
        }

        Set<JClass> direct = new LinkedHashSet<>();
        for(String cn : directClassNames) {
            direct.add(outline.getCodeModel().directClass(cn));
        }

        return direct;

    }

    /**
     * Parse the annotations on the field to see if there is an XmlElements
     * annotation on it. If so, we'll check this annotation to see if it
     * refers to any classes that are external from our code schema compile.
     * If we find any, then we'll add them to our visitor.
     * @param outline
     * @param field
     * @param directClasses
     * @throws IllegalAccessException
     */
    private static void parseXmlAnnotations(Outline outline, FieldOutline field, Set<String> directClasses) throws IllegalAccessException {
        if (field instanceof UntypedListField) {
            JFieldVar jfv = (JFieldVar) FieldHack.listField.get(field);
            for(JAnnotationUse jau : jfv.annotations()) {
                JClass jc = jau.getAnnotationClass();
                if (jc.fullName().equals(XmlElements.class.getName())) {
                    JAnnotationArrayMember value = (JAnnotationArrayMember) jau.getAnnotationMembers().get("value");
                    for(JAnnotationUse anno : value.annotations()) {
                        handleXmlElement(outline, directClasses, anno.getAnnotationMembers().get("type"));
                    }
                }
            }
        }
    }

    /**
     * Handles the extraction of the schema type from the XmlElement
     * annotation. This was surprisingly difficult. Apparently the
     * model doesn't provide access to the annotation we're referring to
     * so I need to print it and read the string back. Event the formatter
     * itself is final!
     * @param outline
     * @param directClasses
     * @param type
     */
    private static void handleXmlElement(Outline outline, Set<String> directClasses, JAnnotationValue type) {
        StringWriter sw = new StringWriter();
        JFormatter jf = new JFormatter(new PrintWriter(sw));
        type.generate(jf);
        String s = sw.toString();
        s = s.substring(0, s.length()-".class".length());
        if (!s.startsWith("java") && outline.getCodeModel()._getClass(s) == null) {
            directClasses.add(s);
        }
    }

    private static void addIfDirectClass(Set<String> directClassNames, JType collType) {
        if (collType.getClass().getName().equals("com.sun.codemodel.JDirectClass")) {
            directClassNames.add(collType.fullName());
        }
    }

    protected static List<FieldOutline> findAllDeclaredAndInheritedFields(ClassOutline classOutline) {
        List<FieldOutline> fields = new LinkedList<>();
        ClassOutline currentClassOutline = classOutline;
        while(currentClassOutline != null) {
            fields.addAll(Arrays.asList(currentClassOutline.getDeclaredFields()));
            currentClassOutline = currentClassOutline.getSuperClass();
        }
        return fields;
    }

    private static final JType[] NONE = new JType[0];
    /**
     * Borrowed this code from jaxb-commons project
     *
     * @param fieldOutline
     */
    protected static JMethod getter(FieldOutline fieldOutline) {
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

    /**
     * Returns true if the type is a JAXBElement. In the case of JAXBElements, we want to traverse its
     * underlying value as opposed to the JAXBElement.
     * @param type
     */
    protected static boolean isJAXBElement(JType type) {
        //noinspection RedundantIfStatement
        if (type.fullName().startsWith(JAXBElement.class.getName())) {
            return true;
        }
        return false;
    }

    public static List<JClass> allConcreteClasses(Set<ClassOutline> classes) {
        return allConcreteClasses(classes, Collections.<JClass>emptySet());
    }

    public static List<JClass> allConcreteClasses(Set<ClassOutline> classes, Set<JClass> directClasses) {
        List<JClass> results = new ArrayList<>();
        for (ClassOutline classOutline : classes) {
            if (!classOutline.target.isAbstract()) {
                JClass implClass = classOutline.implClass;
                results.add(implClass);
            }
        }
        results.addAll(directClasses);

        return results;
    }


}
