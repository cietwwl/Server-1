package com.playerdata.groupcompetition.data;

/**
 * 
 * 帮派争霸的阶段
 * 
 * @author CHEN.P
 *
 */
public interface CompetitionStage {

	/**
	 * 开始
	 */
	public void onStart();
	
	/**
	 * 结束
	 */
	public void onEnd();
}
