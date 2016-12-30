package com.playerdata.activity.fortuneCatType.data;

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
public class ActivityFortuneCatTypeItem implements ActivityTypeItemIF<ActivityFortuneCatTypeSubItem> {

	@Id
	private Integer id;
	
	@OwnerId
	private String userId;// 对应的角色Id

	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;
	
	@CombineSave
	private List<ActivityFortuneCatTypeSubItem> subItemList = new ArrayList<ActivityFortuneCatTypeSubItem>();
	
	@CombineSave
	private int version;
	
	@CombineSave
	private long redPointLastTime;
	
	@CombineSave
	private int times ;
	
	@CombineSave
	private boolean isTouchRedPoint;	
	
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

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
	
	
	public Integer getId() {
		return id;
	}

	public List<ActivityFortuneCatTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityFortuneCatTypeSubItem> subItemList) {
		this.subItemList = subItemList;
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

	@Override
	public void setId(int id) {
		this.id = id;
		this.cfgId = String.valueOf(id);
	}

	public int getVersion() {
		return version;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public boolean isHasViewed() {
		return isTouchRedPoint;
	}

	@Override
	public void setHasViewed(boolean hasViewed) {
		isTouchRedPoint = hasViewed;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}
}
