package com.massfords.jaxb.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class Fixture {
    private Fixture() {
    }

    // called from the groovy scripts
    @SuppressWarnings("unused")
    public static boolean assertAll(File expectedDir, List<FileAssertion> assertionList) {
        return assertionList.stream().allMatch(fileAssertion -> {
            String filename = fileAssertion.getExpected() + ".java";
            return Fixture.actualEqualsExpected(
                    new File(fileAssertion.getActualDir(), filename),
                    new File(expectedDir, filename));
        });
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
            System.out.println("ok: " + expectedFile.getName());
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
