package com.massfords.jaxb;

import com.massfords.jaxb.codegen.AllInterfacesCreated;
import com.massfords.jaxb.codegen.ClassDiscoverer;
import com.massfords.jaxb.codegen.CodeGenOptions;
import com.massfords.jaxb.codegen.ImmutableAllInterfacesCreated;
import com.massfords.jaxb.codegen.ImmutableCodeGenOptions;
import com.massfords.jaxb.codegen.ImmutableInitialState;
import com.massfords.jaxb.codegen.ImmutableVisitorState;
import com.massfords.jaxb.codegen.InitialState;
import com.massfords.jaxb.codegen.VisitorState;
import com.massfords.jaxb.codegen.creators.classes.BaseVisitor;
import com.massfords.jaxb.codegen.creators.classes.DepthFirstTraverser;
import com.massfords.jaxb.codegen.creators.classes.TraversingVisitor;
import com.massfords.jaxb.codegen.creators.decorators.AddAcceptMethod;
import com.massfords.jaxb.codegen.creators.decorators.JAXBElementNameCallback;
import com.massfords.jaxb.codegen.creators.interfaces.Traverser;
import com.massfords.jaxb.codegen.creators.interfaces.TraversingVisitorProgressMonitor;
import com.massfords.jaxb.codegen.creators.interfaces.Visitable;
import com.massfords.jaxb.codegen.creators.interfaces.Visitor;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;
import org.xml.sax.ErrorHandler;

import java.util.Set;
import java.util.function.Function;

/**
 * Plugin generates the following code:
 *
 * <ul>
 * <li>Visitor: interface with visit methods for each of the beans</li>
 * <li>Traverser: interface with traverse methods for each of the beans.
 * <li>BaseVisitor: no-op impl of the Visitor interface</li>
 * <li>DepthFirstTraverserImpl: depth first implementation of the traverser interface</li>
 * <li>TraversingVisitor: class that pairs the visitor and traverser to visit the whole graph</li>
 * <li>accept(Visitor): added to each of the generated JAXB classes</li>
 * </ul>
 *
 * @author markford
 */
public final class VisitorPlugin extends Plugin {

    /**
     * name of the package for our generated visitor classes. If not set, we'll pick the first package from the outline.
     */
    private String packageName;
    private boolean includeType = false;

    /**
     * If true, we generate default implementations for some of the generated interfaces
     */
    private boolean generateClasses = true;

    /**
     * If true, do not traverse idrefs
     */
    private boolean noIdrefTraversal = false;

    /**
     * If true, use legacy non-Jakarta imports
     */
    private boolean noJakarta = false;

    /**
     * If true, include a generic arg for the visit and traverse functions
     */
    private boolean includeArg = false;

