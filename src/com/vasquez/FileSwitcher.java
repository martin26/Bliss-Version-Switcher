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

import com.vasquez.Utilities.RegistryUtility;
import com.vasquez.Utilities.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import static java.nio.file.StandardCopyOption.*;
import java.util.ArrayList;
import java.util.Arrays;

// Switches the files to the correct version of Diablo II
public class FileSwitcher {
    private Entry _selectedEntry;
    private Entry _lastRanEntry;
    private EntryWithModel _tableManager;    
    private ProcessManager processManager;
    
    private File game;
    private File root;

    private enum GameType {
        Classic,
        Expansion
    }

    private String[] thirdPartyLibraries = {
         "binkw32.dll",
         "ijl11.dll",
         "SmackW32.dll"
    };
    

    // Keeping separate from commonFiles since 1.07 doesn't have it.
    private final String patchMpqFile = "Patch_D2.mpq";
    
    private String[] commonFiles = {
        "Diablo II.exe",
        "Game.exe",
        "BNUpdate.exe"
    };
    
    private final String[] requiredPre114Files = {
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
            "Storm.dll",
            "D2VidTst.exe"
    };
    
    private final String[] requiredPost113Files = {
        "BlizzardError.exe",
        "SystemSurvey.exe"
    };

    private String[] expansionMPQs = {
        "d2exp.mpq",
        "d2xmusic.mpq",
        "d2xvideo.mpq",
        "d2xtalk.mpq"
    };
    
    public FileSwitcher(EntryWithModel tableManager) {
        _tableManager = tableManager;
        _lastRanEntry = tableManager.getLastRanEntry();
        processManager = new ProcessManager();
    }

    // Sets the information about the entry you want to launch
    public void setEntry(Entry entry) {
        _selectedEntry = entry;
        game = new File(_selectedEntry.Path);
        root = new File(game.getParent());
    }

    // Launches the game for the appropriate scenario
    // 1. First time running the application
    // 2. Replaying the version you played before
    // 3. Playing a different version than the last one you played
    public void launch() {
        Logger.LogInfo("Launching Diablo II: " + getGameType() + " " + _selectedEntry.Version);

        // This will only happen the first time the user runs the application
        if(_lastRanEntry == null) {            
            // Set version to the current version the user has
            markSelectedEntryAsLastRan();

            // Backs up the files since this is the first time you are running this application
            backupFiles();

            // Updates the registry and makes sure that you have a save directory set up (Fresh environment)
            prepareRegistry();

            // Launch the game
            runGame();
        }
        else if(_lastRanEntry.Version.equalsIgnoreCase(_selectedEntry.Version) && _lastRanEntry.IsExpansion == _selectedEntry.IsExpansion) {
            // If the versions are the same, we will be using the same folder,
            // however, if you were to have multiple entries of the same version,
            // with different flags, switching between those entries would not
            // update the boolean. Let's fix that here.
            if(_lastRanEntry.WasLastRan != _selectedEntry.WasLastRan) {
                _selectedEntry.WasLastRan = true;
                _lastRanEntry.WasLastRan = false;
                _lastRanEntry = _selectedEntry;
            }
            
            // Delete the 'data' directory in the backup if the game doesn't have a 'data' directory in the root
            deleteDataDir(1);

            // Since this was the last version you were playing, just start the game.
            runGame();
        }
        else {
            // This will run if you want to run a different version of D2 compared to the last one

            // Launch the game only if another D2 version isn't running
            if(processManager.getProcessCount() == 0) {
                // Delete the 'data' directory of the previous version if it exists in the Diablo II root
                deleteDataDir(0);

                // Backs up the files if you don't already have a backup for this new version
                backupFiles();

                // Copy the files for the target version now
                restoreFiles();

                // Updates the registry and makes sure that you have a save directory set up (Fresh environment)
                prepareRegistry();

                // write the version for this run now
                markSelectedEntryAsLastRan();

                // Start the game
                runGame();
            }
            else {
                Logger.LogInfo("You are already running a different version of Diablo II. Close it before switching to another version.");
            }
        }
    }

    // Update the "Save Path" and "Resolution" registry variables
    public void prepareRegistry() {
        RegistryUtility ru = new RegistryUtility(root.getAbsolutePath(), _selectedEntry.Version, _selectedEntry.IsExpansion);
        ru.update();
    }

