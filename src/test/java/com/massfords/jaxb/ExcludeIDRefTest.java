package com.massfords.jaxb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

public class ExcludeIDRefTest extends RunXJC2Mojo {
    private GeneratedCodeFixture generatedCodeFixture = new GeneratedCodeFixture(
            "src/test/resources/vec-expected/{0}.java.txt",
            "target/generated-sources/xjc/org/prostep/ecadif/vec113/visitor/{0}.java");

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources/vec/");
    }

    @Override
    protected void configureMojo(AbstractXJC2Mojo mojo) {
        super.configureMojo(mojo);
        mojo.setBindingDirectory(getSchemaDirectory());
        mojo.setForceRegenerate(true);
        mojo.setExtension(true);
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>(super.getArgs());
        args.add("-Xvisitor");
        args.add("-Xvisitor-package:org.prostep.ecadif.vec113.visitor");
        return args;
    }

    @Override
    public void testExecute() throws Exception {
        super.testExecute();

        generatedCodeFixture.assertAllFiles();
    }

}
