package com.playerdata.groupcompetition.data;

import com.playerdata.groupcompetition.util.GCompStageType;

/**
 * 
 * 帮派争霸的阶段
 * 
 * @author CHEN.P
 *
 */
public interface IGCStage {
	
	/**
	 * 
	 * @return
	 */
	public GCompStageType getStageType();

	/**
	 * 通知此阶段开始
	 */
	public void onStageStart(IGCStage preStage);
	
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
