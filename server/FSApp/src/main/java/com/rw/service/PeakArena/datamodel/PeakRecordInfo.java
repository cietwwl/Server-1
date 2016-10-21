package com.rw.service.PeakArena.datamodel;

import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class PeakRecordInfo implements RoleExtProperty {

	@Id
	@JsonProperty("1")
	private int id; // 记录的唯一id
	@JsonProperty("2")
	private PeakArenaResultType result; // 胜负
	@JsonProperty("3")
	private int placeUp; // 交换名次后计算排名变化
	@JsonProperty("4")
	private String enemyName; // 对手的名字
	@JsonProperty("5")
	private String enemyHeadImage; // 对手的头像
	@JsonProperty("6")
	private int enemyLevel; // 等级
	@JsonProperty("7")
	private long time; // 挑战的时间
	@JsonProperty("8")
	private String enemyUserId; // 对手的userId
	@JsonProperty("9")
	private PeakArenaActionType actionType; // 防守还是挑战
	@JsonProperty("10")
	private List<PeakRecordDetail> details; // 详细信息
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
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
	
	public String getEnemyUserId() {
		return enemyUserId;
	}
	
	public void setEnemyUserId(String userId) {
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
