import com.massfords.jaxb.test.FileAssertion
import com.massfords.jaxb.test.Fixture

File actualDir = new File(basedir, "target/generated-sources/xjc")
File expectedDir = new File(basedir, "expected")
String packageAsPath = "org/example/visitor"
File packageDir = new File(actualDir, packageAsPath)

assertions = [
        "BaseVisitor",
        "DepthFirstTraverserImpl",
        "Traverser",
        "TraversingVisitor",
        "TraversingVisitorProgressMonitor",
        "Visitable",
        "Visitor",
].collect {name -> return new FileAssertion(name, packageDir) }

assertions.add(new FileAssertion("TSimpleRequest", new File(actualDir, "org/example/simple/")))

return Fixture.assertAll(expectedDir, assertions);

