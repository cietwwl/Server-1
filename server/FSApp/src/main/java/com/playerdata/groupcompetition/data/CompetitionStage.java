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
	public void onStageStart(CompetitionStage preStage);
	
	/**
	 * 阶段是否已经结束
	 */
	public boolean isStageEnd();
}
