/*
 * Copyright 2013-2015 Jonathan Vasquez <jvasquez1011@gmail.com>
 * Licensed under the Simplified BSD License which can be found in the LICENSE file.
 */

package com.vasquez.utils;

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
