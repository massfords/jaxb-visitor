import com.massfords.jaxb.test.Fixture

File actualDir = new File(basedir, "target/generated-sources/xjc")
File expectedDir = new File(basedir, "expected")
String packageAsPath = "org/prostep/ecadif/vec113/visitor"

assert Fixture.runStandardTests(actualDir, expectedDir, packageAsPath);

