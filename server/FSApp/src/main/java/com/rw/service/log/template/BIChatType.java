package com.rw.service.log.template;

public enum BIChatType {
	WORD(1),
	GROUP(2),
	PRIVATE(4),
	TREASURE(6)
	;
	
	private int type;
	private BIChatType(int type){
		this.type = type;
	}
	public int getType() {
		return type;
	}
}
