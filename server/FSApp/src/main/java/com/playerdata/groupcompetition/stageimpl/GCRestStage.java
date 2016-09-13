package com.playerdata.groupcompetition.stageimpl;

import com.playerdata.groupcompetition.data.IGCStage;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * 帮派争霸休整阶段
 * 
 * @author CHEN.P
 *
 */
public class GCRestStage implements IGCStage {

	public GCRestStage(GroupCompetitionStageCfg cfg) {

	}
	
	@Override
	public void onStageStart(IGCStage preStage) {
		
	}

	@Override
	public void onStageEnd() {
		
	}

	@Override
	public long getStageEndTime() {
		return 0;
	}

}
