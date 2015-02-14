/*
 * Copyright 2013-2015 Jonathan Vasquez <jvasquez1011@gmail.com>
 * Licensed under the Simplified BSD License which can be found in the LICENSE file.
 */

package com.vasquez;

public class Entry {
    public Entry(String version, String path, String flags, boolean expansion) {
        this.version = version;
        this.path = path;
        this.flags = flags;
        this.expansion = expansion;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public void setExpansion(boolean value) {
        expansion = value;
    }

    public Object[] toObjectArray() {
        return new Object[] {
                    getVersion(),
                    getPath(),
                    getFlags(),
                    isExpansion(),
        };
    }

    public String getVersion() { return version; }
    public String getPath() { return path; }
    public String getFlags() { return flags; }

    public boolean isExpansion() { return expansion; }

    public String[] getSplitFlags() {
        return flags.split(" ");
    }

    private String version;
    private String path;
    private String flags;
    private boolean expansion;
}
