import com.massfords.jaxb.test.Fixture

File actualDir = new File(basedir, "target/generated-sources/xjc")
File expectedDir = new File(basedir, "expected")
String packageAsPath = "org/example/visitor"

assert Fixture.runSingleTest("DepthFirstTraverserImpl", actualDir, expectedDir, packageAsPath);

assert Fixture.actualEqualsExpected(
        new File(actualDir, "net/opengis/fes/_2/BinaryComparisonOpType.java"),
        new File(expectedDir, "BinaryComparisonOpType.java"));