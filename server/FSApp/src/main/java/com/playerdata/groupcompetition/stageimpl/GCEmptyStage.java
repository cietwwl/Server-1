package com.playerdata.groupcompetition.stageimpl;

import com.playerdata.groupcompetition.data.IGCStage;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * 过渡的空白阶段
 * 
 * @author CHEN.P
 *
 */
public class GCEmptyStage implements IGCStage {

	private long _stageEndTime;
	
	public GCEmptyStage(GroupCompetitionStageCfg cfg) {
		
	}
	
	@Override
	public void onStageStart(IGCStage preStage) {
		
	}

	@Override
	public void onStageEnd() {
		
	}

	@Override
	public long getStageEndTime() {
		return _stageEndTime;
	}

}
