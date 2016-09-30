package com.rwbase.dao.fresherActivity.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rwbase.common.enu.eActivityType;

@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class FresherActivityItem implements RoleExtProperty, FresherActivityItemIF {

	private final byte FinishTrue = 1;
	private final byte GiftTakenTrue = 2;
	private final byte CloseTrue = 4;
	
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
		isFinish();
		isGiftTaken();
		isClosed();
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
		this.isFinish = (status & 1) > 0;
		return isFinish;
	}

	public boolean isGiftTaken() {
		this.isGiftTaken = (status & 2) > 0;
		return isGiftTaken;
	}

	public boolean isClosed() {
		this.isClosed = (status & 4) > 0;
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
		if (isFinish) {
			status = (byte) (status | FinishTrue);
		}else{
			status = (byte)(status & (0xFF - FinishTrue));
		}
		this.isFinish = isFinish;
	}

	public void setGiftTaken(boolean isGiftTaken) {
		if (isGiftTaken) {
			status = (byte) (status | GiftTakenTrue);
		} else {
			status = (byte) (status & (0xFF - GiftTakenTrue));
		}
		this.isGiftTaken = isGiftTaken;
	}

	public void setClosed(boolean isClosed) {
		if (isClosed) {
			status = (byte) (status | CloseTrue);
		} else {
			status = (byte) (status & 0xFF - CloseTrue);
		}
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

	@Override
	public Integer getId() {
		return cfgId;
	}

}
