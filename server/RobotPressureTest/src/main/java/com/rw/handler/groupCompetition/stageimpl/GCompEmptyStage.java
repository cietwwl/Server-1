package com.rw.handler.groupCompetition.stageimpl;

import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * 过渡的空白阶段
 * 
 * @author CHEN.P
 *
 */
public class GCompEmptyStage implements IGCompStage {

	private long _stageEndTime;
	
	public GCompEmptyStage(GroupCompetitionStageCfg cfg) {
		
	}
	
	@Override
	public String getStageCfgId() {
		return null;
	}
	
	@Override
	public GCompStageType getStageType() {
		return GCompStageType.EMPTY;
	}
	
	@Override
	public void onStageStart(IGCompStage preStage, Object startPara) {
		
	}

	@Override
	public void onStageEnd() {
		
	}

	@Override
	public long getStageEndTime() {
		return _stageEndTime;
	}

}
