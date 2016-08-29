package com.massfords.jaxb;

import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

import java.io.File;

/**
 * @author markford
 */
public abstract class BaseVisitorPluginTest extends RunXJC2Mojo {

    private final GeneratedCodeFixture generatedCodeFixture;

    BaseVisitorPluginTest(GeneratedCodeFixture generatedCodeFixture) {
        this.generatedCodeFixture = generatedCodeFixture;
    }

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources");
    }

    @Override
    protected void configureMojo(AbstractXJC2Mojo mojo) {
        super.configureMojo(mojo);
        mojo.setForceRegenerate(true);
        mojo.setBindingExcludes(new String[]{"*.xjb"});
    }

    @Override
    public void testExecute() throws Exception {
        super.testExecute();

        generatedCodeFixture.assertAllFiles();
    }
}
