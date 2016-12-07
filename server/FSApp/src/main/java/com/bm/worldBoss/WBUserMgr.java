package com.bm.worldBoss;

import com.bm.worldBoss.cfg.WBAwardCfg;
import com.bm.worldBoss.cfg.WBAwardCfgDAO;
import com.bm.worldBoss.cfg.WBSettingCfg;
import com.bm.worldBoss.cfg.WBSettingCfgDAO;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBUserData;
import com.bm.worldBoss.data.WBUserDataDao;
import com.bm.worldBoss.data.WBUserDataHolder;
import com.bm.worldBoss.rank.WBHurtRankMgr;
import com.bm.worldBoss.service.WBHelper;
import com.playerdata.Player;


public class WBUserMgr {
	
	private static WBUserMgr wbMgr = new WBUserMgr();
	
	
	public static WBUserMgr getInstance(){
		return wbMgr;
	} 
	public void synWBUserData(Player player, int wbUserDataVersion){	
//		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
//		int bossVersion = wbUserData.getBossVersion();
//		if(!WBMgr.getInstance().isSameBoss(bossVersion)){
//			//wbUserData.clean(bossVersion);  //不可以在同步的时候检查并清除上一次记录，否则下一个boss在准备期就看不到上一个boss战的记录
//			WBUserDataHolder.getInstance().update(player);
//		}else{
//		}
		WBUserDataHolder.getInstance().syn(player, wbUserDataVersion);		
		
	}	
	
	public void addBuff(Player player, String buffCfgId){	
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		wbUserData.addBuff(buffCfgId);		
		WBUserDataHolder.getInstance().update(player);
		
	}
	
	public void fightBeginUpdate(Player player){
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		
		WBSettingCfg settingCfg = WBSettingCfgDAO.getInstance().getCfg();
		long curTime = System.currentTimeMillis();
		wbUserData.setLastFightTime(curTime);
		wbUserData.setFightCdTime(curTime + settingCfg.getCDInMilli() + settingCfg.getBattleTime());
		wbUserData.cleanLastHurt();
		
//		System.out.println("-------------------last cd time:" + DateUtils.getDateTimeFormatString(wbUserData.getFightCdTime(), "yyyy-MM-dd HH:mm:ss"));
		WBUserDataHolder.getInstance().update(player);
	}
	
	/**
	 * 更新伤害值，返回总伤害
	 * @param player
	 * @param hurt
	 * @return
	 */
	public long fightUpdate(Player player, long hurt){	
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		
		wbUserData.addLastHurt(hurt);
		wbUserData.addTotalHurt(hurt);
		WBUserDataHolder.getInstance().update(player);
		
		WBHurtRankMgr.addOrUpdate(player);
		return wbUserData.getLastHurt();
	}
	
	public void fightEndUpdate(Player player){	
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());	
		
		long lastHurt = wbUserData.getLastHurt();
		
		int awardCoin = countAwardCoin(player, lastHurt);
		wbUserData.setLastAwardCoin(awardCoin);
		
		WBUserDataHolder.getInstance().update(player);
		
		WBHurtRankMgr.addOrUpdate(player);
//		System.err.println("world boss end battle, last hurt:" + wbUserData.getLastHurt() + ", totalHurt:" + wbUserData.getTotalHurt()
//				+",reward coin:" + awardCoin);
	}
	
	private int countAwardCoin(Player player, long totalHurt) {
		int level = player.getLevel();
		WBAwardCfg awardCfg = WBAwardCfgDAO.getInstance().getCfgById(String.valueOf(level));
		int awardCoin = 0;
		if(awardCfg!=null){
			double factor = awardCfg.getFactor();
			awardCoin = (int)(factor*totalHurt);
		}

		boolean success = WBHelper.addCoin(player, awardCoin);
		
		return success?awardCoin:0;
	}
	
	public boolean isInCD(Player player) {
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());		
		long fightCdTime = wbUserData.getFightCdTime();
		return fightCdTime!=0 && fightCdTime > System.currentTimeMillis();
	}
	

	public void cleanCD(Player player){
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		wbUserData.setFightCdTime(0);
		wbUserData.addCdBuyCount();		
		WBUserDataHolder.getInstance().update(player);
	}
	
	
	public void resetUserDataIfNeed(Player player){
		
		WBData wbData = WBDataHolder.getInstance().get();
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		if (wbData != null && wbUserData != null && wbData.getVersion() != wbUserData.getBossVersion()) {
			wbUserData = wbUserData.nextInstance(wbData.getVersion());
			WBUserDataHolder.getInstance().update(player, wbUserData);
		}
		
	}
	public void cleanBuff(String userID) {
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(userID);
		wbUserData.cleanBuff();
		WBUserDataDao.getInstance().update(wbUserData);
	}

}