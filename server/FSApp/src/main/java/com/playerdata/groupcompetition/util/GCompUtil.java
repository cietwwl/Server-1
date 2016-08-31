package com.playerdata.groupcompetition.util;

import java.util.Calendar;

import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageCfgDAO;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageControlCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageControlCfg;

public class GCompUtil {

	/**
	 * 
	 * <pre>
	 * 计算赛事类型的开始索引
	 * 索引的计算规则如下图：
	 * 
	 * 帮派A------                      ------帮派E
	 *         1  |------        ------|  3
	 * 帮派B------       |   7  |       ------帮派F
	 *               5   |------|   6
	 * 帮派C------       |      |       ------帮派G
	 *         2  |------        ------|  4
	 * 帮派D------                      ------帮派H
	 * 
	 * 最初的索引统一从1开始。
	 * 当比赛是从8强开始，左上角为1号比赛，左下角为2号比赛，右上角为3号比赛，右下角为4号比赛
	 * 当比赛是从16强开始，左边从上至下依次为1、2、3、4，右边从上至下依次为5、6、7、8
	 * 晋级之后的索引计算，也是按照从左边开始，按照初赛是哪一种类型累加。
	 * 例如，初赛是8强，则晋级之后索引从5开始计算，16强则是从9开始
	 * </pre>
	 * @param eventsType
	 * @return
	 */
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
		GroupCompetitionStageCfg firstStage = GroupCompetitionStageCfgDAO.getInstance().getCfgById(String.valueOf(cfg.getStageDetailList().get(0)));
		IReadOnlyPair<Integer, Integer> time = firstStage.getStartTimeInfo();
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
		if (time.getT2() >= 0) {
			instance.set(Calendar.MINUTE, time.getT2());
		}
		instance.set(Calendar.SECOND, 0);
		return instance.getTimeInMillis();
	}
	
	public static long calculateEndTimeOfStage(String stageCfgId) {
		GroupCompetitionStageCfg cfg = GroupCompetitionStageCfgDAO.getInstance().getCfgById(String.valueOf(stageCfgId));
		Calendar currentDateTime = Calendar.getInstance();
		IReadOnlyPair<Integer, Integer> endTimeInfo = cfg.getEndTimeInfo();
		currentDateTime.add(Calendar.DAY_OF_YEAR, cfg.getLastDays());
		currentDateTime.set(Calendar.HOUR_OF_DAY, endTimeInfo.getT1());
		currentDateTime.set(Calendar.MINUTE, endTimeInfo.getT2());
		currentDateTime.set(Calendar.SECOND, 0);
		return currentDateTime.getTimeInMillis();
	}
	
	public static void sendMarquee(String msg) {
		MainMsgHandler.getInstance().sendPmdNotId(msg);
	}
}
