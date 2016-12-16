package com.playerdata.activity.dailyCountType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDailyTypeItem implements ActivityTypeItemIF<ActivityDailyTypeSubItem> {

	@Id
	private Integer id;
	
	@OwnerId
	private String userId;// 对应的角色Id
	
	@CombineSave
    private String cfgid;
	
	@CombineSave
	private boolean closed = false;

	@CombineSave
	private long lastTime;
	
	@CombineSave
	private List<ActivityDailyTypeSubItem> subItemList = new ArrayList<ActivityDailyTypeSubItem>();
	
	@CombineSave
	private int version ;
	
	@CombineSave
	private long redPointLastTime;
	
	@CombineSave
	private boolean isTouchRedPoint;
	
	public String getCfgid() {
		return cfgid;
	}

	public void setCfgid(String cfgid) {
		this.cfgid = cfgid;
	}
	
	public long getRedPointLastTime() {
		return redPointLastTime;
	}

	public void setRedPointLastTime(long redPointLastTime) {
		this.redPointLastTime = redPointLastTime;
	}
	
	public boolean isTouchRedPoint() {
		return isTouchRedPoint;
	}

	public void setTouchRedPoint(boolean isTouchRedPoint) {
		this.isTouchRedPoint = isTouchRedPoint;
	}


	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<ActivityDailyTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityDailyTypeSubItem> subItemList) {
		this.subItemList = subItemList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	@Override
	public void setCfgId(String cfgId) {
		this.cfgid = cfgId;
	}

	@Override
	public String getCfgId() {
		return cfgid;
	}

	@Override
	public boolean isHasViewed() {
		return this.isTouchRedPoint;
	}

	@Override
	public void setHasViewed(boolean hasViewed) {
		this.isTouchRedPoint = hasViewed;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
