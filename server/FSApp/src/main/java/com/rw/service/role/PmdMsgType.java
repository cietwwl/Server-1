package com.rw.service.role;

public enum PmdMsgType {
	
	RandomBoss(24),
	;
	
	private int id;
	PmdMsgType(int id){
		this.id = id;
	}
	public int getId() {
		return id;
	}
	
}
