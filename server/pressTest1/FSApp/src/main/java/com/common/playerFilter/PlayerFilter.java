package com.common.playerFilter;

import com.common.playerFilter.PlayerFilterCondition.FilterType;
import com.playerdata.Player;

public class PlayerFilter {

	public static boolean isInRange(Player player, PlayerFilterCondition condition){
		
		int type = condition.getType();
		
		PlayerFilterCondition.FilterType filterType = FilterType.valueOf(type);
		
		boolean isInRange = false;
		switch (filterType) {
			case LEVEL_SPAN:
				int level = player.getLevel();
				isInRange = isValueInRange(condition, level);
				break;
			case CREATE_TIME:
				long createTime = player.getUserDataMgr().getCreateTime();
				isInRange = isValueInRange(condition, createTime);
				break;
	
			default:
				break;
		}
		return isInRange;
		
	}
	
	private static boolean isValueInRange(PlayerFilterCondition condition, long value){
		return condition.getMinValue() < value && value < condition.getMaxValue();
	}
	
}
