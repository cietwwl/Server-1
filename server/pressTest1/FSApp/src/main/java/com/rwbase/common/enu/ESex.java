package com.rwbase.common.enu;

public enum ESex {
	None(-1),		//新手
	Women(0), 	
	Men(1);
	
	private int type;
	ESex(int type){
		this.type = type;
	}
	public int getOrder(){
		return this.type;
	}
}
