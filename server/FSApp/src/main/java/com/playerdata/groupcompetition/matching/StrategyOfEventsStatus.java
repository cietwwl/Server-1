package com.playerdata.groupcompetition.matching;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.playerdata.groupcompetition.util.GCompEventsStatus;

enum StrategyOfEventsStatus implements EventsStatusHandler {

	REST_STRATEGY(GCompEventsStatus.REST) {

		@Override
		public void handleStatus(AgainstMatchingTask task) {
			task.setCurrentEventsStatus(GCompEventsStatus.REST);
			task.pause();
		}

	},
	TEAM_EVENTS_STRATEGY(GCompEventsStatus.TEAM_EVENTS) {

		@Override
		public void handleStatus(AgainstMatchingTask task) {
			task.setCurrentEventsStatus(GCompEventsStatus.TEAM_EVENTS);
			task.start();
		}

	},
	PERSONAL_EVENTS_STRATEGY(GCompEventsStatus.PERSONAL_EVENTS) {

		@Override
		public void handleStatus(AgainstMatchingTask task) {
			task.setCurrentEventsStatus(GCompEventsStatus.PERSONAL_EVENTS);
			task.beforePersonalEvents();
			task.start();
		}

	},
	FINISH_STRATEGY(GCompEventsStatus.FINISH) {

		@Override
		public void handleStatus(AgainstMatchingTask task) {
			task.setCurrentEventsStatus(GCompEventsStatus.FINISH);
			task.end();
		}
		
	};
	
	private static final Map<GCompEventsStatus, StrategyOfEventsStatus> _all;
	
	static {
		Map<GCompEventsStatus, StrategyOfEventsStatus> map = new HashMap<GCompEventsStatus, StrategyOfEventsStatus>();
		StrategyOfEventsStatus[] array = values();
		for (StrategyOfEventsStatus temp : array) {
			map.put(temp.eventStatus, temp);
		}
		_all = Collections.unmodifiableMap(map);
	}
	
	private final GCompEventsStatus eventStatus;

	private StrategyOfEventsStatus(GCompEventsStatus pEventStatus) {
		this.eventStatus = pEventStatus;
	}
	
	public static StrategyOfEventsStatus getByType(GCompEventsStatus status) {
		return _all.get(status);
	}
}
