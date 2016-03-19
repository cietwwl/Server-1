package com.rw.fsutil.log;

public class EngineLoggerFactory {

	public static EngineLogger getLogger(String name){
		return new EngineLoggerImpl(name);
	}
	
}
