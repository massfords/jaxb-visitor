package com.massfords.jaxb;

import java.util.ArrayList;
import java.util.List;

public class VisitorPluginTest extends BaseVisitorPluginTest {

    public VisitorPluginTest() {
        super(new GeneratedCodeFixture(
                "src/test/resources/expected/{0}.java.txt",
                "target/generated-sources/xjc/org/example/visitor/{0}.java"));
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>(super.getArgs());
        args.add("-Xvisitor");
        args.add("-Xvisitor-package:org.example.visitor");
        return args;
    }
}
