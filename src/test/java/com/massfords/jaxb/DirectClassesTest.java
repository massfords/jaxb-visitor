package com.massfords.jaxb;

import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mford
 */
public class DirectClassesTest extends RunXJC2Mojo {
    private GeneratedCodeFixture generatedCodeFixture = new GeneratedCodeFixture(
            "src/test/resources/expected-direct/{0}.java.txt",
            "target/generated-sources/xjc/org/example/direct/{0}.java");

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources/direct-classes/");
    }

    @Override
    protected void configureMojo(AbstractXJC2Mojo mojo) {
        super.configureMojo(mojo);
        mojo.setBindingDirectory(getSchemaDirectory());
        mojo.setForceRegenerate(true);
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>(super.getArgs());
        args.add("-Xvisitor");
        args.add("-Xvisitor-package:org.example.direct");
        return args;
    }

    @Override
    public void testExecute() throws Exception {
        super.testExecute();

        generatedCodeFixture.assertAllFiles();
    }
}
