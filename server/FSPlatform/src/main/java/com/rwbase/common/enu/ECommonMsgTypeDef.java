package com.rwbase.common.enu;

public enum ECommonMsgTypeDef {

	MsgBox(1),
	MsgTips(2);
	
	private final int value;
	
	private ECommonMsgTypeDef(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	
}
