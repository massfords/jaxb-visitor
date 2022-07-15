import com.massfords.jaxb.test.Fixture

File actualDir = new File(basedir, "target/generated-sources/xjc")
File expectedDir = new File(basedir, "expected")
String packageAsPath = "org/example/visitor"

assert Fixture.runStandardTests(actualDir, expectedDir, packageAsPath)

File actualFile = new File(actualDir, "org/example/simple/TSimpleRequest.java")
File expectedFile = new File(expectedDir, "TSimpleRequest.java")
assert Fixture.actualEqualsExpected(actualFile, expectedFile);