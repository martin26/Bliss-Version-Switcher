/* 
 * Copyright (C) 2013 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * */

package com.vasquez;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;

import static java.nio.file.StandardCopyOption.*;


// Switches the files to the correct version of Diablo II 

public class FileSwitcher {
	public FileSwitcher() {
		lastRanVersion = null;
		lastRanVersionFile = "LastRanVersion.txt";
		processCount = 0;
	}
	
	// Sets the information about the entry you want to launch
	public void setEntry(Entry entry) {
		version = entry.getVersion();
		path = entry.getPath();
		flags = entry.getSplitFlags();
		expansion = entry.isExpansion();
		
		game = new File(path);
		root = new File(game.getParent());
	}
	
	// Launches the game for the appropriate scenario
	// 1. First time running the application
	// 2. Replaying the version you played before
	// 3. Playing a different version than the last one you played
	public void launch() {
		System.out.println("Launching Diablo II: " + getGameType() + " " + version);
		
		// This will only happen the first time the user runs the application
		if(getLastVersion() == null) {
			// Set version to the current version the user has
			setLastVersion(version, expansion);
					
			// Backs up the files since this is the first time you are running this application
			backupFiles();
			
			// Updates the registry and makes sure that you have a save directory set up (Fresh environment)
			prepareRegistry();
			
			// Launch the game
			runGame();
		} 
		else if(getLastVersion().equalsIgnoreCase(version.toString()) && lastRanType == expansion) {
			// Since this was the last version you were playing, just start the game.
			runGame();
		}
		else {
			// This will run if you want to run a different version of D2 compared to the last one
			
			// Launch the game only if another D2 version isn't running
			if(getProcessCount() == 0) {
				// Delete the 'data' directory of the previous version if it exists in the Diablo II root
				delDataDir();
				
				// Backs up the files if you don't already have a backup for this new version
				backupFiles();
				
				// Copy the files for the target version now
				restoreFiles();
				
				// Updates the registry and makes sure that you have a save directory set up (Fresh environment)
				prepareRegistry();
				
				// write the version for this run now
				setLastVersion(version, expansion);
				
				// Start the game
				runGame();
			}
			else {
				System.out.println("You are already running a different version of Diablo II. Close it before switching to another version.");
			}
		}	
	}
	
	// Update the "Save Path" and "Resolution" registry variables
	public void prepareRegistry() {
		RegistryUtility ru = new RegistryUtility(root.getAbsolutePath(), version, expansion);
		ru.update();
	}
	
	// Makes sure that the backup/save directories exist
	private void prepareBackupDir() {
		File saveDir = null;
		
		// Sets the path depending if it's an expansion or classic entry
		if(expansion == true) {
			saveDir = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\save\\");
		} 
		else {
			saveDir = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\save\\");
		}
		
		// If we are going to be creating the backup directory, might as well use the 'save' directory as the top most
		// folder since we are going to need to create this directory anyways
		if(!saveDir.exists()) {
			saveDir.mkdirs();
		}
	}
	
	// Runs the game in a separate thread
	private void runGame() {
			Thread gameLaunch = new Thread(new LauncherRunnable());
			gameLaunch.start();
	}
	
	// Backup the files that are in this current directory
	private void backupFiles() {
		// Makes sure that the backup directories exist
		prepareBackupDir();
		
		for(String x: requiredFiles) {
			File source = new File(root.getAbsolutePath() + "\\" + x);	
			File dest = null;
			
			// Sets the path depending if it's an expansion or classic entry
			if(expansion == true) {
				dest = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\" + x);
			} 
			else {
				dest = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\" + x);
			}
		
			// Backup the files if they aren't already backed up
			if(source.exists() && !dest.exists()) {
				backupFilesHandler(source, dest);
			}		
		}
		
		// Backup the 'data' directory if it exists
		doDataDir(0);
	}
	
