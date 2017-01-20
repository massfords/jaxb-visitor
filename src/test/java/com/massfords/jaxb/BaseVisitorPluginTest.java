package com.massfords.jaxb;

import org.junit.Test;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author markford
 */
public abstract class BaseVisitorPluginTest extends RunXJC2Mojo {

    private final GeneratedCodeFixture generatedCodeFixture;
    private final String srcDir;
    private List<String> extraArgs;

    BaseVisitorPluginTest(GeneratedCodeFixture generatedCodeFixture, String srcDir, List<String> extraArgs) {
        this.generatedCodeFixture = generatedCodeFixture;
        this.srcDir = srcDir;
        this.extraArgs = extraArgs;
    }

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), srcDir);
    }

    @Override
    protected void configureMojo(AbstractXJC2Mojo mojo) {
        super.configureMojo(mojo);
        mojo.setForceRegenerate(true);
        mojo.setBindingExcludes(new String[]{"*.xjb"});
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        super.testExecute();

        generatedCodeFixture.assertAllFiles();
    }

    @Override
    public List<String> getArgs() {
        List<String> args = new ArrayList<>();
        args.add("-Xvisitor");
        args.addAll(extraArgs);
        return args;
    }
}
