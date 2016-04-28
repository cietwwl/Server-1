package com.playerdata.activity.countType.cfg;

import java.util.List;

import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;


public class ActivityCountTypeCfg {

	private String id;
	
	private long startTime;
	
	private long endTime;
	
	private String startTimeStr;
	
	private String endTimeStr;
	
	private String cion;
	
	private String title;
	
	private String titleBG;
	private String desc;
	
//	private GoToType goToType;
	private String goToType;
	
	private String group;
		
	//每天刷新
	private boolean dateFresh = false;
	
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
	
	
	//parse from subItems
	private List<ActivityCountTypeSubItem>  subItemList;
	




	public String getId() {
		return id;
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

//	public GoToType getGoToType() {
//		return goToType;
//	}
	public String getGoToType() {
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


	
	public String getCountLimit() {
		return countLimit;
	}

	public void setCountLimit(String countLimit) {
		this.countLimit = countLimit;
	}

	public long getStartTime() {
		return startTime;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean isDateFresh() {
		return dateFresh;
	}

	
	
}
