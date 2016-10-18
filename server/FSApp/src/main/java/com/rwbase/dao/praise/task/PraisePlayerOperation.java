package com.rwbase.dao.praise.task;

import com.playerdata.Player;
import com.rwbase.common.timer.IPlayerOperable;
import com.rwbase.dao.praise.PraiseMgr;

/**
 * @Author HC
 * @date 2016年10月15日 上午11:52:34
 * @desc 点赞的角色要处理的内容
 **/

public class PraisePlayerOperation implements IPlayerOperable {

	/**
	 * 这里只关心不是机器人的角色
	 */
	@Override
	public boolean isInterestingOn(Player player) {
		return !player.isRobot();
	}

	@Override
	public void operate(Player player) {
		PraiseMgr.getMgr().checkOrResetData(System.currentTimeMillis(), player.getUserId());
	}
}