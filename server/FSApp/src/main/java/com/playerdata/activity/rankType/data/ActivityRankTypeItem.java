package com.playerdata.activity.rankType.data;

import java.util.Collections;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.OwnerId;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityRankTypeItem  implements ActivityTypeItemIF<Object> {

	@Id
	private int id;
	
	@OwnerId
	private String userId;// 对应的角色Id

	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;

	@CombineSave
	private int version;
	
	@CombineSave
	private boolean taken = false;//活动昂大奖是否领取

	@CombineSave
	private String reward;
	
	@CombineSave
	private String emailId;
	
	@CombineSave
	private String fashionReward ;
	
	@CombineSave
	private long redPointLastTime;	
	
	@CombineSave
	private String enumId;
	
	@CombineSave
	private boolean isTouchRedPoint;
	
	
	public String getEnumId() {
		return enumId;
	}

	public void setEnumId(String enumId) {
		this.enumId = enumId;
	}

	public long getRedPointLastTime() {
		return redPointLastTime;
	}

	public String getFashionReward() {
		return fashionReward;
	}
	
	public void setRedPointLastTime(long redPointLastTime) {
		this.redPointLastTime = redPointLastTime;
	}	

	public void setFashionReward(String fashionReward) {
		this.fashionReward = fashionReward;
	}
	
	public boolean isTouchRedPoint() {
		return isTouchRedPoint;
	}

	public void setTouchRedPoint(boolean isTouchRedPoint) {
		this.isTouchRedPoint = isTouchRedPoint;
	}
	
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
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

	public boolean isTaken() {
		return taken;
	}

	public void setTaken(boolean taken) {
		this.taken = taken;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public void setSubItemList(List<Object> subItemList) {
		
	}

	@Override
	public List<Object> getSubItemList() {
		return Collections.emptyList();
	}

	@Override
	public boolean isHasViewed() {
		return isTouchRedPoint;
	}

	@Override
	public void setHasViewed(boolean hasViewed) {
		this.isTouchRedPoint = hasViewed;
	}

	@Override
	public synchronized void reset() {
		//做每日重置的事情，如果没有则不需要
	}	
}