    // Makes sure that the backup/save directories exist
    private void prepareBackupDir() {
        File saveDir = null;

        // Sets the path depending if it's an expansion or classic entry
        if(_selectedEntry.IsExpansion) {
            saveDir = new File(root.getAbsolutePath() + "\\Expansion\\" + _selectedEntry.Version + "\\save\\");
        }
        else {
            saveDir = new File(root.getAbsolutePath() + "\\Classic\\" + _selectedEntry.Version + "\\save\\");
        }

        // If we are going to be creating the backup directory, might as well
        // use the 'save' directory as the top most folder since we are going
        // to need to create this directory anyways.
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
        Logger.LogInfo("Backing up files for last ran entry: " + _lastRanEntry.Version + " ...");

        // Makes sure that the backup directories exist
        prepareBackupDir();

        for(String file: getRequiredFiles(_lastRanEntry)) {
            File sourceFile = new File(root.getAbsolutePath() + "\\" + file);
            File targetFile = null;

            // Sets the path depending if it's an expansion or classic entry
            if(_selectedEntry.IsExpansion) {
                targetFile = new File(root.getAbsolutePath() + "\\Expansion\\" + _selectedEntry.Version + "\\" + file);
            }
            else {
                targetFile = new File(root.getAbsolutePath() + "\\Classic\\" + _selectedEntry.Version + "\\" + file);
            }

            // Backup the files if they aren't already backed up
            if(sourceFile.exists() && !targetFile.exists()) {
                backupFilesHandler(sourceFile, targetFile);
            }
            
            // Delete the files for these backed up ones so that the next
            // version has a clean slate.
            sourceFile.delete();
        }

        // Backs up the data folder if it exists
        doDataDir(0);
    }

    private ArrayList<String> getRequiredFiles(Entry entry)
    {       
        ArrayList<String> requiredFiles = new ArrayList<>();
        
        requiredFiles.addAll(Arrays.asList(commonFiles));
        requiredFiles.addAll(Arrays.asList(thirdPartyLibraries));
        
        if (entry.Version.equalsIgnoreCase("1.14d"))
        {
            requiredFiles.addAll(Arrays.asList(requiredPost113Files));
            requiredFiles.add(patchMpqFile);
        }
        else {
            // Every other version is the same (1.00-1.13),
            // just 1.00 and 1.07 don't have a Patch_D2.mpq.
            requiredFiles.addAll(Arrays.asList(requiredPre114Files));
            
            if (!entry.Version.equalsIgnoreCase("1.00") && !entry.Version.equalsIgnoreCase("1.07")) {
                requiredFiles.add(patchMpqFile);
            }
        }
        
       return requiredFiles;
    }
    
