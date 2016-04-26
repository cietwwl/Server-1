package com.common.playerFilter;

import com.playerdata.Player;

public class PlayerFilter {

	public static boolean isInRange(Player player, PlayerFilterCondition condition){
		
		int type = condition.getType();
		
		FilterType filterType = FilterType.valueOf(type);
		
		boolean isInRange = false;
		switch (filterType) {
			case LEVEL_SPAN:
				int level = player.getLevel();
				isInRange = isValueInRange(condition, level);
				break;
			case CREATE_TIME:
				long createTime = player.getUserDataMgr().getCreateTime() / 1000;
				isInRange = isValueInRange(condition, createTime);
				break;
	
			default:
				break;
		}
		return isInRange;
		
	}
	
	private static boolean isValueInRange(PlayerFilterCondition condition, long value){
		return condition.getMinValue() <= value && value <= condition.getMaxValue();
	}
	
}
