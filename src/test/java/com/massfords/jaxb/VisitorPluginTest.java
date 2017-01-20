package com.massfords.jaxb;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(Parameterized.class)
public class VisitorPluginTest extends BaseVisitorPluginTest {

    public VisitorPluginTest(@SuppressWarnings("unused") String name,
                             String expectedPathPattern,
                             String generatedPathPattern,
                             String srcDir,
                             List<String> extraArgs) {
        super(new GeneratedCodeFixture( expectedPathPattern, generatedPathPattern), srcDir, extraArgs);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> params() throws Exception {
        return Arrays.asList(
                new Object[] {"basic",
                        "src/test/resources/expected/{0}.java.txt",
                        "target/generated-sources/xjc/org/example/visitor/{0}.java",
                        "src/test/resources",
                        Collections.singletonList("-Xvisitor-package:org.example.visitor")
                },
                new Object[] {"dupe",
                        "src/test/resources/dupe-expected/{0}.java.txt",
                        "target/generated-sources/xjc/dupe/{0}.java",
                        "src/test/resources/dupe",
                        Collections.singletonList("-Xvisitor-package:dupe")
                },
                new Object[] {"noOverload",
                        "src/test/resources/expected-no-overloading/{0}.java.txt",
                        "target/generated-sources/xjc/org/nooverload/visitor/{0}.java",
                        "src/test/resources",
                        Arrays.asList("-Xvisitor-includeType", "-Xvisitor-package:org.nooverload.visitor")
                }
                );
    }
}
