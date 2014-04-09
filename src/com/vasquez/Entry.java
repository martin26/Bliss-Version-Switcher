/* 
 * Copyright 2013-2014 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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