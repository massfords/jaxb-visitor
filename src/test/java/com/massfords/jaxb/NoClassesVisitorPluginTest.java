package com.massfords.jaxb;

import org.apache.maven.plugin.Mojo;

import java.util.Arrays;

public class NoClassesVisitorPluginTest extends BaseVisitorPluginTest {
    public NoClassesVisitorPluginTest() {
        super(new GeneratedCodeFixture(
                "src/test/resources/expected-noClasses/{0}.java.txt",
                "target/generated-sources/xjc/org/noclasses/visitor/{0}.java"),
                "src/test/resources",
                Arrays.asList("-Xvisitor-noClasses", "-Xvisitor-package:org.noclasses.visitor"));
    }

    @Override
    public void testExecute() throws Exception {
        final Mojo mojo = initMojo();
        mojo.execute();

        generatedCodeFixture.assertInterfaces();
    }
}
