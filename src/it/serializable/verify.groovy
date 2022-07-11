import com.massfords.jaxb.test.Fixture

File actualDir = new File(basedir, "target/generated-sources/xjc")
File expectedDir = new File(basedir, "expected")
String packageAsPath = "org/example/visitor/serializable"

assert Fixture.runSingleTest("DepthFirstTraverserImpl", actualDir, expectedDir, packageAsPath);