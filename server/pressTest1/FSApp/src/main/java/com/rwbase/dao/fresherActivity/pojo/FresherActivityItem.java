package com.rwbase.dao.fresherActivity.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rwbase.common.enu.eActivityType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name="fresheractivity")
@SynClass
public class FresherActivityItem implements IMapItem, FresherActivityItemIF{
	@Id
	private String id;
	private String ownerId;
	@CombineSave
	private String currentValue;   //当前值
	@CombineSave
	private eActivityType type;
	@CombineSave
	private int cfgId;
	@CombineSave
	private long startTime;
	@CombineSave
	private long endTime;
	@CombineSave
	private boolean isFinish;   //是否完成
	@CombineSave
	private boolean isGiftTaken;//是否领奖
	@CombineSave
	private boolean isClosed;   //领取完奖励关闭该item 	
	
	public String getId() {
		return id;
	}
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
	public void setId(String id) {
		this.id = id;
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
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
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
}
