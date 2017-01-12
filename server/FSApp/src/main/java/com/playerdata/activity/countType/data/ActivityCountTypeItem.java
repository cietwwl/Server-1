package com.playerdata.activity.countType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityCountTypeItem implements ActivityTypeItemIF<ActivityCountTypeSubItem> {

	@Id
	private Integer id;
	
	@OwnerId
	private String userId;// 对应的角色Id
	
	@CombineSave
	private int count;

	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;

	@CombineSave
	private List<ActivityCountTypeSubItem> subItemList = new ArrayList<ActivityCountTypeSubItem>();
	
	@CombineSave
	private int version;
	
	@CombineSave
	private long redPointLastTime;
	
	@CombineSave
	private String enumId;
	
	@CombineSave
	private boolean isTouchRedPoint;
	
	@IgnoreSynField
	private long lastAddCountTime = 0;
	
	public String getEnumId() {
		return enumId;
	}

	public void setEnumId(String enumId) {
		this.enumId = enumId;
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

	public synchronized void reset(ActivityCountTypeCfg cfg,List<ActivityCountTypeSubItem> sublist){
		cfgId = String.valueOf(cfg.getId());
		closed = false;
		count=0;
		version = cfg.getVersion();
		subItemList = sublist;
		isTouchRedPoint = false;
	}

	public int getVersion() {
		return version;
	}

	//重置活动
	public synchronized void reset(){
		subItemList = new ArrayList<ActivityCountTypeSubItem>();
		count = 0;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public synchronized int getCount() {
		return count;
	}

	public synchronized void setCount(int count) {
		this.count = count;
	}

	public List<ActivityCountTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityCountTypeSubItem> subItemList) {
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
	
	public long getLastAddCountTime() {
		return lastAddCountTime;
	}

	public void setLastAddCountTime(long lastAddCountTime) {
		this.lastAddCountTime = lastAddCountTime;
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
		this.isTouchRedPoint = hasViewed;
	}
}
