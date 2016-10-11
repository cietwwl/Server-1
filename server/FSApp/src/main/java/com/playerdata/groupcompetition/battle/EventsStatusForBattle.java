package com.playerdata.groupcompetition.battle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.playerdata.groupcompetition.holder.GCompMatchDataHolder;
import com.playerdata.groupcompetition.util.GCompEventsStatus;

/**
 * @Author HC
 * @date 2016年10月10日 下午2:57:49
 * @desc
 **/

public enum EventsStatusForBattle implements EventsStatusForBattleHandler {

	NONE_STATUS(GCompEventsStatus.NONE) {

		@Override
		public void handler(GCompMatchBattleCheckTask task) {
			task.taskPause();

			// 清除数据
			GCompMatchDataHolder.getHolder().clearAllMatchData();
		}
	},

	PREPARE_STATUS(GCompEventsStatus.PREPARE) {

		@Override
		public void handler(GCompMatchBattleCheckTask task) {
			task.taskPause();

			// 清除数据
			GCompMatchDataHolder.getHolder().clearAllMatchData();
		}
	},

	REST_STATUS(GCompEventsStatus.REST) {

		@Override
		public void handler(GCompMatchBattleCheckTask task) {
			task.taskPause();

			// 清除数据
			GCompMatchDataHolder.getHolder().clearAllMatchData();
		}

	},
	TEAM_EVENTS_STATUS(GCompEventsStatus.TEAM_EVENTS) {

		@Override
		public void handler(GCompMatchBattleCheckTask task) {
			task.taskResume();
		}

	},
	PERSONAL_EVENTS_STATUS(GCompEventsStatus.PERSONAL_EVENTS) {

		@Override
		public void handler(GCompMatchBattleCheckTask task) {
			task.taskResume();
		}

	},
	FINISH_STATUS(GCompEventsStatus.FINISH) {

		@Override
		public void handler(GCompMatchBattleCheckTask task) {
			task.taskPause();

			// 清除数据
			GCompMatchDataHolder.getHolder().clearAllMatchData();
		}
	};

	private static final Map<GCompEventsStatus, EventsStatusForBattle> _all;

	static {
		Map<GCompEventsStatus, EventsStatusForBattle> map = new HashMap<GCompEventsStatus, EventsStatusForBattle>();
		EventsStatusForBattle[] array = values();
		for (EventsStatusForBattle temp : array) {
			map.put(temp.eventStatus, temp);
		}
		_all = Collections.unmodifiableMap(map);
	}

	private final GCompEventsStatus eventStatus;

	private EventsStatusForBattle(GCompEventsStatus pEventStatus) {
		this.eventStatus = pEventStatus;
	}

	public static EventsStatusForBattle getByType(GCompEventsStatus status) {
		return _all.get(status);
	}
}