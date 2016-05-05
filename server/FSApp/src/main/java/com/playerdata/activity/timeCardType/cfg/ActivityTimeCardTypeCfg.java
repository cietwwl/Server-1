package com.playerdata.activity.timeCardType.cfg;


public class ActivityTimeCardTypeCfg {

	private String id;
	
	private String cion;
	
	private String title;
	
	private String titleBG;
	
	private String desc;
	
	private String group;
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	private int sortNum;
	
	private String countLimit;
	
	//id-count-giftId;
	private String subItems;
	

	public String getId() {
		return id;
	}

	public String getCion() {
		return cion;
	}

	public String getTitle() {
		return title;
	}

	public String getTitleBG() {
		return titleBG;
	}

	public String getDesc() {
		return desc;
	}

	public int getSortNum() {
		return sortNum;
	}

	public String getSubItems() {
		return subItems;
	}

	
	public String getCountLimit() {
		return countLimit;
	}

	public void setCountLimit(String countLimit) {
		this.countLimit = countLimit;
	}
	
	
}
