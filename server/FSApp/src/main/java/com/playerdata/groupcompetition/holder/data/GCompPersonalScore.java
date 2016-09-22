package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompPersonalScore {

	private String name; // 名字
	private String headIcon; // 头像
	private int score; // 分数
	private int continueWin; // 连胜次数
	
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

	@Override
	public String toString() {
		return "GCompPersonalScore [name=" + name + ", score=" + score + ", continueWin=" + continueWin + "]";
	}
}
