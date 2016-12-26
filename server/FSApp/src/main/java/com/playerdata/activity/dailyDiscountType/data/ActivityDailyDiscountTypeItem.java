package com.playerdata.activity.dailyDiscountType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_dailydiscounttype_item")
public class ActivityDailyDiscountTypeItem implements ActivityTypeItemIF<ActivityDailyDiscountTypeSubItem> {

	@Id
	private int id;
	
	@OwnerId
	private String userId;// 对应的角色Id
	
	@CombineSave
    private String cfgId;

	@CombineSave
	private boolean closed = false;

	@CombineSave
	private long lastTime;
	
	@CombineSave
	private List<ActivityDailyDiscountTypeSubItem> subItemList = new ArrayList<ActivityDailyDiscountTypeSubItem>();
	
	@CombineSave
	private int version ;
	
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

	public int getVersion() {
		return version;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

//	public void reset(ActivityDailyDiscountTypeCfg targetCfg) {
//		this.closed = false;
//		this.lastTime = System.currentTimeMillis();
//		this.cfgId = targetCfg.getId();
//		this.version = targetCfg.getVersion();
//		this.subItemList = ActivityDailyDiscountTypeCfgDAO.getInstance().newSubItemList(targetCfg);
//		isTouchRedPoint = false;
//	}

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
		
	}
}
