/*
 * Copyright 2013-2017 Jonathan Vasquez <jon@xyinn.org>
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.vasquez.Utilities;

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
                bw.write("\"NewSavePath\"=\"" + getSavePath() + "Expansion\\\\" + version + "\\\\save\\\\\"\r\n");
            }
            else {
                bw.write("\"Save Path\"=\"" + getSavePath() + "Classic\\\\" + version + "\\\\save\\\\\"\r\n");
                bw.write("\"NewSavePath\"=\"" + getSavePath() + "Classic\\\\" + version + "\\\\save\\\\\"\r\n");

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
