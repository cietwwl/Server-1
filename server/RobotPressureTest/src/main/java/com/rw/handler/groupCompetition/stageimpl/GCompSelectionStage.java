package com.rw.handler.groupCompetition.stageimpl;

import com.rw.handler.groupCompetition.data.group.IGCompStage;
import com.rw.handler.groupCompetition.util.GCompRestStartPara;
import com.rw.handler.groupCompetition.util.GCompStageType;

/**
 * 
 * 帮派争霸海选阶段
 * 
 * @author CHEN.P
 *
 */
public class GCompSelectionStage implements IGCompStage {
	
	private long _stageEndTime; // 阶段结束的时间
	private String _stageCfgId; // 阶段的配置id
	
	public GCompSelectionStage(GroupCompetitionStageCfg cfg) {
		_stageCfgId = cfg.getCfgId();
	}
	
	@Override
	public String getStageCfgId() {
		return _stageCfgId;
	}
	
	@Override
	public GCompStageType getStageType() {
		return GCompStageType.SELECTION;
	}
	
	@Override
	public void onStageStart(IGCompStage preStage, Object startPara) {
		if (startPara != null && startPara instanceof GCompRestStartPara) {
			this._stageEndTime = ((GCompRestStartPara) startPara).getEndTime();
		} else {
			this._stageEndTime = GCompUtil.calculateEndTimeOfStage(_stageCfgId);
		}
		GCompUtil.sendMarquee(GCompTips.getTipsEnterSelectionStage());
	}

	@Override
	public void onStageEnd() {
		GCompHistoryDataMgr.getInstance().setSelectedGroupIds(GCompUtil.getTopCountGroupsFromRank());
		GroupCompetitionMgr.getInstance().updateEndTimeOfSelection(System.currentTimeMillis());
	}

	@Override
	public long getStageEndTime() {
		return _stageEndTime;
	}
	
}
