package com.playerdata.groupcompetition;

public interface GCConstance {

	public static interface CompetitionStartType {
		
		/**
		 * 以开服时间为偏移
		 */
		public static final int START_TYPE_SERVER_TIME_OFFSET = 1;
		
		/**
		 * 以自然时间为偏移
		 */
		public static final int START_TYPE_NUTRAL_TIME_OFFSET = 2;
	}
	
	public static interface CompetitionStageType {
		
		/**
		 * 海选期
		 */
		public static final int COMPETITION_STAGE_TYPE_SELECTION = 1;
		
		/**
		 * 赛事期
		 */
		public static final int COMPETITION_STAGE_TYPE_EVENTS = 2;
		
		/**
		 * 休整期
		 */
		public static final int COMPETITION_STAGE_TYPE_REST = 3;
		
		/**
		 * 过渡期
		 */
		public static final int COMPETITION_STAGE_TYPE_EMPTY = 4;
	}
}
