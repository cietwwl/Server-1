package com.common;

public enum RemoteMessageEnum {

	RMType_Benefit(1),//精准营销
	;
	
	private int id;

	private RemoteMessageEnum(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	
}
