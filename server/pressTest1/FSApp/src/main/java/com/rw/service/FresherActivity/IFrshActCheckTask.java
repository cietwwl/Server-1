package com.rw.service.FresherActivity;

import java.util.List;

import com.playerdata.Player;
import com.rwbase.common.enu.eActivityType;

public interface IFrshActCheckTask {
	/**
	 * 
	 * @param player
	 * @param activityType 开服活动类型
	 */
	public FresherActivityCheckerResult doCheck(Player player,eActivityType activityType);
}
