package com.playerdata.groupcompetition.stageimpl;

import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.util.GCompRestStartPara;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * 帮派争霸休整阶段
 * 
 * @author CHEN.P
 *
 */
public class GCompRestStage implements IGCompStage {
	
	private long _endTime;
	private String _stageCfgId;

	public GCompRestStage(GroupCompetitionStageCfg cfg) {
		_stageCfgId = cfg.getCfgId();
	}
	
	@Override
	public String getStageCfgId() {
		return _stageCfgId;
	}
	
	@Override
	public GCompStageType getStageType() {
		return GCompStageType.REST;
	}
	
	@Override
	public void onStageStart(IGCompStage preStage, Object startPara) {
		if (startPara != null && startPara instanceof GCompRestStartPara) {
			this._endTime = ((GCompRestStartPara) startPara).getEndTime();
		} else {
			this._endTime = GCompUtil.calculateEndTimeOfStage(this._stageCfgId);
		}
	}

	@Override
	public void onStageEnd() {
		
	}

	@Override
	public long getStageEndTime() {
		return _endTime;
	}

}
