package com.playerdata.groupcompetition.util;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * 冠军队伍的保存数据
 * 
 * @author CHEN.P
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class ChampionGroupData {

	@JsonProperty("1")
	private String groupId; // 帮会id
	@JsonProperty("2")
	private String groupName; // 帮会的名字
	@JsonProperty("3")
	private String groupIcon; // 帮会的图标
	@JsonProperty("4")
	private String groupPresident; // 帮主名字
	@JsonProperty("5")
	private List<String> groupVPs; // 副帮主的名字
	@JsonProperty("6")
	private int score; // 夺冠时的积分
	
	public String getGroupId() {
		return groupId;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public String getGroupIcon() {
		return groupIcon;
	}
	
	public String getGroupPresident() {
		return groupPresident;
	}
	
	public List<String> getGroupVPs() {
		return groupVPs;
	}
	
	/**
	 * 
	 * 获取夺冠时的积分
	 * 
	 * @return
	 */
	public int getScore() {
		return score;
	}
	
	
}
