package com.rw.service.PeakArena.datamodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class PeakRecordInfo {

	@JsonProperty("1")
	private PeakArenaResultType result; // 
	@JsonProperty("2")
	private int placeUp; // 交换名次后计算排名变化
	@JsonProperty("3")
	private String enemyName; // 对手的名字
	@JsonProperty("4")
	private String enemyHeadImage; // 对手的头像
	@JsonProperty("5")
	private int enemyLevel; // 等级
	@JsonProperty("6")
	private long time; // 挑战的时间
	@JsonProperty("7")
	private String enemyUserId; // 对手的userId
	@JsonProperty("8")
	private PeakArenaActionType actionType; // 
	@JsonProperty("9")
	private List<PeakRecordDetail> details; // 详细信息
	
	public PeakArenaResultType getResult() {
		return result;
	}

	public void setResult(PeakArenaResultType result) {
		this.result = result;
	}
	
	public int getPlaceUp() {
		return placeUp;
	}
	
	public void setPlaceUp(int placeUp) {
		this.placeUp = placeUp;
	}
	
	public String getEnemyName() {
		return enemyName;
	}
	
	public void setEnemyName(String name) {
		this.enemyName = name;
	}
	
	public String getHeadImage() {
		return enemyHeadImage;
	}
	
	public void setHeadImage(String headImage) {
		this.enemyHeadImage = headImage;
	}
	
	public int getLevel() {
		return enemyLevel;
	}
	
	public void setLevel(int level) {
		this.enemyLevel = level;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public String getUserId() {
		return enemyUserId;
	}
	
	public void setUserId(String userId) {
		this.enemyUserId = userId;
	}
	
	public PeakArenaActionType getActionType() {
		return actionType;
	}

	public void setActionType(PeakArenaActionType actionType) {
		this.actionType = actionType;
	}
	
	public List<PeakRecordDetail> getDetails() {
		return details;
	}
	
	public void setDetails(List<PeakRecordDetail> details) {
		this.details = details;
	}
	
}
