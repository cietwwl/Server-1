package com.rw.routerServer;

public class RouterServiceHandler {
	
	private static RouterServiceHandler instance = new RouterServiceHandler();
	
	public static RouterServiceHandler getInstance(){
		return instance;
	}
	
	public String getGift(String param){
		return "{}";
	}
}
