package com.playerdata.activity.rankType.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.rankType.cfg.ActivityRankTypeCfg;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "activity_RankType_item")
public class ActivityRankTypeItem implements  IMapItem {

	@Id
	private String id;
	
	private String userId;// 对应的角色Id

	@CombineSave
	private String cfgId;
	
	@CombineSave
	private boolean closed = false;

	@CombineSave
	private String version;	
	
	@CombineSave
	private boolean taken = false;//活动昂大奖是否领取

	@CombineSave
	private String reward ;
	
	@CombineSave
	private String emailId ;
	
	
	public String getId() {
		return id;
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



	public String getVersion() {
		return version;
	}



	public void setVersion(String version) {
		this.version = version;
	}



	public void setId(String id) {
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



	public void reset(ActivityRankTypeCfg targetCfg) {
		this.taken = false;
		this.closed = false;
		this.version = targetCfg.getVersion();
		this.reward = null;
		this.emailId=null;
	}	
}
