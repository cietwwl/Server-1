package com.playerdata.groupcompetition;

import java.lang.reflect.Field;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

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
	private int heldTimes; // 已经举办的次数
	@JsonProperty("2")
	private long lastHeldTimeMillis; // 最后一次举办的时间
	@JsonProperty("3")
	private GroupCompetitionCurrentData currentData; // 当前的赛事的保存数据
	
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
		this.heldTimes++;
	}

	/**
	 * 
	 * 获取帮派争霸已经举办的次数
	 * 
	 * @return
	 */
	public int getHeldTimes() {
		return heldTimes;
	}
	
	/**
	 * 
	 * 更新最后一次举办的时间
	 * 
	 * @param timeMillis
	 */
	public void updateLastHeldTime(long timeMillis) {
		lastHeldTimeMillis = timeMillis;
	}
	
	/**
	 * 
	 * 获取最后一次争霸赛举办的时间
	 * 
	 * @return
	 */
	public long getLastHeldTimeMillis() {
		return lastHeldTimeMillis;
	}
}
