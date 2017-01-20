package com.massfords.jaxb;

import java.util.ArrayList;
import java.util.List;

public class VisitorPluginNoOverloadTest extends BaseVisitorPluginTest {

    public VisitorPluginNoOverloadTest() {
        super(new GeneratedCodeFixture(
                "src/test/resources/expected-no-overloading/{0}.java.txt",
                "target/generated-sources/xjc/org/nooverload/visitor/{0}.java"),
                "src/test/resources");
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>(super.getArgs());
        args.add("-Xvisitor");
        args.add("-Xvisitor-includeType");
        args.add("-Xvisitor-package:org.nooverload.visitor");
        return args;
    }
}
