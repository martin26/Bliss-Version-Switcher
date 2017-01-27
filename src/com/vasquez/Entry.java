/*
 * Copyright 2013-2017 Jonathan Vasquez <jon@xyinn.org>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