	private void backupFilesHandler(File source, File dest) {
		Path sourceDll = Paths.get(source.getAbsolutePath());
		Path destDll = Paths.get(dest.getAbsolutePath());
		
		// Check to see if the version is 1.00/1.07 and if it is then don't copy some files
		try {
			// Expansion
			if(expansion == true) {
				if(version.equalsIgnoreCase("1.07") && !source.getName().equalsIgnoreCase("Patch_D2.mpq")) {
					Files.copy(sourceDll, destDll, REPLACE_EXISTING);	
				} 
				else if(version.equalsIgnoreCase("1.07") && source.getName().equalsIgnoreCase("Patch_D2.mpq")){
					source.delete();
				}
				else {
					// You can copy the same files for all the other versions (Well... anything > 1.07).
					Files.copy(sourceDll, destDll, REPLACE_EXISTING);
				}
			}
			else {
				// Classic
				if(version.equalsIgnoreCase("1.00") && (!source.getName().equalsIgnoreCase("Patch_D2.mpq") 
													&& !source.getName().equalsIgnoreCase("BNUpdate.exe"))) {
					Files.copy(sourceDll, destDll, REPLACE_EXISTING);
				} 
				else if(version.equalsIgnoreCase("1.00") && (source.getName().equalsIgnoreCase("Patch_D2.mpq") 
														 || source.getName().equalsIgnoreCase("BNUpdate.exe"))){
					source.delete();
				}
				else {
					// You can copy the same files for all the other versions (Well... anything > 1.00).
					Files.copy(sourceDll, destDll, REPLACE_EXISTING);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Restore the files for the version you want to play
		private void restoreFiles() {
			for(String x: requiredFiles) {
				File source = null;
				File dest = new File(root.getAbsolutePath() + "\\" + x);
				
				// Sets the path depending if it's an expansion or classic entry
				if(expansion == true) {
					source = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\" + x);
				} else {
					source = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\" + x);
				}

				Path sourceDll = Paths.get(source.getAbsolutePath());
				Path destDll = Paths.get(dest.getAbsolutePath());
					
				if(source.exists()) {
					try {
						Files.copy(sourceDll, destDll, REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			// Restore the 'data' directory if it exists
			doDataDir(1);
			
			// Switch the Expansion MPQs to different locations depending if expansion/classic
			if(expansion == true) {
				switchToExpansion();
			}
			else {
				switchToClassic();
			}
		}
	
	// Backs up or restores the 'data' depending on the option
	// Options: 0 = Backup, Non-0 = Restore
	private void doDataDir(int choice) {
		File source = null;
		File dest = null;
		
		if(choice == 0) {
			source = new File(root.getAbsolutePath() + "\\data\\");
			
			if(expansion == true) {
				dest = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\data\\");
			} else {
				dest = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\data\\");
			}
		} else {
			dest = new File(root.getAbsolutePath() + "\\data\\");
			
			if(expansion == true) {
				source = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\data\\");
			} else {
				source = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\data\\");
			}
		}
	
		try {
			if(source.exists() && source.isDirectory()) {
				if(dest.exists() && dest.isDirectory()) {
					FileUtils.deleteDirectory(dest);
					FileUtils.copyDirectory(source, dest);
				} else if(dest.exists() && !dest.isDirectory()) {
					dest.delete();
					FileUtils.copyDirectory(source, dest);
				} else {
					FileUtils.copyDirectory(source, dest);
				}
			} else if(source.exists() && !source.isDirectory()) {
				// This is a bad file.. 'data' is suppose to be a folder not a file.
				source.delete();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	// Deletes the 'data' directory (used only if you are switching versions)
	private void delDataDir() {
		File source = new File(root.getAbsolutePath() + "\\data\\");
	
		if(source.exists() && source.isDirectory()) {
			try {
				FileUtils.deleteDirectory(source);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else if(source.exists() && !source.isDirectory()) {
			source.delete();
		}
	}
	
	// Moves the expansion specific MPQs to the Expansion directory
	private void switchToClassic() {
		for(String y: expansionMPQs) {
			File source_mpq = new File(root.getAbsolutePath() + "\\" + y);
			File dest_mpq = new File(root.getAbsolutePath() + "\\Expansion\\" + y);
			
			Path sourceMPQ = Paths.get(source_mpq.getAbsolutePath());
			Path destMPQ = Paths.get(dest_mpq.getAbsolutePath());
			
			if(source_mpq.exists()) {
				try {
					Files.move(sourceMPQ, destMPQ, REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// Moves the expansion specific MPQs to the root directory
	private void switchToExpansion() {
		for(String y: expansionMPQs) {
			File source_mpq = new File(root.getAbsolutePath() + "\\Expansion\\" + y);
			File dest_mpq = new File(root.getAbsolutePath() + "\\" + y);
			
			Path sourceMPQ = Paths.get(source_mpq.getAbsolutePath());
			Path destMPQ = Paths.get(dest_mpq.getAbsolutePath());
			
			if(source_mpq.exists()) {
				try {
					Files.move(sourceMPQ, destMPQ, REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void setLastVersion(String version, boolean expansion) {
    	try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(lastRanVersionFile));
			bw.write(version + ";" + expansion + "\r\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private String getLastVersion() {
		String line = null;
		
		try {
			File lrv = new File(lastRanVersionFile);
			
			if(lrv.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(lastRanVersionFile));
				
				while((line = br.readLine()) != null) {
					String[] result = line.split(";");
					lastRanVersion = result[0];
					lastRanType = Boolean.parseBoolean(result[1]);
				}
				
				br.close();
			}
			else {
				lrv.createNewFile();
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lastRanVersion;
	}
	
	private String getGameType() {
		if(expansion == true) { return "Expansion"; }
		else { return "Classic"; }
	}
	
	public class LauncherRunnable implements Runnable {
		public void run() {
			CommandLine cmdLine = new CommandLine(path);
			DefaultExecutor launcher = new DefaultExecutor();
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
			
			// Exit Codes: Game.exe = 0; Diablo II.exe = 1
			if(path.contains("Diablo II.exe")) {
				launcher.setExitValue(1);
			}
			
			// Prepare the command line string so that we can execute everything in one shot
			for(String x: flags) {
				cmdLine.addArgument(x);
			}
			
			// Launch the process and add one to the counter
			try {
				launcher.execute(cmdLine, resultHandler);
				addProcessCount();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			// Wait for the process to finish. Once the process finishes, remove one from the counter
			try {
				resultHandler.waitFor();
				
				// Only backup the 'data' directory if it exists and only if there is only one process (Things might have changed with -direct -txt)
				// Technically speaking this isn't completely the best way since if you opened 1 d2 and go into a game, then -direct -txt will generate bins
				// If you open a second d2 and go into a game, -direct -txt might generate bins if you deleted the bins between the first and second process,
				// Thus once you quit the second d2, and then quit the first d2, the application will backup, and it would actually be backing up the second
				// d2's -direct -txt generation. However, most likely this scenario of deleting the previously generated -direct -txt files in between two
				// running d2s will not happen. Most people open multiple copies of D2 and use the same -direct -txt throughout all of them.
				if(getProcessCount() == 1) {
					doDataDir(0);
				}
				
				delProcessCount();		
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
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
	
	private String version;
	private String path;
	private String[] flags;
	private boolean expansion;
	private String lastRanVersion;
	private String lastRanVersionFile;
	private boolean lastRanType;
	private int processCount;
	
	private File game;
	private File root;
	
	private String[] requiredFiles = {
			"binkw32.dll",
			"Bnclient.dll",
			"D2Client.dll",
			"D2CMP.dll",
			"D2Common.dll",
			"D2DDraw.dll",
			"D2Direct3D.dll",
			"D2Game.dll",
			"D2Gdi.dll",
			"D2gfx.dll",
			"D2Glide.dll",
			"D2Lang.dll",
			"D2Launch.dll",
			"D2MCPClient.dll",
			"D2Multi.dll",
			"D2Net.dll",
			"D2sound.dll",
			"D2Win.dll",
			"Fog.dll",
			"ijl11.dll",
			"SmackW32.dll",
			"Storm.dll",
			"Patch_D2.mpq",
			"Diablo II.exe",
			"Game.exe",
			"BNUpdate.exe",
			"D2VidTst.exe"
	};
	
	private String[] expansionMPQs = {
		"d2exp.mpq",
		"d2xmusic.mpq",
		"d2xvideo.mpq",
		"d2xtalk.mpq"
	};
}
