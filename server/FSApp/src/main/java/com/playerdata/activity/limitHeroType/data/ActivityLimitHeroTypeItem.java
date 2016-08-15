package com.playerdata.activity.limitHeroType.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_limitherotype_item")
public class ActivityLimitHeroTypeItem implements  IMapItem{
	@Id
	private String id ;
	
	private String userId;
	
	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;
	
	@CombineSave
	private String version ;
	
	@CombineSave
	private boolean isTouchRedPoint;
	
	@CombineSave
	private int integral;//积分
	
	@CombineSave
	private long lastSingleTime;//上一次免费单抽时间

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isTouchRedPoint() {
		return isTouchRedPoint;
	}

	public void setTouchRedPoint(boolean isTouchRedPoint) {
		this.isTouchRedPoint = isTouchRedPoint;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public long getLastSingleTime() {
		return lastSingleTime;
	}

	public void setLastSingleTime(long lastSingleTime) {
		this.lastSingleTime = lastSingleTime;
	}
	
	
}
