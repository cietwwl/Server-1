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
	 * 通知此阶段开始
	 */
	public void onStageStart(CompetitionStage preStage);
	
	/**
	 * 通知此阶段结束
	 */
	public void onStageEnd();
	
	/**
	 * 
	 * 获取阶段本次结束的日期时间的毫秒形式
	 * 
	 * @return
	 */
	public long getStageEndTime();
}
