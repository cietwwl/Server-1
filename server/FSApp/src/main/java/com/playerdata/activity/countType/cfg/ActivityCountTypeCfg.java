package com.playerdata.activity.countType.cfg;

import java.util.List;

import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;


public class ActivityCountTypeCfg {

	private String id;
	
	private long starTime;
	
	private long endTime;
	
	private String cion;
	
	private String title;
	
	private String titleBG;
	
	private String desc;
	
	private GoToType goToType;
	
	private int sortNum;
	
	//id-count-giftId;
	private String subItems;
	
	//parse from subItems
	private List<ActivityCountTypeSubItem>  subItemList;

	public String getId() {
		return id;
	}

	public long getStarTime() {
		return starTime;
	}

	public long getEndTime() {
		return endTime;
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

	public GoToType getGoToType() {
		return goToType;
	}

	public int getSortNum() {
		return sortNum;
	}

	public String getSubItems() {
		return subItems;
	}

	public List<ActivityCountTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityCountTypeSubItem> subItemList) {
		this.subItemList = subItemList;
	}





	
	
	
	
}
