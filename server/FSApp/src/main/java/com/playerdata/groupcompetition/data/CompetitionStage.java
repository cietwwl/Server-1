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
	public void onStart(CompetitionStage preStage);
	
	/**
	 * 结束（结束是否应该由Stage自身控制？）
	 */
	public void onEnd();
}
