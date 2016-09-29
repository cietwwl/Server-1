package com.rwbase.dao.groupcompetition.pojo;

public class GCompBasicScoreCfg {
	
	/**
	 * 胜利
	 */
	public static final int BATTLE_RESULT_WIN = 1;
	/**
	 * 失败
	 */
	public static final int BATTLE_RESULT_LOSE = 2;
	/**
	 * 平局
	 */
	public static final int BATTLE_RESULT_DRAW = 3;

	private int battleResult;
	private int personalScore;
	private int groupScore;
	
	public int getBattleResult() {
		return battleResult;
	}
	
	public int getPersonalScore() {
		return personalScore;
	}
	
	public int getGroupScore() {
		return groupScore;
	}
}
