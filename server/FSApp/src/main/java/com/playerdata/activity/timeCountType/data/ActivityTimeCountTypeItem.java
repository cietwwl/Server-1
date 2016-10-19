package com.playerdata.activity.timeCountType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfg;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_time_count_type_item")
public class ActivityTimeCountTypeItem implements  RoleExtProperty {

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
	private long lastCountTime = 0;
	
	@CombineSave
	private List<ActivityTimeCountTypeSubItem> subItemList = new ArrayList<ActivityTimeCountTypeSubItem>();
	
	
	@CombineSave
	private String version ;
	
	
	
	public void reset(ActivityTimeCountTypeCfg cfg,List<ActivityTimeCountTypeSubItem> sublist){
		closed = false;
		count = 0;
		version = cfg.getVersion();
		subItemList = sublist;		
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<ActivityTimeCountTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityTimeCountTypeSubItem> subItemList) {
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

	public long getLastCountTime() {
		return lastCountTime;
	}

	public void setLastCountTime(long lastCountTime) {
		this.lastCountTime = lastCountTime;
	}

}
