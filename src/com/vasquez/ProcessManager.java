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

package com.vasquez;

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

        String [] commandShot = command.toArray(new String[command.size()]);
        
        Runtime runtime = Runtime.getRuntime();

        // Launch the process and add one to the counter
        try {
            Process process = runtime.exec(commandShot);
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
