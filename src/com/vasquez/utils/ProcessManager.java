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
