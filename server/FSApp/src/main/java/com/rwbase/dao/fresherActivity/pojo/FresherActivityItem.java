package com.rwbase.dao.fresherActivity.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.PlayerExtProperty;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rwbase.common.enu.eActivityType;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class FresherActivityItem implements PlayerExtProperty, FresherActivityItemIF {

	private String currentValue; // 当前值
	private eActivityType type;

	private int cfgId;

	private long startTime;

	private long endTime;

	@NonSave
	private boolean isFinish; // 是否完成
	@NonSave
	private boolean isGiftTaken;// 是否领奖
	@NonSave
	private boolean isClosed; // 领取完奖励关闭该item
	@IgnoreSynField
	private byte status;

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
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
		return (status & 1) > 0;
	}

	public boolean isGiftTaken() {
		return (status & 2) > 0;
	}

	public boolean isClosed() {
		return (status & 4) > 0;
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
		status = (byte)(status | 1);
	}

	public void setGiftTaken(boolean isGiftTaken) {
		status = (byte)(status | 2);
	}

	public void setClosed(boolean isClosed) {
		status = (byte)(status | 4);
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

	@Override
	public Integer getId() {
		return cfgId;
	}

}
