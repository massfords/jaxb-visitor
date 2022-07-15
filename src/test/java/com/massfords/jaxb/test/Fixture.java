package com.massfords.jaxb.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public final class Fixture {
    private Fixture() {
    }
    @SuppressWarnings("checkstyle:ConstantName")
    public static final String[] StandardClassNamesToTest = {
            "BaseVisitor",
            "DepthFirstTraverserImpl",
            "Traverser",
            "TraversingVisitor",
            "TraversingVisitorProgressMonitor",
            "Visitable",
            "Visitor"
    };

    public static boolean runStandardTests(File actualDir, File expectedDir, String packageAsPath) {
        return runTests(Fixture.StandardClassNamesToTest, actualDir, expectedDir, packageAsPath);
    }

    public static boolean runTests(String[] names, File actualDir, File expectedDir, String packageAsPath) {
        return Arrays.stream(names).allMatch(className -> runSingleTest(className, actualDir, expectedDir, packageAsPath));
    }

    public static boolean runSingleTest(String className, File actualDir, File expectedDir, String packageAsPath) {
        System.out.print("running test: " + className + " :");
        File actual = new File(actualDir, MessageFormat.format("{0}/{1}.java", packageAsPath, className));
        File expected = new File(expectedDir, MessageFormat.format("{0}.java", className));
        return Fixture.actualEqualsExpected(actual, expected);
    }

    public static boolean actualEqualsExpected(File actualFile, File expectedFile) {
        try (Reader expectedReader = new FileReader(expectedFile);
             Reader actualReader = new FileReader(actualFile)) {
            String expectedValue = noComments(expectedReader);
            String actualValue = noComments(actualReader);
            if (!expectedValue.equals(actualValue)) {
                System.out.println("error with " + expectedFile.getName());
                System.err.println("actual:" + actualValue);
                System.err.println("expect:" + expectedValue);
            }
            assertEquals(expectedValue, actualValue);
            System.out.println("ok");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String noComments(Reader r) throws IOException {
        BufferedReader reader = new BufferedReader(r);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("//")) {
                continue;
            }
            if (line.matches("^\\s+$")) {
                continue;
            }
            if (line.contains("{@link")) {
                continue;
            }
            sb.append(line).append("\n");
        }
        String s = sb.toString();
        // quick fix to strip javadoc
        Pattern p = Pattern.compile("/\\*.*?\\*/", Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(s);
        return m.replaceAll("")
                .replaceAll("public<", "public <")
                .replaceAll("\\n+", " ")
                .replaceAll(" +([>{!=:();&|])", "$1")
                .replaceAll("(>)([^ ])", "$1 $2")
                .replaceAll(" +", " ").trim();
    }
}
