package com.playerdata.groupcompetition;

import java.lang.reflect.Field;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.groupcompetition.util.ChampionGroupData;
import com.rw.fsutil.util.jackson.JsonUtil;

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
	
	private static final GroupCompetitionSaveData _INSTANCE = new GroupCompetitionSaveData();
	
	@JsonProperty("1")
	private int _heldTimes; // 已经举办的次数
	@JsonProperty("2")
	private long _lastHeldTimeMillis; // 最后一次举办的时间
	@JsonProperty("3")
	private GCCurrentData _currentData; // 当前的赛事的保存数据
	@JsonProperty("4")
	private List<ChampionGroupData> _championGroups; // 历届冠军
	
	static void initDataFromDB(String attribute) {
		GroupCompetitionSaveData data = JsonUtil.readValue(attribute, GroupCompetitionSaveData.class);
		Field[] allFields = GroupCompetitionSaveData.class.getDeclaredFields();
		Field tempField;
		for (int i = 0, size = allFields.length; i < size; i++) {
			tempField = allFields[i];
			if (tempField.isAnnotationPresent(JsonProperty.class)) {
				try {
					tempField.set(_INSTANCE, tempField.get(data));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static final GroupCompetitionSaveData getInstance() {
		return _INSTANCE;
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
}
