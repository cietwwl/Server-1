package com.rw.trace.stat;

public class MsgStatFactory {

	public static MsgStatCollector collector = new MsgStatCollector();
	
	public static MsgStatCollector getCollector(){
		return collector;
	}
}
