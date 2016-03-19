package com.rw.service.log.template;


/**
 * 活动入口
 * @author allen
 *
 */
public enum BIActivityCode {
	
	Entry(1); 
	
	private int code =1;
	
	private BIActivityCode(int type){
		this.code = type;
	}

	public int getCode(){
		return this.code;
	}
}
