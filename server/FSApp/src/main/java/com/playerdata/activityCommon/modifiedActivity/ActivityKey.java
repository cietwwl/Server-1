package com.playerdata.activityCommon.modifiedActivity;

import com.rwbase.gameworld.GameWorldKey;

/**
 * 用于存储修改过的活动数据
 * 
 * <note>
 * 由于数据存储在game_world表，所有需要一个GameWorldKey
 * 但是为了方便管理活动类的存储，所以创建这个枚举管理活动类的key
 * </note>
 * @author aken
 *
 */
public enum ActivityKey {
	
	ACTIVITY_CHARGE_RANK(GameWorldKey.DAILY_RANKING_RESET),
	ACTIVITY_CONSUME_RANK(GameWorldKey.DAILY_RANKING_RESET),
	;
	
	ActivityKey(GameWorldKey worldKey) {
		this.worldKey = worldKey;
	}

	private GameWorldKey worldKey;

	public GameWorldKey getGameWorldKey() {
		return worldKey;
	}
}
