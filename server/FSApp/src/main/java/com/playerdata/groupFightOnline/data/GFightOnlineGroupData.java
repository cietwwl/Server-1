package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupFightOnline.dataForClient.GFFightRecord;
import com.rw.fsutil.dao.annotation.CombineSave;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
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
	@IgnoreSynField
	private List<GFFightRecord> recordList = new ArrayList<GFFightRecord>();
	
	@CombineSave
	private int defenderCount;	//总的防守队伍数
	
	@CombineSave
	private int aliveCount;		//存活队伍数
	
	private int version = 0;
	
	@IgnoreSynField
	private byte[] recordLock = new byte[0];
	@IgnoreSynField
	private static final int LIST_SIZE = 50;

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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void addFightRecord(GFFightRecord record){
		synchronized (recordLock) {
			if(recordList.size() >= LIST_SIZE){
				Collections.sort(recordList);
				recordList.set(LIST_SIZE - 1, record);
			} else recordList.add(record);
		}
	}
}
