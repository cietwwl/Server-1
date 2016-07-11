package com.rwbase.dao.fresherActivity.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rwbase.common.enu.eActivityType;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class FresherActivityItem implements FresherActivityItemIF{
	
	private String id;
	
	private String ownerId;
	
	private String currentValue;   //当前值
	
	private eActivityType type;
	
	private int cfgId;
	
	private long startTime;
	
	private long endTime;
	
	private boolean isFinish;   //是否完成
	
	private boolean isGiftTaken;//是否领奖
	
	private boolean isClosed;   //领取完奖励关闭该item 	

	public int getCfgId() {
		return cfgId;
	}
	public long getStartTime() {
		return startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public boolean isFinish() {
		return isFinish;
	}
	public boolean isGiftTaken() {
		return isGiftTaken;
	}
	public boolean isClosed() {
		return isClosed;
	}
	public void setCfgId(int cfgId) {
		this.cfgId = cfgId;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
	public void setGiftTaken(boolean isGiftTaken) {
		this.isGiftTaken = isGiftTaken;
	}
	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
	public eActivityType getType() {
		return type;
	}
	public void setType(eActivityType type) {
		this.type = type;
	}
	public String getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
