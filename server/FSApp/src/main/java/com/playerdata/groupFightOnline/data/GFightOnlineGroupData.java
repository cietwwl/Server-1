package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Id;

import jdk.nashorn.internal.ir.annotations.Ignore;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupFightOnline.dataExtend.GFFightRecord;
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
	private List<GFFightRecord> recordList = new ArrayList<GFFightRecord>();
	
	@Ignore
	private byte[] recordLock = new byte[0];
	@Ignore
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
	
	public void addFightRecord(GFFightRecord record){
		synchronized (recordLock) {
			if(recordList.size() >= LIST_SIZE){
				Collections.sort(recordList);
				recordList.set(LIST_SIZE - 1, record);
			} else recordList.add(record);
		}
	}
}
