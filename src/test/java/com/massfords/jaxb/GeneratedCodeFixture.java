package com.massfords.jaxb;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GeneratedCodeFixture extends Assert {
    private String expectedPathPattern;
    private String generatedPathPattern;
    
    GeneratedCodeFixture(String expectedPathPattern, String generatedPathPattern) {
        this.expectedPathPattern = expectedPathPattern;
        this.generatedPathPattern = generatedPathPattern;
    }

    void assertInterfaces() throws Exception {
        String[] files = {
                "Traverser",
                "TraversingVisitorProgressMonitor",
                "Visitor",
                "Visitable"
        };

        for(String f : files) {
            assertClassGenerated(f);
            assertClassMatches(f);
        }
    }

    void assertAllFiles() throws Exception {
        String[] files = {
                "DepthFirstTraverserImpl",
                "BaseVisitor",
                "Traverser",
                "TraversingVisitor",
                "TraversingVisitorProgressMonitor",
                "Visitor",
                "Visitable"
        };
        
        for(String f : files) {
            assertClassGenerated(f);
            assertClassMatches(f);
        }
    }
    
    void assertClassGenerated(String className) {
        File file = new File(MessageFormat.format(generatedPathPattern, className));
        assertTrue("expected to find generated class:" + file, file.isFile());
    }
    
    void assertClassMatches(String className) throws Exception {
        String expected = MessageFormat.format(expectedPathPattern, className);
        String actual = MessageFormat.format(generatedPathPattern, className);
        
        String expectedValue = noComments(new FileReader(expected));

        String actualValue = noComments(new FileReader(actual));

        assertEquals(className + " failed to match", expectedValue, actualValue);
    }

    String noComments(Reader r) throws IOException {
        BufferedReader reader = new BufferedReader(r);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("//") && !line.contains("{@link")) {
                sb.append(line).append("\n");
            }
        }
        String s = sb.toString();
        // quick fix to strip all of the javadoc
        Pattern p = Pattern.compile("\\s*/\\*.+\\s*\\*/", Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(s);
        String replaced = m.replaceAll("");
        return replaced;
    }
}
