package com.massfords.jaxb;

import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VisitorPluginSerializableTest extends RunXJC2Mojo {

    GeneratedCodeFixture generatedCodeFixture = new GeneratedCodeFixture("src/test/resources/expected-serializable/{0}.java.txt",
            "target/generated-sources/xjc/org/example/visitor/serializable/{0}.java");

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources");
    }

    @Override
    protected void configureMojo(AbstractXJC2Mojo mojo) {
        super.configureMojo(mojo);
        mojo.setForceRegenerate(true);
        mojo.setBindingIncludes(new String[]{"serializable.xjb"});
        mojo.setExtension(true);
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>(super.getArgs());
        args.add("-Xvisitor");
        args.add("-Xvisitor-package:org.example.visitor.serializable");
        return args;
    }

    @Override
    public void testExecute() throws Exception {
        super.testExecute();

        generatedCodeFixture.assertClassGenerated("DepthFirstTraverserImpl");
        generatedCodeFixture.assertClassMatches("DepthFirstTraverserImpl");
    }
}
