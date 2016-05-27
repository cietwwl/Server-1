package com.playerdata.mgcsecret.data;

import java.util.ArrayList;
import java.util.Calendar;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.SimpleArmyInfo;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.mgcsecret.cfg.MagicChapterCfg;
import com.playerdata.mgcsecret.cfg.MagicChapterCfgDAO;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserMagicSecretHolder {

	private UserMagicSecretDao userMagicSecretDao = UserMagicSecretDao.getInstance();
	private final String userId;
	private static eSynType synType = eSynType.MagicSecretData;

	public UserMagicSecretHolder(String userId) {
		this.userId = userId;
	}

	public void syn(Player player, int version) {
		UserMagicSecretData userMagicSecret = get();
		if (userMagicSecret != null) {
			if(isDailyFirstLogin(userMagicSecret.getLastResetTime())){
				player.getMagicSecretMgr().resetDailyMSInfo();
			}
			ClientDataSynMgr.synData(player, userMagicSecret, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserMagicSecretHolder", "#syn()", "find UserMagicSecretData fail:" + userId);
		}
	}

	public UserMagicSecretData get() {
		UserMagicSecretData umsData = userMagicSecretDao.get(userId);
		Player player = PlayerMgr.getInstance().find(userId);
		if(umsData.getSecretArmy() == null) {
			MagicChapterCfg mcCfg = MagicChapterCfgDAO.getInstance().getCfgById(MagicSecretMgr.CHAPTER_INIT_ID);
			if(player.getUserDataMgr().getUser().getLevel() < mcCfg.getLevelLimit()) return umsData;
			ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(userId, player.getHeroMgr().getHeroIdList());
			if(armyInfo == null) return umsData;
			ArrayList<String> uuids = new ArrayList<String>();
			for(ArmyHero hero : armyInfo.getHeroList())
				uuids.add(hero.getRoleBaseInfo().getId());
			SimpleArmyInfo sArmy = new SimpleArmyInfo();
			sArmy.setHeroIds(uuids);
			if(armyInfo.getArmyMagic() != null){
				sArmy.setLevel(armyInfo.getArmyMagic().getLevel());
				sArmy.setModelId(armyInfo.getArmyMagic().getModelId());
			}
			umsData.setSecretArmy(sArmy);
			syn(player, 0);
		}
		return umsData;
	}

	public void update(Player player) {
		userMagicSecretDao.update(userId);
		UserMagicSecretData userMagicSecret = get();
		if (userMagicSecret != null) {
			ClientDataSynMgr.updateData(player, userMagicSecret, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserMagicSecretHolder", "#update()", "find UserMagicSecretData fail:" + userId);
		}
	}
	
	public void update(Player player, String fieldName){
		userMagicSecretDao.update(userId);
		UserMagicSecretData userMagicSecret = get();
		if (userMagicSecret != null) {
			ArrayList<String> list = new ArrayList<String>(1);
			list.add(fieldName);
			ClientDataSynMgr.synDataFiled(player, userMagicSecret, synType, list);
		} else {
			GameLog.error("UserMagicSecretHolder", "#updateF()", "find UserMagicSecretData fail:" + userId);
		}
	}

	public void flush() {
	}

	public boolean isDailyFirstLogin(long lastResetTime){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 5);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return false;
//		if(lastResetTime < cal.getTimeInMillis())
//			return System.currentTimeMillis() > cal.getTimeInMillis();
//		return true;
	}
}
