package com.massfords.jaxb;

import org.junit.Test;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

import java.io.File;

/**
 * @author markford
 */
public abstract class BaseVisitorPluginTest extends RunXJC2Mojo {

    private final GeneratedCodeFixture generatedCodeFixture;
    private final String srcDir;

    BaseVisitorPluginTest(GeneratedCodeFixture generatedCodeFixture, String srcDir) {
        this.generatedCodeFixture = generatedCodeFixture;
        this.srcDir = srcDir;
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
}
