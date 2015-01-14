package com.massfords.jaxb;

import org.junit.Assert;

import java.io.*;
import java.text.MessageFormat;

public class GeneratedCodeFixture extends Assert {
    private String expectedPathPattern;
    private String generatedPathPattern;
    
    public GeneratedCodeFixture(String expectedPathPattern, String generatedPathPattern) {
        this.expectedPathPattern = expectedPathPattern;
        this.generatedPathPattern = generatedPathPattern;
    }
    
    public void assertAllFiles() throws Exception {
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
    
    public void assertClassGenerated(String className) {
        File file = new File(MessageFormat.format(generatedPathPattern, className));
        assertTrue("expected to find generated class:" + file, file.isFile());
    }
    
    public void assertClassMatches(String className) throws Exception {
        String expected = MessageFormat.format(expectedPathPattern, className);
        String actual = MessageFormat.format(generatedPathPattern, className);
        
        String expectedValue = noComments(new FileReader(expected));

        String actualValue = noComments(new FileReader(actual));

        assertEquals(className + " failed to match", expectedValue, actualValue);
    }

    protected String noComments(Reader r) throws IOException {
        BufferedReader reader = new BufferedReader(r);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("//") && !line.contains("{@link")) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
