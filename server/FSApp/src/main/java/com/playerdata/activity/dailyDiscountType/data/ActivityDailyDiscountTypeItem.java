package com.playerdata.activity.dailyDiscountType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;



import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_dailydiscounttype_item")
public class ActivityDailyDiscountTypeItem implements  IMapItem {

	@Id
	private String id;
	
	private String userId;// 对应的角色Id
	@CombineSave
    private String cfgid;

	@CombineSave
	private boolean closed = false;

	@CombineSave
	private long lastTime;
	
	@CombineSave
	private List<ActivityDailyDiscountTypeSubItem> subItemList = new ArrayList<ActivityDailyDiscountTypeSubItem>();
	
	
	@CombineSave
	private String version ;

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

	public String getCfgid() {
		return cfgid;
	}

	public void setCfgid(String cfgid) {
		this.cfgid = cfgid;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public List<ActivityDailyDiscountTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityDailyDiscountTypeSubItem> subItemList) {
		this.subItemList = subItemList;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setId(String id) {
		this.id = id;
	}	
}
