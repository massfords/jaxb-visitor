package com.massfords.jaxb.test;

import java.io.File;

public final class FileAssertion {
    private final String expected;
    private final File actualDir;

    public FileAssertion(String expected, File actualDir) {
        this.expected = expected;
        this.actualDir = actualDir;
    }

    public String getExpected() {
        return expected;
    }

    public File getActualDir() {
        return actualDir;
    }
}
