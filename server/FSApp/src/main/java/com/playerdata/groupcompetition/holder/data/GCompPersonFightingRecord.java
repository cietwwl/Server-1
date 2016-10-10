package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.util.GCompBattleResult;

/**
 * 
 * 帮派争霸的挑战记录
 * 
 * @author CHEN.P
 *
 */
@SynClass
public class GCompPersonFightingRecord {
	
	private String offendName;	//主动方名字
	private String defendName;	//防御方名字
	private GCompBattleResult battleResult;	//战斗结果
	private int continueWin; // 连胜次数
	private int personalScore; // 本次挑战记录的个人积分
	private int groupScore; // 本次挑战记录的帮派积分
	
	public String getOffendName() {
		return offendName;
	}

	public void setOffendName(String offendName) {
		this.offendName = offendName;
	}

	public String getDefendName() {
		return defendName;
	}

	public void setDefendName(String defendName) {
		this.defendName = defendName;
	}

	public GCompBattleResult getBattleResult() {
		return battleResult;
	}

	public void setBattleResult(GCompBattleResult battleResult) {
		this.battleResult = battleResult;
	}

	public int getContinueWin() {
		return continueWin;
	}
	
	public void setContinueWin(int continueWin) {
		this.continueWin = continueWin;
	}
	
	public int getPersonalScore() {
		return personalScore;
	}
	
	public void setPersonalScore(int personalScore) {
		this.personalScore = personalScore;
	}
	
	public int getGroupScore() {
		return groupScore;
	}
	
	public void setGroupScore(int groupScore) {
		this.groupScore = groupScore;
	}
}
