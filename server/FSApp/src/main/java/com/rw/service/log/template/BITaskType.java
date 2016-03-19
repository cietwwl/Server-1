package com.rw.service.log.template;

public enum BITaskType {
	
	Main(1), //主线
	Branch(2); //支线
	
	private int type =1;
	
	private BITaskType(int type){
		this.type = type;
	}

	public int getTypeNo(){
		return this.type;
	}
}
