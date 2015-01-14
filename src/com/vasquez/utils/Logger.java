/* 
 * Copyright 2013-2015 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
