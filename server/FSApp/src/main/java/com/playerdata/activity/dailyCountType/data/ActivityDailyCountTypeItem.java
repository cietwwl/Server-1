package com.playerdata.activity.dailyCountType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfg;
import com.playerdata.activity.rateType.cfg.ActivityRateTypeCfgDAO;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_dailycounttype_item")
public class ActivityDailyCountTypeItem implements  IMapItem {

	@Id
	private String id;
	
	private String userId;// 对应的角色Id
	
	@CombineSave
	private int count;

	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;

	
	@CombineSave
	private List<ActivityDailyCountTypeSubItem> subItemList = new ArrayList<ActivityDailyCountTypeSubItem>();
	
	
	@CombineSave
	private String version ;
	
	public void reset(ActivityDailyCountTypeCfg cfg){
		closed = false;
		count=0;
		version = cfg.getVersion();
		
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}


	//重置活动
	public void reset(){
		subItemList = new ArrayList<ActivityDailyCountTypeSubItem>();
		count = 0;
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

	public List<ActivityDailyCountTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityDailyCountTypeSubItem> subItemList) {
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
