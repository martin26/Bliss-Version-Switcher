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
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.*;

// Switches the files to the correct version of Diablo II 

public class FileSwitcher {
	public FileSwitcher(Entry entry) {
		version = entry.getVersion();
		path = entry.getPath();
		flags = entry.getSplitFlags();
		expansion = entry.isExpansion();
		lastRanVersion = null;
		game = new File(path);
		root = new File(game.getParent());
		lastRanVersionFile = "LastRanVersion.txt";
	}
	
	public void launch() {
		System.out.println("Executing Diablo II: " + version);
		
		// Get last version ran and check with current version
		// if match, run game, if not, set lastRanVersion and copy files to directory, and set savepath
		
		// This should only happen the first time the user runs the application
		if(getLastVersion() == null) {
			
			// Set version to the current version the user has
			setLastVersion(version);
			
			// And back up these files since the user probably didn't back them up already
			backupFiles();
			
			// Launch the game
			runGame();
		} else if(getLastVersion().equalsIgnoreCase(version.toString())) {
			// Since this was the last version you were playing, most of the files are already in place.
			runGame();
		}
		else {
			// If you are running a different version of D2 compared to the last one, this 'else' will run.
			
			// Backup the files first
			backupFiles();
			
			// Copy the files for the target version now
			copyFiles();
			
			// Update the "Save Path" and "Resolution" registry variables
			RegistryUtility ru = new RegistryUtility(root.getAbsolutePath(), version, expansion);
			
			ru.update();
			
			// Make sure that the save directory exists in this sub-directory
			checkSaveDir();
			
			// write the version for this run now
			setLastVersion(version);
			
			// Launch the game
			runGame();
		}	
	}
	
	// Checks to see if a save directory exists, if not, creates it
	private void checkSaveDir() {
		File saveDir = null;
		
		// Sets the path depending if it's an expansion or classic entry
		if(expansion == true) {
			saveDir = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\save\\");
		} 
		else {
			saveDir = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\save\\");
		}
		
		if(!saveDir.exists()) {
			System.out.println("Save directory doesn't exist for version " + version + ". Creating now...");
			saveDir.mkdirs();
		}
	}
	
	private void runGame() {
		try {
			List<String> params = new ArrayList<String>();
			
			// Adding the game to the list
			params.add(path.toString());
			
			// Adding the rest of the flags to the list
			for(String x: flags) {
				params.add(x);
			}
			
			@SuppressWarnings("unused")
			Process p = new ProcessBuilder(params).start();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	// Copies the files for the version you want to play
	private void copyFiles() {
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
					System.out.println("[" + version + "] Copying " + source.getName() + " to " + dest.getParent());
					Files.copy(sourceDll, destDll, REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}
	}
	
	// Backup the files that are in this current directory
	private void backupFiles() {
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
			
			if(source.exists()) {
				if(dest.exists()) {
					System.out.println("File: " + dest.getName() + " already backed up in the " + dest.getParent() + " directory.");	
				} 
				else {
					File backupDir = new File(dest.getParent());
					
					// If the backup directory doesn't exist, Then make it
					if(!backupDir.exists()) {
						backupDir.mkdirs();
					}
					
					// Backup the files
					backupFilesHandler(source, dest);
				}
			}
		}
		
		// Switch the Expansion MPQs to different locations depending if expansion/classic
		if(expansion == true) {
			System.out.println("Switching to Expansion...");
			switchToExpansion();
		}
		else {
			System.out.println("Switching to Classic...");
			switchToClassic();
		}
	}
	
	private void backupFilesHandler(File source, File dest) {
		Path sourceDll = Paths.get(source.getAbsolutePath());
		Path destDll = Paths.get(dest.getAbsolutePath());
		
		// Check to see if the version is 1.00/1.07 and if it is then don't copy some files
		try {	
			// Expansion
			if(expansion == true) {
				if(version.equalsIgnoreCase("1.07") && !source.getName().equalsIgnoreCase("Patch_D2.mpq")) {
						System.out.println("Backing up: " + source.getName() + " to " + version + " directory.");
						Files.copy(sourceDll, destDll, REPLACE_EXISTING);	
				} 
				else if(version.equalsIgnoreCase("1.07") && source.getName().equalsIgnoreCase("Patch_D2.mpq")){ 
						System.out.println("This is expansion 1.07 and a patch has been detected.");
						System.out.println("Deleting: " + source.getName());
						source.delete();
				}
				else {
					// You can copy the same files for all the other versions (Well... anything > 1.07).
					System.out.println("Backing up " + source.getName() + " to " + version + " directory.");
					Files.copy(sourceDll, destDll, REPLACE_EXISTING);
				}
			}
			else {
				// Classic
				if(version.equalsIgnoreCase("1.00") && (!source.getName().equalsIgnoreCase("Patch_D2.mpq") 
													&& !source.getName().equalsIgnoreCase("BNUpdate.exe"))) {
					System.out.println("Backing up: " + source.getName() + " to " + version + " directory.");
					Files.copy(sourceDll, destDll, REPLACE_EXISTING);
				} 
				else if(version.equalsIgnoreCase("1.00") && (source.getName().equalsIgnoreCase("Patch_D2.mpq") 
														 || source.getName().equalsIgnoreCase("BNUpdate.exe"))){
						source.delete();
				}
				else {
					// You can copy the same files for all the other versions (Well... anything > 1.00).
					System.out.println("Backing up " + source.getName() + " to " + version + " directory.");
					Files.copy(sourceDll, destDll, REPLACE_EXISTING);
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
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
				System.out.println("Expansion File: " + source_mpq.getName() + " exists. Moving to Expansion directory.");
				
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
				System.out.println("Expansion File: " + source_mpq.getName() + " exists. Moving to root directory.");
				
				try {
					Files.move(sourceMPQ, destMPQ, REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void setLastVersion(String version) {
		System.out.println("Saving data into " + lastRanVersionFile + "...");
    	
    	try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(lastRanVersionFile));
			bw.write(version + "\r\n");
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
					lastRanVersion = line;
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
	
	private String version;
	private String path;
	private String[] flags;
	private boolean expansion;
	private String lastRanVersion;
	private String lastRanVersionFile;
	
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
