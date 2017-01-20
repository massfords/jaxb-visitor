package com.massfords.jaxb;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class VisitorPluginTest extends BaseVisitorPluginTest {

    private final String packageName;

    public VisitorPluginTest(@SuppressWarnings("unused") String name,
                             String expectedPathPattern,
                             String generatedPathPattern,
                             String srcDir,
                             String packageName) {
        super(new GeneratedCodeFixture( expectedPathPattern, generatedPathPattern), srcDir);
        this.packageName = packageName;
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> params() throws Exception {
        return Arrays.asList(
                new Object[] {"basic",
                        "src/test/resources/expected/{0}.java.txt",
                        "target/generated-sources/xjc/org/example/visitor/{0}.java",
                        "src/test/resources",
                        "org.example.visitor"
                },
                new Object[] {"dupe",
                        "src/test/resources/dupe-expected/{0}.java.txt",
                        "target/generated-sources/xjc/dupe/{0}.java",
                        "src/test/resources/dupe",
                        "dupe"
                }
                );
    }


    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<>(super.getArgs());
        args.add("-Xvisitor");
        args.add("-Xvisitor-package:" + packageName);
        return args;
    }
}
