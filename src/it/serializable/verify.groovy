import com.massfords.jaxb.test.Fixture
import com.massfords.jaxb.test.FileAssertion

File actualDir = new File(basedir, "target/generated-sources/xjc")
File expectedDir = new File(basedir, "expected")
String packageAsPath = "org/example/visitor"
File packageDir = new File(actualDir, packageAsPath)

assertions = [
        new FileAssertion("DepthFirstTraverserImpl", packageDir)
]
return Fixture.assertAll(expectedDir, assertions);

