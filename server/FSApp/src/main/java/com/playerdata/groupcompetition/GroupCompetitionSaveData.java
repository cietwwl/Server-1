package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.groupcompetition.util.ChampionGroupData;

/**
 * 
 * 帮派争霸数据保存数据
 * 
 * @author CHEN.P
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
class GroupCompetitionSaveData {
	
	@JsonProperty("1")
	private int _heldTimes; // 已经举办的次数
	@JsonProperty("2")
	private long _lastHeldTimeMillis; // 最后一次举办的时间
	@JsonProperty("3")
	private GCompCurrentData _currentData; // 当前的赛事的保存数据
	@JsonProperty("4")
	private List<ChampionGroupData> _championGroups; // 历届冠军
	@JsonProperty("5")
	private int _againstIdRecord = 0;
	
	public static GroupCompetitionSaveData createEmpty() {
		GroupCompetitionSaveData data = new GroupCompetitionSaveData();
		data._heldTimes = 0;
		data._lastHeldTimeMillis = 0;
		data._championGroups = new ArrayList<ChampionGroupData>();
		return data;
	}
	
	/**
	 * 更新举办次数，使举办次数+1
	 */
	public void increaseHeldTimes() {
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
	public void updateLastHeldTime(long timeMillis) {
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
	public void setCurrentData(GCompCurrentData data) {
		this._currentData = data;
	}
	
	/**
	 * 
	 * 获取当前比赛的数据记录
	 * 
	 * @return
	 */
	public GCompCurrentData getCurrentData() {
		return _currentData;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAgainstIdRecord() {
		return _againstIdRecord;
	}
	
	public void setAgainstIdRecord(int pId) {
		this._againstIdRecord = pId;
	}
}
