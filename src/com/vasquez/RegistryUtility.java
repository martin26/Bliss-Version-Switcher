/* 
 * Copyright (C) 2013 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * 
 * */

package com.vasquez;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RegistryUtility {
	public RegistryUtility(String rootDir, String version, boolean expansion) {
		this.rootDir = rootDir;
		this.version = version;
		this.expansion = expansion;
		registryFile = "SavePath.reg";
	}
	
	public void update() {
		prepareRegistryFile();
		updateRegistry();
	}
	
	// This will prepare a registry file with the correct save path. We will use this file and feed it to the 'reg' application
	// which in turn will update the registry. This avoids using the external 'jni' library and other types of more complicated hacks.
	private void prepareRegistryFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(registryFile));
			bw.write("Windows Registry Editor Version 5.00\r\n");
			bw.write("\r\n");
			bw.write("[HKEY_CURRENT_USER\\Software\\Blizzard Entertainment\\Diablo II]\r\n" );
			
			// Sets the path depending if it's an expansion or classic entry
			if(expansion == true) {
				bw.write("\"Save Path\"=\"D:\\\\Games\\\\Diablo II\\\\Expansion\\\\" + version + "\\\\save\\\\\"\r\n");
				
				// Set the resolution to 800x600 (For convenience, not a necessity)
				bw.write("\"Resolution\"=dword:00000001\r\n");
			}
			else {
				bw.write("\"Save Path\"=\"D:\\\\Games\\\\Diablo II\\\\Classic\\\\" + version + "\\\\save\\\\\"\r\n");
				
				// Make sure the resolution is 640x480 or the game will crash when you try to load your character
				bw.write("\"Resolution\"=dword:00000000\r\n");
			}
						
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void updateRegistry() {
		try {
			List<String> params = java.util.Arrays.asList("REG.EXE", "IMPORT", registryFile);
			Process p = new ProcessBuilder(params).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String savePath;
	private String rootDir;
	private String version;
	private boolean expansion;
	private String registryFile;
}
