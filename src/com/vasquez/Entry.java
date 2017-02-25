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
    public String Version;
    public String Path;
    public String Flags;
    public boolean IsExpansion;
    public boolean WasLastRan;
    
    public Entry(String version, String path, String flags, boolean expansion, boolean wasLastRan) {
        this.Version = version;
        this.Path = path;
        this.Flags = flags;
        this.IsExpansion = expansion;
        this.WasLastRan = wasLastRan;
    }
    
    public String[] getSplitFlags() {
        return Flags.split(" ");
    }
}
