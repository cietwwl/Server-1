package com.playerdata.groupcompetition.util;

/**
 * @Author HC
 * @date 2016年9月23日 下午12:16:46
 * @desc
 **/

public interface GCompMatchConst {

	/**
	 * 帮派争霸匹配的类型
	 */
	public static enum GCompMatchType {
		/**
		 * 队伍匹配
		 */
		TEAM_MATCH(1),
		/**
		 * 个人匹配
		 */
		PERSONAL_MATCH(2);

		public final int type;// 匹配类型

		private GCompMatchType(int type) {
			this.type = type;
		}
	}

	/**
	 * 帮派争霸匹配状态
	 */
	public static enum GCompMatchState {
		/**
		 * 未匹配
		 */
		NON_MATCH(1),
		/**
		 * 匹配中
		 */
		MATCHING(2),
		/**
		 * 可以开始战斗
		 */
		START_BATTLE(3);

		public final int state;// 匹配状态

		private GCompMatchState(int state) {
			this.state = state;
		}
	}
}