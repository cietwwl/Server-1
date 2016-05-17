package com.playerdata.activity.timeCountType.cfg;


public class ActivityTimeCountTypeSubCfg {

	private String id;
	
	//所属活动配置id
	private String parentId;
	
	//计数
	private int count;
	//计数奖励
	private String giftId;	
	
	public String getId() {
		return id;
	}

	
	public String getParentId() {
		return parentId;
	}


	public void setParentId(String parentId) {
		this.parentId = parentId;
	}




	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public String getGiftId() {
		return giftId;
	}


	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}
	


	
	
	
	
}
