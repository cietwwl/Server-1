package com.playerdata.groupcompetition.util;

import java.util.Calendar;

import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageControlCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageControlCfg;

public class GCompUtil {

	public static int computeBeginIndex(GCEventsType eventsType) {
		GCEventsType firstType = GroupCompetitionMgr.getInstance().getFisrtTypeOfCurrent();
		int usedIndex = 0;
		switch (eventsType) {
		case TOP_16:
			return 1; // 初赛从1开始
		case TOP_8:
			// 视乎是不是从16强开始而处理
			break;
		case QUATER:
			usedIndex = 4; // 最少会经历1/4 共4场比赛
			break;
		case FINAL:
			usedIndex = 4 + 2; // 最少会经历1/4和1/2决赛，共六场比赛
			break;
		}
		if (firstType == GCEventsType.TOP_16) {
			usedIndex += 8;
		}
		return usedIndex + 1;
	}
	
	/**
	 * 
	 * 计算帮派战的开始时间
	 * 
	 * @param type 开始的时间类型，这个用于从{@link GroupCompetitionStageControlCfgDAO#getByType(int))
	 * @param relativeTime 以这个相对时间为基准进行偏移
	 * @return
	 */
	public static long calculateGroupCompetitionStartTime(GCompStartType type, long relativeTime) {
		Calendar instance = Calendar.getInstance();
		if (relativeTime > 0) {
			instance.setTimeInMillis(relativeTime);
		}
		GroupCompetitionStageControlCfg cfg = GroupCompetitionStageControlCfgDAO.getInstance().getByType(type.sign);
		IReadOnlyPair<Integer, Integer> time = cfg.getStartTimeInfo();
		if (cfg.getStartWeeks() > 0) {
			instance.add(Calendar.WEEK_OF_YEAR, cfg.getStartWeeks());
			instance.set(Calendar.DAY_OF_WEEK, cfg.getStartDayOfWeek());
		} else {
			if (instance.get(Calendar.DAY_OF_WEEK) >= cfg.getStartDayOfWeek()) {
				instance.add(Calendar.WEEK_OF_YEAR, 1);
			}
			instance.set(Calendar.DAY_OF_WEEK, cfg.getStartDayOfWeek());
		}
		instance.set(Calendar.HOUR_OF_DAY, time.getT1());
		if (time.getT2() > 0) {
			instance.set(Calendar.MINUTE, time.getT2());
		}
		instance.set(Calendar.SECOND, 0);
		return instance.getTimeInMillis();
	}
}
