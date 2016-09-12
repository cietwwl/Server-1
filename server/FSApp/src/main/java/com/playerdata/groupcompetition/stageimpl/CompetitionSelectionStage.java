package com.playerdata.groupcompetition.stageimpl;

import java.util.Calendar;

import com.playerdata.groupcompetition.cfg.CompetitionCommonCfgDAO;
import com.playerdata.groupcompetition.data.CompetitionStage;
import com.rw.fsutil.common.IReadOnlyPair;

/**
 * 
 * 帮派争霸海选阶段
 * 
 * @author CHEN.P
 *
 */
public class CompetitionSelectionStage implements CompetitionStage {
	
	private long _stageEndTime; // 阶段结束的时间
	private CompetitionCommonCfgDAO _competitionCommonCfgDAO;
	
	public CompetitionSelectionStage() {
		_competitionCommonCfgDAO = CompetitionCommonCfgDAO.getInstance();
	}

	/**
	 * 
	 * 结算本阶段的结束时间
	 * 
	 * @param endOnThisWeek 是否在本周结束
	 * @return
	 */
	private long calculateEndTime(boolean endOnThisWeek) {
		IReadOnlyPair<Integer, IReadOnlyPair<Integer, Integer>> selectionEndDateTime = _competitionCommonCfgDAO.getCfg().getSelectionStageEndTimeInfos();
		Calendar currentDateTime = Calendar.getInstance();
		if(!endOnThisWeek) {
			currentDateTime.add(Calendar.WEEK_OF_YEAR, 1);
		}
		IReadOnlyPair<Integer, Integer> timeInfo = selectionEndDateTime.getT2();
		currentDateTime.set(Calendar.DAY_OF_WEEK, selectionEndDateTime.getT1());
		currentDateTime.set(Calendar.HOUR_OF_DAY, timeInfo.getT1());
		currentDateTime.set(Calendar.MINUTE, timeInfo.getT2());
		currentDateTime.set(Calendar.SECOND, 0);
		return currentDateTime.getTimeInMillis();
	}
	
	@Override
	public void onStageStart(CompetitionStage preStage) {
		this._stageEndTime = calculateEndTime(false);
	}

	@Override
	public void onStageEnd() {

	}

	@Override
	public long getStageEndTime() {
		return _stageEndTime;
	}
	
}
