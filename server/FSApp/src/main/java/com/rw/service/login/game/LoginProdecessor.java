package com.rw.service.login.game;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.dataaccess.GameOperationFactory;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.PlayerPredecessor;

public class LoginProdecessor implements PlayerPredecessor {

	@Override
	public void run(String userId) {
		User user = UserDataDao.getInstance().getByUserId(userId);
		if (user == null) {
			GameLog.error("LoginProdecessor", userId, "pre load get user fail");
			return;
		}
		Hero mainRole = FSHeroMgr.getInstance().getMainRoleHero(userId);
		if (mainRole == null) {
			GameLog.error("LoginProdecessor", userId, "pre load get main role fail");
			return;
		}
		long createTime = user.getCreateTime();
		int level = mainRole.getLevel();
		// 对登录玩家进行预加载
		GameOperationFactory.getLoadOperation().execute(userId, createTime, level);
	}

}
