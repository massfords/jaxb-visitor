import com.massfords.jaxb.test.Fixture

File actualDir = new File(basedir, "target/generated-sources/xjc")
File expectedDir = new File(basedir, "expected")
String packageAsPath = "org/example/visitor"

assert Fixture.runSingleTest("Traverser", actualDir, expectedDir, packageAsPath);
assert Fixture.runSingleTest("TraversingVisitorProgressMonitor", actualDir, expectedDir, packageAsPath);
assert Fixture.runSingleTest("Visitor", actualDir, expectedDir, packageAsPath);
assert Fixture.runSingleTest("Visitable", actualDir, expectedDir, packageAsPath);