package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.util.ChampionGroupData;
import com.playerdata.groupcompetition.util.GCompStageType;

/**
 * 
 * 帮派争霸全局数据
 * 
 * @author CHEN.P
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
class GroupCompetitionGlobalData {
	
	@JsonProperty("1")
	private int _heldTimes; // 已经举办的次数
	@JsonProperty("2")
	private long _lastHeldTimeMillis; // 最后一次举办的时间（从Selection阶段开始）
	@JsonProperty("3")
	private GCompEventsRecord _currentEventsRecord; // 当前的赛事的保存数据
	@JsonProperty("4")
	private List<ChampionGroupData> _championGroups; // 历届冠军
	@JsonProperty("5")
	private int _againstIdRecord; // 当前的赛事id的最大记录
	@JsonProperty("6")
	private GCompStageType _currentStageType; // 当前的阶段
	@JsonProperty("7")
	private long _currentStageEndTime; // 当前阶段的结束时间
	
	public static GroupCompetitionGlobalData createEmpty() {
		GroupCompetitionGlobalData data = new GroupCompetitionGlobalData();
		data._heldTimes = 0;
		data._lastHeldTimeMillis = 0;
		data._championGroups = new ArrayList<ChampionGroupData>();
		return data;
	}
	
	/**
	 * 更新举办次数，使举办次数+1
	 */
	void increaseHeldTimes() {
		this._heldTimes++;
	}

	/**
	 * 
	 * 获取帮派争霸已经举办的次数
	 * 
	 * @return
	 */
	public int getHeldTimes() {
		return _heldTimes;
	}
	
	/**
	 * 
	 * 更新最后一次举办的时间
	 * 
	 * @param timeMillis
	 */
	void updateLastHeldTime(long timeMillis) {
		_lastHeldTimeMillis = timeMillis;
	}
	
	/**
	 * 
	 * 获取最后一次争霸赛举办的时间
	 * 
	 * @return
	 */
	public long getLastHeldTimeMillis() {
		return _lastHeldTimeMillis;
	}
	
	/**
	 * 
	 * @param data
	 */
	void setCurrentRecord(GCompEventsRecord data) {
		this._currentEventsRecord = data;
	}
	
	/**
	 * 
	 * 获取当前比赛的数据记录
	 * 
	 * @return
	 */
	public GCompEventsRecord getCurrentEventsRecord() {
		return _currentEventsRecord;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAgainstIdRecord() {
		return _againstIdRecord;
	}
	
	/**
	 * 
	 * @param pId
	 */
	void setAgainstIdRecord(int pId) {
		this._againstIdRecord = pId;
	}
	
	/**
	 * 
	 * 获取当前的阶段类型
	 * 
	 * @return
	 */
	public GCompStageType getCurrentStageType() {
		return this._currentStageType;
	}
	
	/**
	 * 
	 * 设置当前的阶段类型
	 * 
	 * @param currentStageType
	 */
	void setCurrentStageType(GCompStageType currentStageType) {
		this._currentStageType = currentStageType;
	}
	
	/**
	 * 
	 * 获取当前阶段的结束时间
	 * 
	 * @return
	 */
	public long getCurrentStageEndTime() {
		return _currentStageEndTime;
	}
	
	/**
	 * 
	 * 设置当前阶段的结束时间
	 * 
	 * @param endTime
	 */
	void setCurrentStageEndTime(long endTime) {
		this._currentStageEndTime = endTime;
	}
	
	void addChampion(GCGroup group) {
		this._championGroups.add(ChampionGroupData.createChampionGroupData(group));
	}
}
