package com.playerdata.activity.retrieve.data;

import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 记录当天各功能的数据
 * @author Administrator
 *
 */
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class RewardBackTodaySubItem {
	private String id;//功能id
	private int maxCount;//今天最大次数
	private int count;//今天已参与次数
	private HashMap<Integer, TeamBattleRecord> teambattleCountMap = new HashMap<Integer, TeamBattleRecord>();//心魔录记录

	public int getMaxCount() {
		return maxCount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	
	public HashMap<Integer, TeamBattleRecord> getTeambattleCountMap() {
		return teambattleCountMap;
	}
	public void setTeambattleCountMap(HashMap<Integer, TeamBattleRecord> teambattleCountMap) {
		this.teambattleCountMap = teambattleCountMap;
	}


	
}
