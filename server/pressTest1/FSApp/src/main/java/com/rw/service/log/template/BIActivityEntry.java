package com.rw.service.log.template;


/**
 * 活动入口
 * @author allen
 *
 */
public enum BIActivityEntry {
	
	Entry(1); 
	
	private int entry =1;
	
	private BIActivityEntry(int type){
		this.entry = type;
	}

	public int getEntry(){
		return this.entry;
	}
}
