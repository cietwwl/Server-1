package com.log;

import com.rw.manager.ServerSwitch;

public class FSTraceLogger {

	public static void logger(String text) {
		if (ServerSwitch.isOpenTraceLogger()) {
			System.out.println(text);
		}
	}
}
