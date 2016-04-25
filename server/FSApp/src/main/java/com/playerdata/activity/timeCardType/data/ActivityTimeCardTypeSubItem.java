package com.playerdata.activity.timeCardType.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfg;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityTimeCardTypeSubItem {
	
	private String id;
	
	//剩下天数
	private int dayLeft;
	
	//上次领取每日奖励的时间
	private long lastTakeAwardTime;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDayLeft() {
		return dayLeft;
	}

	public void setDayLeft(int dayLeft) {
		this.dayLeft = dayLeft;
	}

	public long getLastTakeAwardTime() {
		return lastTakeAwardTime;
	}

	public void setLastTakeAwardTime(long lastTakeAwardTime) {
		this.lastTakeAwardTime = lastTakeAwardTime;
	}


	public static ActivityTimeCardTypeSubItem newItem(ActivityTimeCardTypeSubCfg subItemCfg){
		ActivityTimeCardTypeSubItem subItem = new ActivityTimeCardTypeSubItem();
		subItem.setId(subItemCfg.getId());
		return subItem;
		
	}
	

}
