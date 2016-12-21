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
	private boolean closed = false;
	
	@CombineSave
	private int finishCount;	// 已经完成的数量
	
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

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public synchronized int getFinishCount() {
		return finishCount;
	}

	public synchronized void setFinishCount(int finishCount) {
		this.finishCount = finishCount;
	}

	public boolean isHasViewed() {
		return hasViewed;
	}

	public void setHasViewed(boolean hasViewed) {
		this.hasViewed = hasViewed;
	}
	
	public synchronized void reset(){
		finishCount = 0;
	}

	@Override
	public void setSubItemList(List<Object> subItemList) {
		
	}

	@Override
	public List<Object> getSubItemList() {
		return Collections.emptyList();
	}
}
