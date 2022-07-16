import com.massfords.jaxb.test.Fixture

File actualDir = new File(basedir, "target/generated-sources/xjc")
File expectedDir = new File(basedir, "expected")
String packageAsPath = "org/example/visitor"

files = [
        "Traverser",
        "TraversingVisitorProgressMonitor",
        "Visitable",
        "Visitor"
]

files.each { f ->
    String filename = f + ".java"
    assert Fixture.actualEqualsExpected(
            new File(new File(actualDir, packageAsPath), filename),
            new File(expectedDir, filename));
}
return true
