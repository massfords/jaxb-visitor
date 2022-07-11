import com.massfords.jaxb.test.Fixture

File actualDir = new File(basedir, "target/generated-sources/xjc")
File expectedDir = new File(basedir, "expected")
String packageAsPath = "dupe"

Fixture.runStandardTests(actualDir, expectedDir, packageAsPath);
