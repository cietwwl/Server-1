package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 
 * 帮派争霸的挑战记录
 * 
 * @author CHEN.P
 *
 */
@SynClass
public class GCompPersonFightingRecord {

	private String text; // 描述
	private int continueWin; // 连胜次数
	private int personalScore; // 本次挑战记录的个人积分
	private int groupScore; // 本次挑战记录的帮派积分
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
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
