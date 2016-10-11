package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompMember implements Comparable<GCompMember> {

	@IgnoreSynField
	private String userId; // 玩家id
	private int lv; // 玩家等级
	// @IgnoreSynField
	private int score; // 积分
	private int totalWinTimes; // 胜利的次数
	private int continueWins; // 连胜次数
	private int maxContinueWins; // 最大连胜次数
	private int groupScore; // 贡献的帮派积分
	private String headIcon; // 头像
	@IgnoreSynField
	private int robotContinueWins; // 作为机器人的连胜次数
	@IgnoreSynField
	private String userName; //
	@IgnoreSynField
	private long lastUpdateTime; // 上一次更新的时间
	@IgnoreSynField
	private static IGCompMemberAgent _robotAgent = new GCompMemberRobotAgent();
	@IgnoreSynField
	private static IGCompMemberAgent _commonAgent = new GCompMemberCommonAgent();

	public GCompMember(String userId, String userName, int lv, String headIcon) {
		this.userId = userId;
		this.lv = lv;
		this.userName = userName;
		this.headIcon = headIcon;
	}

	public static IGCompMemberAgent getAgent(boolean isRobot) {
		if (isRobot) {
			return _robotAgent;
		} else {
			return _commonAgent;
		}
	}

	void updateScore(int offset) {
		this.score += offset;
		lastUpdateTime = System.currentTimeMillis();
	}

	int getContinueWins() {
		return continueWins;
	}

	void incWinTimes() {
		this.totalWinTimes++;
		this.continueWins++;
		if (this.maxContinueWins < this.continueWins) {
			this.maxContinueWins = this.continueWins;
		}
		lastUpdateTime = System.currentTimeMillis();
	}

	void resetContinueWins() {
		this.continueWins = 0;
		lastUpdateTime = System.currentTimeMillis();
	}

	void incRobotContinueWins() {
		this.robotContinueWins++;
	}

	void resetRobotContinueWins() {
		this.robotContinueWins = 0;
	}

	int getRobotContinueWins() {
		return this.robotContinueWins;
	}

	void updateGroupScore(int offset) {
		this.groupScore += offset;
	}

	public int getLv() {
		return lv;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public int getScore() {
		return score;
	}

	public int getTotalWinTimes() {
		return totalWinTimes;
	}

	public int getMaxContinueWins() {
		return maxContinueWins;
	}

	public int getGroupScore() {
		return groupScore;
	}
	
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public int compareTo(GCompMember o) {
		return this.lv > o.lv ? -1 : 1;
	}

}
