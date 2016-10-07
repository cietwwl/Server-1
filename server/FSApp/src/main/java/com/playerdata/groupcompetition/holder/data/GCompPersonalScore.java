package com.playerdata.groupcompetition.holder.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown=true)
public class GCompPersonalScore {

	private String name; // 名字
	private String headIcon; // 头像
	private int score; // 分数
	private int continueWin; // 连胜次数
	private int groupScore; // 贡献的帮派积分
	
	public String getName() {
		return name;
	}
	
	public String getHeadIcon() {
		return headIcon;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getContinueWin() {
		return continueWin;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setContinueWin(int continueWin) {
		this.continueWin = continueWin;
	}
	
	public void setGroupScore(int pGroupScore) {
		this.groupScore = pGroupScore;
	}
	
	public int getGroupScore() {
		return groupScore;
	}

	@Override
	public String toString() {
		return "GCompPersonalScore [name=" + name + ", score=" + score + ", continueWin=" + continueWin + "]";
	}
}
