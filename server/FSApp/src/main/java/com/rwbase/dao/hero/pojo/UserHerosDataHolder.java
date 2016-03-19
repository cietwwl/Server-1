package com.rwbase.dao.hero.pojo;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.hero.UserHeroDAO;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserHerosDataHolder {// 战斗数据

	private UserHeroDAO userHeroDAO = UserHeroDAO.getInstance();
	// private TableUserHero userHero;
	// private boolean modified = false;
	private static eSynType synType = eSynType.USER_HEROS;
	private final String userId;

	public UserHerosDataHolder(String userId) {
		this.userId = userId;
	}

	public void syn(Player player, int version) {
		TableUserHero userHero = get();
		if (userHero != null) {
			ClientDataSynMgr.synData(player, userHero, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserHerosDataHolder", "#syn()", "find TableUserHero fail:" + userId);
		}

	}

	public TableUserHero get() {
		return userHeroDAO.get(userId);
	}

	public void update(Player player) {
		userHeroDAO.update(userId);
		TableUserHero userHero = get();
		if (userHero != null) {
			ClientDataSynMgr.updateData(player, userHero, synType, eSynOpType.UPDATE_SINGLE);
		}else{
			GameLog.error("UserHerosDataHolder", "#update()", "find TableUserHero fail:" + userId);
		}
	}

	public void flush() {
	}

}
