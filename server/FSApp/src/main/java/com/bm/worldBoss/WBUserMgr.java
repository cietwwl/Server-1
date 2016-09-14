package com.bm.worldBoss;

import com.bm.worldBoss.cfg.WBSettingCfg;
import com.bm.worldBoss.cfg.WBSettingCfgDAO;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBUserData;
import com.bm.worldBoss.data.WBUserDataHolder;
import com.bm.worldBoss.rank.WBHurtRankMgr;
import com.playerdata.Player;


public class WBUserMgr {
	
	private static WBUserMgr wbMgr = new WBUserMgr();
	
	
	public static WBUserMgr getInstance(){
		return wbMgr;
	} 
	public void synWBUserData(Player player, int wbUserDataVersion){	
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		int bossVersion = wbUserData.getBossVersion();
		if(!WBMgr.getInstance().isSameBoss(bossVersion)){
			WBUserData newData = WBUserData.newInstance(wbUserData.getUserId());
			WBUserDataHolder.getInstance().update(player, newData);
		}else{
			WBUserDataHolder.getInstance().syn(player, wbUserDataVersion);		
		}
	
	}	
	
	public void addBuff(Player player, String buffCfgId){	
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		wbUserData.addBuff(buffCfgId);		
		WBUserDataHolder.getInstance().update(player);
		
	}
	
	public void fightEndUpdate(Player player, long hurt, int awardCoin){	
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		
		WBSettingCfg settingCfg = WBSettingCfgDAO.getInstance().getCfg();
		
		wbUserData.setLastHurt(hurt);
		wbUserData.addTotalHurt(hurt);
		long curTime = System.currentTimeMillis();
		wbUserData.setLastFightTime(curTime);
		wbUserData.setLastAwardCoin(awardCoin);
		wbUserData.setFightCdTime(curTime + settingCfg.getCDInMilli());
		
		WBUserDataHolder.getInstance().update(player);
		
		WBHurtRankMgr.addOrUpdate(player);
	}
	
	public boolean isInCD(Player player) {
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());		
		long fightCdTime = wbUserData.getFightCdTime();
		return fightCdTime!=0 && fightCdTime < System.currentTimeMillis();
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
		if(wbData!=null && wbUserData!=null && wbData.getVersion() != wbUserData.getBossVersion()){
			wbUserData = wbUserData.nextInstance(wbData.getVersion());
			WBUserDataHolder.getInstance().update(player, wbUserData);
		}
		
	}
}