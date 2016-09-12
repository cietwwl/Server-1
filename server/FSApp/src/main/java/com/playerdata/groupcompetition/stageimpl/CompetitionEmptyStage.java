package com.playerdata.groupcompetition.stageimpl;

import com.playerdata.groupcompetition.data.CompetitionStage;

/**
 * 
 * 过渡的空白阶段
 * 
 * @author CHEN.P
 *
 */
public class CompetitionEmptyStage implements CompetitionStage {

	private long _stageEndTime;
	
	@Override
	public void onStageStart(CompetitionStage preStage) {
		
	}

	@Override
	public void onStageEnd() {
		
	}

	@Override
	public long getStageEndTime() {
		return _stageEndTime;
	}

}
