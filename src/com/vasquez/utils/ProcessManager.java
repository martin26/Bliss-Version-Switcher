/* 
 * Copyright 2013-2015 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vasquez.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ProcessManager {
	private int processCount;
	
	public ProcessManager() {
		processCount = 0;
	}
	
	// Starts the application with its corresponding flags. Also keeps track
	// of how many instances of the application were started via the application.
	// This prevents users from opening up multiple versions of Diablo II.
	// Only multiple instances of the same version of Diablo II are allowed.
	public int startProcess(String path, String[] flags) {
		String finalCommand = "";
		ArrayList<String> command = new ArrayList<String>();
		command.add(path);
		
		// Launch the process.
		// Exit Codes: Game.exe = 0; Diablo II.exe = 1
		int successfulExitCode = 0;
		
		if (path.contains("Diablo II.exe")) {
			successfulExitCode = 1;
		}
		
		// Prepare the command line string so that we can execute everything in one shot
		for (String flag: flags) {
			command.add(flag);
		}
		
		// Build Command String
		for (Iterator<String> i = command.iterator(); i.hasNext();) {
			String value = i.next();
			
			if (finalCommand.isEmpty()) {
				finalCommand = value;
			}
			else {
				finalCommand = finalCommand + " " + value;
			}
		}
		
		Runtime runtime = Runtime.getRuntime();
		
		// Launch the process and add one to the counter
		try {
			Process process = runtime.exec(finalCommand);
			addProcessCount();
			
			// Wait for the process to finish.
			int exitResult = process.waitFor();
			
			delProcessCount();
			
			if(exitResult != successfulExitCode) {
				return -1;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		return 0;
	}
	
	public int getProcessCount() {
		return processCount;
	}
	
	public void addProcessCount() {
		processCount += 1;
	}
	
	public void delProcessCount() {
		processCount -= 1;
	}
}
