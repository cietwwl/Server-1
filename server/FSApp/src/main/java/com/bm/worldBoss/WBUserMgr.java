package com.bm.worldBoss;

import com.bm.worldBoss.cfg.WBAwardCfg;
import com.bm.worldBoss.cfg.WBAwardCfgDAO;
import com.bm.worldBoss.cfg.WBSettingCfg;
import com.bm.worldBoss.cfg.WBSettingCfgDAO;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBUserData;
import com.bm.worldBoss.data.WBUserDataHolder;
import com.bm.worldBoss.rank.WBHurtRankMgr;
import com.bm.worldBoss.service.WBHelper;
import com.playerdata.Player;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;


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
	
	public void fightBeginUpdate(Player player){
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		
		WBSettingCfg settingCfg = WBSettingCfgDAO.getInstance().getCfg();
		long curTime = System.currentTimeMillis();
		wbUserData.setLastFightTime(curTime);
		wbUserData.setFightCdTime(curTime + settingCfg.getCDInMilli());
		wbUserData.cleanAccHurt();
		
		WBUserDataHolder.getInstance().update(player);
	}
	
	public void fightUpdate(Player player, long hurt){	
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		
		wbUserData.addAccHurt(hurt);
		
		WBUserDataHolder.getInstance().update(player);
		
		WBHurtRankMgr.addOrUpdate(player);
	}
	
	public void fightEndUpdate(Player player, long hurt){	
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());	
		
		wbUserData.addAccHurt(hurt);
		
		long lastHurt = wbUserData.getAccHurt();
		wbUserData.setLastHurt(lastHurt);
		wbUserData.addTotalHurt(lastHurt);
		int awardCoin = countAwardCoin(player, lastHurt);
		wbUserData.setLastAwardCoin(awardCoin);
		

		
		WBHurtRankMgr.addOrUpdate(player);
	}
	
	private int countAwardCoin(Player player, long totalHurt) {
		int level = player.getLevel();
		WBAwardCfg awardCfg = WBAwardCfgDAO.getInstance().getCfgById(String.valueOf(level));
		int awardCoin = 0;
		if(awardCfg!=null){
			float factor = awardCfg.getFactor();
			awardCoin = (int)(factor*totalHurt);
		}

		boolean success = WBHelper.addCoin(player, awardCoin);
		
		return success?awardCoin:0;
	}
	
	public boolean isInCD(Player player) {
//		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());		
//		long fightCdTime = wbUserData.getFightCdTime();
//		return fightCdTime!=0 && fightCdTime < System.currentTimeMillis();
		return false;
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
	public boolean canBuyCd(Player player) {

		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		int maxCdCanBuy = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.worldBossMaxBuyBuffCount);
		
		return wbUserData.getCdBuyCount() < maxCdCanBuy;
	}
}