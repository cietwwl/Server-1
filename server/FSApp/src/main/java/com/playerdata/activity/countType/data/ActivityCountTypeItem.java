package com.playerdata.activity.countType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_counttype_item")
public class ActivityCountTypeItem implements  IMapItem {

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
	private List<ActivityCountTypeSubItem> subItemList = new ArrayList<ActivityCountTypeSubItem>();
	
	
	@CombineSave
	private String version ;
	
	@CombineSave
	private long redPointLastTime;
	
	@CombineSave
	private String enumId;
	
	
	
	
	
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
	
	@CombineSave
	private boolean isTouchRedPoint;	

	public boolean isTouchRedPoint() {
		return isTouchRedPoint;
	}

	public void setTouchRedPoint(boolean isTouchRedPoint) {
		this.isTouchRedPoint = isTouchRedPoint;
	}

	public void reset(ActivityCountTypeCfg cfg,List<ActivityCountTypeSubItem> sublist){
		closed = false;
		count=0;
		version = cfg.getVersion();
		subItemList = sublist;
		isTouchRedPoint = false;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}


	//重置活动
	public void reset(){
		subItemList = new ArrayList<ActivityCountTypeSubItem>();
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

	

	
	
	
}
