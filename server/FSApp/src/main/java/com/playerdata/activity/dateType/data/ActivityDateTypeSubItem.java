package com.playerdata.activity.dateType.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.dateType.cfg.ActivityDateTypeSubCfg;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDateTypeSubItem {
	
	private String cfgId;
	

	private int count;
	
	//是否已领取
	private boolean taken;


	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}


	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	
	
	public boolean isTaken() {
		return taken;
	}

	public void setTaken(boolean taken) {
		this.taken = taken;
	}

	public static ActivityDateTypeSubItem newItem(ActivityDateTypeSubCfg subItemCfg){
		ActivityDateTypeSubItem subItem = new ActivityDateTypeSubItem();
		subItem.setCfgId(subItemCfg.getId());
		return subItem;
		
	}
	

}
