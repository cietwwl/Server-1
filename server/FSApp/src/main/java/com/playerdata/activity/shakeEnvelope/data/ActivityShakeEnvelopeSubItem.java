package com.playerdata.activity.shakeEnvelope.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeSubItemIF;
import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityShakeEnvelopeSubItem implements ActivityTypeSubItemIF{
	
	private boolean isGet = false;
	
	private long startTime;
	
	private long endTime;
	
	private boolean isInformed = false;

	public boolean isGet() {
		return isGet;
	}

	public void setGet(boolean isGet) {
		this.isGet = isGet;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean isInformed() {
		return isInformed;
	}

	public void setInformed(boolean isInformed) {
		this.isInformed = isInformed;
	}

	@Override
	public String getCfgId() {
		return String.valueOf(startTime);
	}

	@Override
	public void setCfgId(String cfgId) {
		//Do nothing
	}
}
