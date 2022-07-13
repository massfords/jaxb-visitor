package com.massfords.jaxb.codegen.creators;

import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.InitialState;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.xml.namespace.QName;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.massfords.jaxb.codegen.creators.Utils.annotateGenerated;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JAXBElementNameCallback {

    private static final String SETTER = "setJAXBElementName";
    private static final String GETTER = "getJAXBElementName";
    private static final String FIELD = "jaxbElementName";

    public static void create(InitialState state, CodeGenOptions options) {
        Outline outline = state.getOutline();
        JDefinedClass _class = outline.getClassFactory().createInterface(options.getPackageForVisitor(), "Named", null);
        annotateGenerated(_class, options);
        _class.method(JMod.PUBLIC, void.class, SETTER).param(QName.class, "name");
        _class.method(JMod.PUBLIC, QName.class, GETTER);

        Set<ClassOutline> named = onlyNamed(outline, state.getSorted());

        JClass jaxbElementClass = outline.getCodeModel().ref(JAXBElement.class).narrow(outline.getCodeModel().ref(Object.class).wildcard());

        for (ClassOutline classOutline : named) {
            JDefinedClass implClass = classOutline.implClass;
            // implement the interface
            implClass._implements(_class);
            /*
                @XmlTransient
                private QName jaxbElementName;
             */
            implClass.field(JMod.PRIVATE, QName.class, FIELD).annotate(XmlTransient.class);
            /*
               public void setJAXBElementName(QName name) {
                   this.jaxbElementName = name;
               }
            */
            JMethod setter = implClass.method(JMod.PUBLIC, void.class, SETTER);
            setter.param(QName.class, "name");
            setter.body().assign(JExpr._this().ref(FIELD), JExpr.ref("name"));
            /*
               public QName getJAXBElementName() {
                   return this.jaxbElementName;
               }
            */
            JMethod getter = implClass.method(JMod.PUBLIC, QName.class, GETTER);
            getter.body()._return(JExpr._this().ref(FIELD));

            /*
                public void afterUnmarshal(Unmarshaller u, Object parent) {
                    if (parent instanceof JAXBElement) {
                        this.jaxbElementName = ((JAXBElement)parent).getName()
                    }
                }
             */
            JMethod after = implClass.method(JMod.PUBLIC, void.class, "afterUnmarshal");
            after.param(Unmarshaller.class, "u");
            after.param(Object.class, "parent");
            after.body()._if(JExpr.ref("parent")._instanceof(jaxbElementClass))
                    ._then().assign(JExpr._this().ref(FIELD),
                            JExpr.invoke(JExpr.cast(jaxbElementClass, JExpr.ref("parent")), "getName"));
        }
    }

    private static Set<JDefinedClass> identifyCandidates(Outline outline) {

        // phase one: identify all the candidates and update the ObjectFactories with the setter call
        // phase two: only include instances that don't have a JDefinedClass as their super
        // phase three: add all the markings

        JClass qNameClass = outline.getCodeModel().ref(QName.class);
        Set<JDefinedClass> candidates = new LinkedHashSet<>();
        outline.getAllPackageContexts().forEach(po -> {
            // locate the object factory
            JDefinedClass of = outline.getPackageContext(po._package()).objectFactory();
            of.methods()
                    .stream()
                    .filter(method -> method.type().binaryName().startsWith(JAXBElement.class.getName()))
                    .filter(method -> ((JClass) method.type()).getTypeParameters().size() == 1)
                    .filter(method -> ((JClass) method.type()).getTypeParameters().get(0) instanceof JDefinedClass)
                    .filter(method -> !((JClass) method.type()).getTypeParameters().get(0).isAbstract())
                    .forEach(method -> {
                        JType retType = method.type();
                        JClass clazz = (JClass) retType;
                        List<JClass> typeParameters = clazz.getTypeParameters();
                        String namespace = null;
                        String localPart = null;
                        for (JAnnotationUse au : method.annotations()) {
                            // todo - need to check this and other spots for jakarta to see what the legacy behavior is
                            if (au.getAnnotationClass().fullName().equals(XmlElementDecl.class.getName())) {
                                namespace = annotationValueToString(au.getAnnotationMembers().get("namespace"));
                                localPart = annotationValueToString(au.getAnnotationMembers().get("name"));
                                break;
                            }
                        }
                        if (namespace != null) {
                            method.body().pos(0);
                            method.body().invoke(method.params().get(0), SETTER)
                                    .arg(JExpr._new(qNameClass).arg(namespace).arg(localPart));
                        }
                        JDefinedClass dc = (JDefinedClass) typeParameters.get(0);
                        candidates.add(dc);
                    });
        });
        return candidates;
    }

    private static Set<ClassOutline> filterSubclasses(Set<ClassOutline> all, Set<JDefinedClass> candidates) {
        // mapping the class to the outline
        Map<JDefinedClass, ClassOutline> classToOutline = all.stream()
                .collect(Collectors.toMap(co -> co.implClass, Function.identity()));

        Set<ClassOutline> filtered = new LinkedHashSet<>();
        all.forEach(co -> {
            if (candidates.contains(co.implClass)) {
                JDefinedClass jdc = co.implClass;
                JDefinedClass base = jdc._extends() instanceof JDefinedClass ? (JDefinedClass) jdc._extends() : null;
                if (base != null) {
                    while (base._extends() instanceof JDefinedClass) {
                        base = (JDefinedClass) base._extends();
                    }
                    filtered.add(classToOutline.get(base));
                } else {
                    filtered.add(co);
                }
            }
        });
        return filtered;
    }


    private static Set<ClassOutline> onlyNamed(Outline outline, Set<ClassOutline> sorted) {
        Set<JDefinedClass> candidates = identifyCandidates(outline);
        return filterSubclasses(sorted, candidates);
    }

    private static String annotationValueToString(JAnnotationValue ns) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        JFormatter jf = new JFormatter(pw, "");
        ns.generate(jf);
        pw.flush();
        String s = sw.toString();
        return s.substring(1, s.length() - 1);
    }
}
