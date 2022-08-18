package com.massfords.jaxb.test;

import java.io.File;

public final class FileAssertion {
    private final String expected;
    private final File actualDir;

    public FileAssertion(String s, File file) {
        this.expected = s;
        this.actualDir = file;
    }

    public String getExpected() {
        return expected;
    }

    public File getActualDir() {
        return actualDir;
    }
}
