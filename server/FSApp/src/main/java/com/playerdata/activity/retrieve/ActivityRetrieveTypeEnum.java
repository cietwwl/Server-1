package com.playerdata.activity.retrieve;

/**
 * 只是用来存下功能编号，方便存入数据库时不冲突
 * @author Administrator
 *
 */
public enum ActivityRetrieveTypeEnum {
	retrieve(130001);
	
	private int id;
	
	private ActivityRetrieveTypeEnum(int id){
		this.id =id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
	
	
}
