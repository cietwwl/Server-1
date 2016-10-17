package com.rw.handler.groupCompetition.data.group;

import com.rw.handler.groupCompetition.util.GCompStageType;

/**
 * 
 * 帮派争霸的阶段
 * 
 * @author CHEN.P
 *
 */
public interface IGCompStage {
	
	/**
	 * 
	 * 获取配置的id
	 * 
	 * @return
	 */
	public String getStageCfgId();
	
	/**
	 * 
	 * @return
	 */
	public GCompStageType getStageType();

	/**
	 * 
	 * @param preStage
	 * @param startPara
	 */
	public void onStageStart(IGCompStage preStage, Object startPara);
	
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
