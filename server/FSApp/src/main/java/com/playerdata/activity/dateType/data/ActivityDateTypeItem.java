package com.playerdata.activity.dateType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.dateType.cfg.ActivityDateTypeSubCfg;
import com.playerdata.activity.dateType.cfg.ActivityDateTypeSubCfgDAO;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_datetype_item")
public class ActivityDateTypeItem implements  IMapItem {

	@Id
	private String id;
	
	private String userId;// 对应的角色Id

	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;
	
	@CombineSave
	private List<ActivityDateTypeSubItem> subItemList = new ArrayList<ActivityDateTypeSubItem>();
	
	@CombineSave
	private int count;//总计数

	@CombineSave
	private int day;//第几天
	
	@CombineSave
	private boolean taken = false;//活动昂大奖是否领取


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ActivityDateTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityDateTypeSubItem> subItemList) {
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
	

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
	

	public ActivityDateTypeSubItem getSubItemById(String subItemId){
		ActivityDateTypeSubItem target = null;
		for (ActivityDateTypeSubItem subItem : subItemList) {
			if(StringUtils.equals(subItemId, subItem.getCfgId())){
				target = subItem;
			} 
		}
		return target;
	}

	public ActivityDateTypeSubItem getCurentDaySubItem() {
		ActivityDateTypeSubItem target = null;
		for (ActivityDateTypeSubItem subItem : subItemList) {
			ActivityDateTypeSubCfg subItemCfg = ActivityDateTypeSubCfgDAO.getInstance().getById(subItem.getCfgId());
			if(subItemCfg.getDay() == day){
				target = subItem;
			} 
		}
		return target;
	}

	public boolean isTaken() {
		return taken;
	}

	public void setTaken(boolean taken) {
		this.taken = taken;
	}
	

	
	
	
}
