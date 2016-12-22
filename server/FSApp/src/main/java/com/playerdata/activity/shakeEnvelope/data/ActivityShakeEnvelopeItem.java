package com.playerdata.activity.shakeEnvelope.data;

import java.util.Collections;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activityCommon.activityType.ActivityTypeItemIF;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityShakeEnvelopeItem implements ActivityTypeItemIF<Object> {

	@Id
	private Integer id;
	
	private String userId;	//对应的角色Id
	
	@CombineSave
	private String cfgId;	//活动的id
	
	@CombineSave
	private int version;	//cfg的版本号
	
	@CombineSave
	private int count;	//可以领取的红包数量
	
	@CombineSave
	private long currentOverTime;	//当前可领取红包的失效时间
	
	@CombineSave
	private boolean hasViewed;	//是否已经查看过该活动
	
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getCurrentOverTime() {
		return currentOverTime;
	}

	public void setCurrentOverTime(long currentOverTime) {
		this.currentOverTime = currentOverTime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isHasViewed() {
		return hasViewed;
	}

	public void setHasViewed(boolean hasViewed) {
		this.hasViewed = hasViewed;
	}

	@Override
	public void setSubItemList(List<Object> subItemList) {
		
	}

	@Override
	public List<Object> getSubItemList() {
		return Collections.emptyList();
	}

	@Override
	public void reset() {
		
	}
}
