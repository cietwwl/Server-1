package com.rw.routerServer.giftManger;

public enum RounterGiftType {
	
	TYPE_DAILY(1,"每日领取"),
	TYPE_CHARGE(2,"充值领取"),
	TYPE_LEVEL(3,"等级领取"),
	TYPE_VIP(4,"会员礼包"),//这个不作判断，直接领取
	TYPE_WEEK(5,"每周可以领取"),
	;
	private int type;
	private String desc;
	
	
	RounterGiftType( int ordinal, String name) {
		this.type = ordinal;
		this.desc = name;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
	
	

}
