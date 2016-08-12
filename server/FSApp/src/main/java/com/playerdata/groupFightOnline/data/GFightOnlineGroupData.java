package com.playerdata.groupFightOnline.data;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gf_group_data")
public class GFightOnlineGroupData {
	
	@Id
	private String groupID;
	
	@CombineSave
	private int biddingCount;  // 帮派竞标用的令牌数
	
	@CombineSave
	private int resourceID;		// 帮派竞标的资源点
	
	@CombineSave
	private long lastBidTime;	// 上次竞标时间，主要用于排名
	
	@CombineSave
	private int defenderCount;	//总的防守队伍数
	
	@CombineSave
	private int aliveCount;		//存活队伍数
//	
//	private int version = 0;	//初始版本号
	
	@IgnoreSynField
	private long lastkillTime;	//最后击杀的时间

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public int getBiddingCount() {
		return biddingCount;
	}

	public void setBiddingCount(int biddingCount) {
		this.biddingCount = biddingCount;
	}

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}
	
	public long getLastBidTime() {
		return lastBidTime;
	}

	public void setLastBidTime(long lastBidTime) {
		this.lastBidTime = lastBidTime;
	}

	public int getDefenderCount() {
		return defenderCount;
	}

	public void setDefenderCount(int defenderCount) {
		this.defenderCount = defenderCount;
	}
	
	public void addDefenderCount(int count) {
		this.defenderCount += count;
		this.aliveCount += count;
	}

	public int getAliveCount() {
		return aliveCount;
	}

	public void setAliveCount(int aliveCount) {
		this.aliveCount = aliveCount;
	}
	
	public void deductAliveCount() {
		this.aliveCount--;
	}

//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}

	public long getLastkillTime() {
		return lastkillTime;
	}

	public void setLastkillTime(long lastkillTime) {
		this.lastkillTime = lastkillTime;
	}
	
	public void clearCurrentLoopData(){
		biddingCount = 0;  // 帮派竞标用的令牌数
		resourceID = 0;		// 帮派竞标的资源点
		lastBidTime = 0;	// 上次竞标时间，主要用于排名
		defenderCount = 0;	//总的防守队伍数
		aliveCount = 0;		//存活队伍数
		lastkillTime = 0;	//最后击杀的时间
	}
}
