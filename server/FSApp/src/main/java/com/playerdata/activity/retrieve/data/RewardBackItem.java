package com.playerdata.activity.retrieve.data;

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
@Table(name = "activity_retrievetype_item")
public class RewardBackItem implements IMapItem{
	@Id
	private String id ;
	
	private String userId;
	
	@CombineSave
	private long lastSingleTime;//上一次触发隔日的登陆或5点刷新时间
	
	@CombineSave
	private List<RewardBackTodaySubItem> todaySubitemList = new ArrayList<RewardBackTodaySubItem>();
	
	@CombineSave
	private List<RewardBackSubItem> subList = new ArrayList<RewardBackSubItem>();
	
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}


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


	public void setId(String id) {
		this.id = id;
	}


	public List<RewardBackSubItem> getSubList() {
		return subList;
	}


	public void setSubList(List<RewardBackSubItem> subList) {
		this.subList = subList;
	}
	
	
	
}