    // Restore the files for the version you want to play
    private void restoreFiles() {
        Logger.LogInfo("Restoring important files for new run: " + _selectedEntry.Version + " ...");

        for(String file: getRequiredFiles(_selectedEntry)) {
            File sourceFile = null;
            File targetFile = new File(root.getAbsolutePath() + "\\" + file);

            // Sets the path depending if it's an expansion or classic entry
            if(_selectedEntry.IsExpansion) {
                sourceFile = new File(root.getAbsolutePath() + "\\Expansion\\" + _selectedEntry.Version + "\\" + file);
            }
            else {
                sourceFile = new File(root.getAbsolutePath() + "\\Classic\\" + _selectedEntry.Version + "\\" + file);
            }

            Path sourceDll = Paths.get(sourceFile.getAbsolutePath());
            Path destDll = Paths.get(targetFile.getAbsolutePath());

            if(sourceFile.exists()) {
                try {
                    Files.copy(sourceDll, destDll, REPLACE_EXISTING);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Restore the 'data' directory if it exists
        doDataDir(1);

        // Switch the Expansion MPQs to different locations depending if expansion/classic
        if(_selectedEntry.IsExpansion) {
            switchTo(GameType.Expansion);
        }
        else {
            switchTo(GameType.Classic);
        }
    }

    private void backupFilesHandler(File source, File dest) {
        Path sourceDll = Paths.get(source.getAbsolutePath());
        Path destDll = Paths.get(dest.getAbsolutePath());

        // Check to see if the version is 1.00/1.07 and if it is then don't copy some files
        try {
            // Expansion
            if(_selectedEntry.IsExpansion) {
                if(_selectedEntry.Version.equalsIgnoreCase("1.07") && !source.getName().equalsIgnoreCase("Patch_D2.mpq")) {
                    Files.copy(sourceDll, destDll, REPLACE_EXISTING);
                }
                else if(_selectedEntry.Version.equalsIgnoreCase("1.07") && source.getName().equalsIgnoreCase("Patch_D2.mpq")){
                    source.delete();
                }
                else {
                    // You can copy the same files for all the other versions (Well... anything > 1.07).
                    Files.copy(sourceDll, destDll, REPLACE_EXISTING);
                }
            }
            else {
                // Classic
                if(_selectedEntry.Version.equalsIgnoreCase("1.00") && (!source.getName().equalsIgnoreCase("Patch_D2.mpq")
                                                    && !source.getName().equalsIgnoreCase("BNUpdate.exe"))) {
                    Files.copy(sourceDll, destDll, REPLACE_EXISTING);
                }
                else if(_selectedEntry.Version.equalsIgnoreCase("1.00") && (source.getName().equalsIgnoreCase("Patch_D2.mpq")
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

    // Backs up or restores the 'data' directory depending on the option
    // Options:
    //      0 = Backup
    //      Other = Restore
    private void doDataDir(int choice) {
        File sourceFile = null;
        File targetFile = null;

        if(choice == 0) {
            Logger.LogInfo("Backing up data directory if needed...");

            sourceFile = new File(root.getAbsolutePath() + "\\data\\");

            if(_selectedEntry.IsExpansion) {
                targetFile = new File(root.getAbsolutePath() + "\\Expansion\\" + _selectedEntry.Version + "\\data\\");
            }
            else {
                targetFile = new File(root.getAbsolutePath() + "\\Classic\\" + _selectedEntry.Version + "\\data\\");
            }
        }
        else {
            Logger.LogInfo("Restoring data directory if needed...");

            targetFile = new File(root.getAbsolutePath() + "\\data\\");

            if(_selectedEntry.IsExpansion) {
                sourceFile = new File(root.getAbsolutePath() + "\\Expansion\\" + _selectedEntry.Version + "\\data\\");
            }
            else {
                sourceFile = new File(root.getAbsolutePath() + "\\Classic\\" + _selectedEntry.Version + "\\data\\");
            }
        }

        try {
            if(sourceFile.exists() && sourceFile.isDirectory()) {
                if(targetFile.exists() && targetFile.isDirectory()) {
                    FileUtils.deleteDirectory(targetFile);
                    FileUtils.copyDirectory(sourceFile, targetFile);
                }
                else if(targetFile.exists() && !targetFile.isDirectory()) {
                    targetFile.delete();
                    FileUtils.copyDirectory(sourceFile, targetFile);
                }
                else {
                    FileUtils.copyDirectory(sourceFile, targetFile);
                }
            }
            else if(sourceFile.exists() && !sourceFile.isDirectory()) {
                Logger.LogWarning("A 'data' of type file was detected. 'data' is supposed to be a folder. Deleting...");
                sourceFile.delete();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    // Deletes the 'data' directory depending the situation
    // Options: 0 = Deletes data dir in root
    //          1 = Deletes data dir in backup if data dir in root doesn't exist
    private void deleteDataDir(int option) {
        File sourceFile = new File(root.getAbsolutePath() + "\\data\\");

        try {
            if(option == 0) {
                if(sourceFile.exists() && sourceFile.isDirectory()) {
                    FileUtils.deleteDirectory(sourceFile);
                }
                else if(sourceFile.exists() && !sourceFile.isDirectory()) {
                    sourceFile.delete();
                }
            }
            else if(option == 1 && !sourceFile.exists()) {
                File backupFile = null;

                if(_selectedEntry.IsExpansion) {
                    backupFile = new File(root.getAbsolutePath() + "\\Expansion\\" + _selectedEntry.Version + "\\data\\");
                }
                else {
                    backupFile = new File(root.getAbsolutePath() + "\\Classic\\" + _selectedEntry.Version + "\\data\\");
                }

                if(backupFile.exists() && backupFile.isDirectory()) {
                    FileUtils.deleteDirectory(backupFile);
                }
                else if(backupFile.exists() && !backupFile.isDirectory()) {
                    backupFile.delete();
                }
            }
        } catch (IOException e) {
            Logger.LogError("A problem was encountered while deleting data dir");
            e.printStackTrace();
        }
    }

    // Moves classic or expansion specific MPQs to or from the root directory
    private void switchTo(GameType gameType) {
        String sourcePath = null;
        String targetPath = null;

        File sourceMpq = null;
        File targetMpq = null;

        if (gameType == GameType.Expansion) {
            Logger.LogInfo("The game will use the Expansion MPQs. Enabling Expansion ...");
            sourcePath = root.getAbsolutePath() + "\\Expansion\\";
            targetPath = root.getAbsolutePath() + "\\";
        }
        else {
            Logger.LogInfo("The game will not use the Expansion MPQs. Enabling Classic ...");
            sourcePath = root.getAbsolutePath() + "\\";
            targetPath = root.getAbsolutePath() + "\\Expansion\\";
        }

        for(String mpq: expansionMPQs) {
            if (gameType == GameType.Expansion) {
                sourceMpq = new File(sourcePath + mpq);
                targetMpq = new File(targetPath + mpq);
            }
            else {
                sourceMpq = new File(sourcePath + mpq);
                targetMpq = new File(targetPath + mpq);
            }

            Path sourceMpqPath = Paths.get(sourceMpq.getAbsolutePath());
            Path targetMpqPath = Paths.get(targetMpq.getAbsolutePath());

            if(sourceMpq.exists()) {
                try {
                    Files.move(sourceMpqPath, targetMpqPath, REPLACE_EXISTING);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void markSelectedEntryAsLastRan() { 
        if(_lastRanEntry != null) {
            _lastRanEntry.WasLastRan = false;
        }
        _selectedEntry.WasLastRan = true;
        _lastRanEntry = _selectedEntry;
        
        _tableManager.saveData();
    }

    private String getGameType() {
        return _selectedEntry.IsExpansion ? "Expansion" : "Classic";
    }

    public class LauncherRunnable implements Runnable {
        public void run() {
            int result = processManager.startProcess(_selectedEntry.Path, _selectedEntry.getSplitFlags());

            if (result == -1) {
                Logger.LogError("There was an error managing the Diablo II process.");
            }

            // Only backup the 'data' directory if it exists and only after the last process finished.
            // Meaning that if the user has 3 Diablo II processes opened, only after the user closes the
            // last one will the application backup the data dir.
            if(processManager.getProcessCount() == 0) {
                doDataDir(0);
            }
        }
    }
}
