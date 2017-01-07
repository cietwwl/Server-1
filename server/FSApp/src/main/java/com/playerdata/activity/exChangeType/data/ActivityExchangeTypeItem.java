package com.playerdata.activity.exChangeType.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityExchangeTypeItem  implements ActivityTypeItemIF<ActivityExchangeTypeSubItem>{

	@Id
	private int id;
	
	@OwnerId
	private String userId;// 对应的角色Id

	@CombineSave
	private String enumId;
	
	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;
	
	@CombineSave
	private List<ActivityExchangeTypeSubItem> subItemList = new ArrayList<ActivityExchangeTypeSubItem>();
	
	@CombineSave
	private long lasttime;
	
	@CombineSave
	private int version ;
	
	@CombineSave
	private long redPointLastTime;
	
	/**
	 * 记录subcfgid,在此内的id均为之前兑换道具可兑换的对象，以对应显示新红点
	 */
	@CombineSave
	private List<String> historyRedPoint = new ArrayList<String>();	
	
	@CombineSave
	private boolean isTouchRedPoint;
	
	public String getEnumId() {
		return enumId;
	}

	public void setEnumId(String enumId) {
		this.enumId = enumId;
	}

	public List<String> getHistoryRedPoint() {
		return historyRedPoint;
	}

	public void setHistoryRedPoint(List<String> historyRedPoint) {
		this.historyRedPoint = historyRedPoint;
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
	
//	/**版本刷新*/
//	public void reset(ActivityExchangeTypeCfg targetCfg,List<ActivityExchangeTypeSubItem> list){
//		this.cfgId = String.valueOf(targetCfg.getId());
//		this.closed = false;
//		this.version = String.valueOf(targetCfg.getVersion());
//		subItemList = list;
//		isTouchRedPoint = false;
//		historyRedPoint = new ArrayList<String>();
//	}

	public long getLasttime() {
		return lasttime;
	}

	public void setLasttime(long lasttime) {
		this.lasttime = lasttime;
	}



	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<ActivityExchangeTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityExchangeTypeSubItem> subItemList) {
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

	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getVersion() {
		return version;
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
		// TODO Auto-generated method stub
		
	}	
}
