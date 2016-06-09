package com.playerdata.mgcsecret.data;

import java.util.ArrayList;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.rw.fsutil.util.DateUtils;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserMagicSecretHolder {

	private static class InstanceHolder{
		private static UserMagicSecretHolder instance = new UserMagicSecretHolder();
		private static UserMagicSecretDao userMagicSecretDao = UserMagicSecretDao.getInstance();
	}
	
	private static eSynType synType = eSynType.MagicSecretData;
	
	private UserMagicSecretHolder(){ }
	
	public static UserMagicSecretHolder getInstance(){
		return InstanceHolder.instance;
	}

	public void syn(Player player) {
		UserMagicSecretData userMagicSecret = get(player);
		if (userMagicSecret != null) {
			if(isDailyFirstLogin(userMagicSecret.getLastResetTime())){
				MagicSecretMgr.getInstance().resetDailyMSInfo(player);
			}
			ClientDataSynMgr.synData(player, userMagicSecret, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserMagicSecretHolder", "#syn()", "find UserMagicSecretData fail:" + player.getUserId());
		}
	}

	public UserMagicSecretData get(Player player) {
		String userId = player.getUserId();
		UserMagicSecretData umsData = InstanceHolder.userMagicSecretDao.get(userId);
		if(umsData.getSecretArmy() == null) {
			umsData.setSecretArmy("");
			syn(player);
		}
		return umsData;
	}

	public void update(Player player) {
		InstanceHolder.userMagicSecretDao.update(player.getUserId());
		UserMagicSecretData userMagicSecret = get(player);
		if (userMagicSecret != null) {
			ClientDataSynMgr.updateData(player, userMagicSecret, synType, eSynOpType.UPDATE_SINGLE);
		} else {
			GameLog.error("UserMagicSecretHolder", "#update()", "find UserMagicSecretData fail:" + player.getUserId());
		}
	}
	
	public void update(Player player, String fieldName){
		InstanceHolder.userMagicSecretDao.update(player.getUserId());
		UserMagicSecretData userMagicSecret = get(player);
		if (userMagicSecret != null) {
			ArrayList<String> list = new ArrayList<String>(1);
			list.add(fieldName);
			ClientDataSynMgr.synDataFiled(player, userMagicSecret, synType, list);
		} else {
			GameLog.error("UserMagicSecretHolder", "#updateF()", "find UserMagicSecretData fail:" + player.getUserId());
		}
	}

	public void flush() {
	}

	/**
	 * 每日重置的比对时间
	 * 05:00:00
	 * @param lastResetTime
	 * @return
	 */
	public boolean isDailyFirstLogin(long lastResetTime){
		return true;
		//return DateUtils.isResetTime(5, 0, 0, lastResetTime);
	}
}