    @Override
    public String getOptionName() {
        return "Xvisitor";
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public int parseArgument(Options opt, String[] args, int index) {

        // look for the visitor-package argument since we'll use this for package name for our generated code.
        String arg = args[index];
        if (arg.startsWith("-Xvisitor-package:")) {
            packageName = arg.split(":")[1];
            return 1;
        }
        if (arg.startsWith("-Xvisitor-includeType:")) {
            includeType = "true".equalsIgnoreCase(arg.split(":")[1]);
            return 1;
        }
        if (arg.equals("-Xvisitor-includeType")) {
            includeType = true;
            return 1;
        }
        if (arg.equals("-Xvisitor-noClasses")) {
            generateClasses = false;
            return 1;
        }
        if (arg.equals("-Xvisitor-noIdrefTraversal")) {
            noIdrefTraversal = true;
            return 1;
        }
        if (arg.equals("-Xvisitor-legacy")) {
            noJakarta = true;
            return 1;
        }
        if (arg.equals("-Xvisitor-includeArg")) {
            includeArg = true;
            return 1;
        }
        return 0;
    }

    @Override
    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) {
        try {

            // create a set to hold all the beans that need a qname
            // add a qname field to each of these beans
            // add a getter/setter for the qname via an interface
            // add unmarshaller hook to each of these beans to pull the qname from their JAXBElement parent
            // update the traverser code for JAXBElement to see if the bean is an instance of
            //      this interface and invoke the setter
            // done and done

            JPackage vizPackage = getOrCreatePackageForVisitors(outline);

            Set<JClass> directClasses = ClassDiscoverer.discoverDirectClasses(outline, outline.getClasses());

            InitialState initialState = ImmutableInitialState.builder()
                    .outline(outline)
                    .addAllDirectClasses(directClasses)
                    .build();

            /*
             * These functions are used to produce the name of a Visitor or
             * Traverser method. Previously, these names were hardcoded and
             * we relied on Java's overloading to handle the dispatch but Issue #8
             * was filed regarding performance issues.
             *
             * The names produced from these functions will include the
             * type name if that feature is enabled. It's still possible that
             * may have some overloaded methods due to inner types but this should
             * cut down on overloading significantly.
             */
            Function<String, String> visitMethodNamer;
            Function<String, String> traverseMethodNamer;
            if (includeType) {
                visitMethodNamer = s -> "visit" + s;
                traverseMethodNamer = s -> "traverse" + s;
            } else {
                visitMethodNamer = s -> "visit";
                traverseMethodNamer = s -> "traverse";
            }

            CodeGenOptions codeGenOptions = ImmutableCodeGenOptions.builder()
                    .noIdrefTraversal(this.noIdrefTraversal)
                    .useLegacyImports(this.noJakarta)
                    .packageForVisitor(vizPackage)
                    .visitMethodNamer(visitMethodNamer)
                    .traverseMethodNamer(traverseMethodNamer)
                    .includeArg(this.includeArg)
                    .build();

            JAXBElementNameCallback.create(initialState, codeGenOptions);
            JDefinedClass visitor = Visitor.create(initialState, codeGenOptions);

            VisitorState visitorCreated = ImmutableVisitorState.builder()
                    .visitor(visitor)
                    .narrowedVisitor(visitor.narrow(visitor.typeParams()))
                    .initial(initialState)
                    .build();

            JDefinedClass visitable = Visitable.create(visitorCreated, codeGenOptions);
            AddAcceptMethod.decorate(visitorCreated, codeGenOptions);
            JDefinedClass traverser = Traverser.createInterface(visitorCreated, codeGenOptions);
            JDefinedClass progressMonitor = TraversingVisitorProgressMonitor.createInterface(
                    visitorCreated, codeGenOptions);

            if (generateClasses) {
                AllInterfacesCreated allState = ImmutableAllInterfacesCreated.builder()
                        .visitable(visitable)
                        .traverser(traverser)
                        .progressMonitor(progressMonitor)
                        .visitor(visitorCreated.visitor())
                        .narrowedVisitor(visitorCreated.narrowedVisitor())
                        .initial(visitorCreated.initial())
                        .build();
                BaseVisitor.createClass(allState, codeGenOptions);
                DepthFirstTraverser.createClass(allState, codeGenOptions);
                TraversingVisitor.createClass(allState, codeGenOptions);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }


    private JPackage getOrCreatePackageForVisitors(Outline outline) {
        JPackage vizPackage = null;
        if (packageName != null) {
            JPackage root = outline.getCodeModel().rootPackage();
            String[] packages = packageName.split("\\.");
            JPackage current = root;
            for (String p : packages) {
                current = current.subPackage(p);
            }
            vizPackage = current;
        }
        if (vizPackage == null) {
            PackageOutline packageOutline = outline.getAllPackageContexts().iterator().next();
            CClassInfoParent.Package pkage = new CClassInfoParent.Package(packageOutline._package());
            vizPackage = (JPackage) outline.getContainer(pkage, Aspect.IMPLEMENTATION);
        }
        return vizPackage;
    }
}
