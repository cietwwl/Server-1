package com.bm.worldBoss;

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
		
		
		wbUserData.setLastHurt(hurt);
		wbUserData.addTotalHurt(hurt);
		long curTime = System.currentTimeMillis();
		wbUserData.setLastFightTime(curTime);
		wbUserData.setLastAwardCoin(awardCoin);
		wbUserData.setFightCdTime(curTime + CD );
		
		WBUserDataHolder.getInstance().update(player);
		
		WBHurtRankMgr.addOrUpdate(player);
	}
	
	final private int CD = 30000;  //用户攻击的时间间隔。
	public boolean isInCD(Player player) {
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());		
		return wbUserData.getFightCdTime() < System.currentTimeMillis();
	}
	

	public void cleanCD(Player player){
		
		WBUserData wbUserData = WBUserDataHolder.getInstance().get(player.getUserId());
		wbUserData.setFightCdTime(0);
		wbUserData.addCdBuyCount();		
		WBUserDataHolder.getInstance().update(player);
	}
	
}