/*
 * Copyright 2013-2015 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vasquez.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
            if(expansion) {
                bw.write("\"Save Path\"=\"" + getSavePath() + "Expansion\\\\" + version + "\\\\save\\\\\"\r\n");

                // Set the resolution to 800x600 (For convenience, not a necessity)
                bw.write("\"Resolution\"=dword:00000001\r\n");
            }
            else {
                bw.write("\"Save Path\"=\"" + getSavePath() + "Classic\\\\" + version + "\\\\save\\\\\"\r\n");

                // Make sure the resolution is 640x480 or the game will crash when you try to load your character
                bw.write("\"Resolution\"=dword:00000000\r\n");
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRegistry() {
        String[] command = {"REG.EXE", "IMPORT", registryFile};
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // Launch the process
        try {
            Process process = processBuilder.start();

            // Wait for the process to finish. Once the process finishes, remove the SavePath.reg file
            try {
                process.waitFor();
                deleteRegFile();
            }  catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete the SavePath.reg file since we are done using it
    private void deleteRegFile() {
        File savePath = new File(registryFile);

        if(savePath.exists()) {
            savePath.delete();
        }
    }

    // Gets the SavePath in a format that the Windows Registry can understand (aka double slashes)
    private String getSavePath() {
        return rootDir.replace("\\", "\\\\") + "\\\\";
    }

    private String rootDir;
    private String version;
    private boolean expansion;
    private String registryFile;
}
