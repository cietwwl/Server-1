package com.playerdata.activity.timeCardType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_counttype_item")
public class ActivityTimeCardTypeItem implements  IMapItem {

	@Id
	private String id;
	
	private String userId;// 对应的角色Id
	
	@CombineSave
	private int count;
	
	@CombineSave
	private String group;
	

	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;
	
	@CombineSave
	private List<ActivityTimeCardTypeSubItem> subItemList = new ArrayList<ActivityTimeCardTypeSubItem>();
	
	
	
	@CombineSave
	private long activityLoginTime;


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

	@CombineSave
	private long startTime;
	
	@CombineSave
	private long endTime;
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	

	public long getActivityLoginTime() {
		return activityLoginTime;
	}

	public void setActivityLoginTime(long activityLoginTime) {
		this.activityLoginTime = activityLoginTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}


	public List<ActivityTimeCardTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityTimeCardTypeSubItem> subItemList) {
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

	
	

	
	
	
}
