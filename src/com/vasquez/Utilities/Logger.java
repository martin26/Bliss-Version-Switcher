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

package com.vasquez.Utilities;

public class Logger {
    @SuppressWarnings("unused")
    private static boolean debugMode;

    public static void EnableLogging() {
        debugMode = true;
    }

    public static void DisableLogging() {
        debugMode = false;
    }

    public static void LogError(String message) {
        if (!debugMode) { return; }
        System.out.println("[Error] " + message);
    }

    public static void LogSuccess(String message) {
        if (!debugMode) { return; }
        System.out.println("[Success] " + message);
    }

    public static void LogInfo(String message) {
        if (!debugMode) { return; }
        System.out.println("[Info] " + message);
    }

    public static void LogWarning(String message) {
        if (!debugMode) { return; }
        System.out.println("[Warning] " + message);
    }
}
