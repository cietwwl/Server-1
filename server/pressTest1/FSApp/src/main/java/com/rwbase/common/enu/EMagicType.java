package com.rwbase.common.enu;

public enum EMagicType {

	Piece(1),  //法宝碎片
	Magic(2);   //法宝
	
	private int order;
	EMagicType(int order){
		this.order = order;
	}
	public int getOrder() {
		return order;
	}
	
}
