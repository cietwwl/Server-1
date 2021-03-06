package com.playerdata.activity.retrieve.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_retrievetype_item")
public class RewardBackItem implements RoleExtProperty{
	@Id
	private Integer id ;
	@OwnerId
	private String userId;
	
	@CombineSave
	private long lastSingleTime;//上一次触发隔日的登陆或5点刷新时间
	
	@CombineSave
	private long lastAddPowerTime;//上一次增加找回体力的时间
	
	@CombineSave
	private List<RewardBackTodaySubItem> todaySubitemList = new ArrayList<RewardBackTodaySubItem>();
	
	@CombineSave
	private List<RewardBackSubItem> subList = new ArrayList<RewardBackSubItem>();
	
	
	


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public long getLastSingleTime() {
		return lastSingleTime;
	}


	public void setLastSingleTime(long lastSingleTime) {
		this.lastSingleTime = lastSingleTime;
	}


	public List<RewardBackTodaySubItem> getTodaySubitemList() {
		return todaySubitemList;
	}


	public void setTodaySubitemList(List<RewardBackTodaySubItem> todaySubitemList) {
		this.todaySubitemList = todaySubitemList;
	}




	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public List<RewardBackSubItem> getSubList() {
		return subList;
	}


	public void setSubList(List<RewardBackSubItem> subList) {
		this.subList = subList;
	}


	public long getLastAddPowerTime() {
		return lastAddPowerTime;
	}


	public void setLastAddPowerTime(long lastAddPowerTime) {
		this.lastAddPowerTime = lastAddPowerTime;
	}
	
	
	
}
