package com.playerdata.groupcompetition.holder.data;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class GCompGroupScoreRecord {

	@JsonProperty("1")
	private String groupId; // 帮派的id
	@JsonProperty("2")
	private String groupName; // 帮派的名字
	@JsonProperty("3")
	private int score; // 当前的积分
	@JsonProperty("4")
	private String groupIcon; // 帮派的icon
	
	public static GCompGroupScoreRecord createNew(String groupId, String groupName, String groupIcon) {
		GCompGroupScoreRecord instance = new GCompGroupScoreRecord();
		instance.groupId = groupId;
		instance.groupName = groupName;
		instance.groupIcon = groupIcon;
		return instance;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public String getGroupIcon() {
		return groupIcon;
	}

	@Override
	public String toString() {
		return "GCompGroupScore [groupId=" + groupId + ", score=" + score + "]";
	}
}
