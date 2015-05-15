/*
 * Copyright 2013-2015 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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

import org.apache.commons.io.FileUtils;

import com.vasquez.utils.Logger;
import com.vasquez.utils.ProcessManager;
import com.vasquez.utils.RegistryUtility;

import static java.nio.file.StandardCopyOption.*;

// Switches the files to the correct version of Diablo II

public class FileSwitcher {
    public FileSwitcher() {
        lastRanVersion = null;
        lastRanVersionFile = "LastRanVersion.txt";
        processManager = new ProcessManager();
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
        Logger.LogInfo("Launching Diablo II: " + getGameType() + " " + version);

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
                setLastVersion(version, expansion);

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
        RegistryUtility ru = new RegistryUtility(root.getAbsolutePath(), version, expansion);
        ru.update();
    }

    // Makes sure that the backup/save directories exist
    private void prepareBackupDir() {
        File saveDir = null;

        // Sets the path depending if it's an expansion or classic entry
        if(expansion) {
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
        Logger.LogInfo("Backing up important Diablo II files...");

        // Makes sure that the backup directories exist
        prepareBackupDir();

        for(String file: requiredFiles) {
            File sourceFile = new File(root.getAbsolutePath() + "\\" + file);
            File targetFile = null;

            // Sets the path depending if it's an expansion or classic entry
            if(expansion) {
                targetFile = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\" + file);
            }
            else {
                targetFile = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\" + file);
            }

            // Backup the files if they aren't already backed up
            if(sourceFile.exists() && !targetFile.exists()) {
                backupFilesHandler(sourceFile, targetFile);
            }
        }

        // Backs up the data folder if it exists
        doDataDir(0);
    }

    // Restore the files for the version you want to play
    private void restoreFiles() {
        Logger.LogInfo("Restoring important Diablo II files...");

        for(String file: requiredFiles) {
            File sourceFile = null;
            File targetFile = new File(root.getAbsolutePath() + "\\" + file);

            // Sets the path depending if it's an expansion or classic entry
            if(expansion) {
                sourceFile = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\" + file);
            }
            else {
                sourceFile = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\" + file);
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
        if(expansion) {
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
            if(expansion) {
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

            if(expansion) {
                targetFile = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\data\\");
            }
            else {
                targetFile = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\data\\");
            }
        }
        else {
            Logger.LogInfo("Restoring data directory if needed...");

            targetFile = new File(root.getAbsolutePath() + "\\data\\");

            if(expansion) {
                sourceFile = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\data\\");
            }
            else {
                sourceFile = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\data\\");
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

                if(expansion) {
                    backupFile = new File(root.getAbsolutePath() + "\\Expansion\\" + version + "\\data\\");
                }
                else {
                    backupFile = new File(root.getAbsolutePath() + "\\Classic\\" + version + "\\data\\");
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

    private void setLastVersion(String version, boolean expansion) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(lastRanVersionFile));
            bw.write(version + ";" + expansion + "\r\n");
            bw.close();
        }
        catch (IOException e) {
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
        if (expansion) {
            return "Expansion";
        }

        return "Classic";
    }

    public class LauncherRunnable implements Runnable {
        public void run() {
            int result = processManager.startProcess(path, flags);

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

    private String version;
    private String path;
    private String[] flags;
    private boolean expansion;
    private String lastRanVersion;
    private String lastRanVersionFile;
    private boolean lastRanType;
    private ProcessManager processManager;

    private File game;
    private File root;

    private enum GameType {
        Classic,
        Expansion
    }

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
