package com.bm.worldBoss;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBSettingCfg;
import com.bm.worldBoss.cfg.WBSettingCfgDAO;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.state.WBStateFSM;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;


public class WBMgr {

	
	private static WBMgr wbMgr = new WBMgr();
	
	private ReadLock readLock;
	private WriteLock writeLock;
	
	public WBMgr(){
		ReentrantReadWriteLock treeRwLock = new ReentrantReadWriteLock();
		this.readLock = treeRwLock.readLock();
		this.writeLock = treeRwLock.writeLock();		
	}
	
	public static WBMgr getInstance(){
		return wbMgr;
	} 

	public void synWBData(Player player, int wbDataVersion){
		
		WBDataHolder.getInstance().syn(player, wbDataVersion);
		
	}
	
	public boolean isBossDie(){
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getCurLife()<=0;
	}
	
	public boolean isAfterBossStartTime(){
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getStartTime() > System.currentTimeMillis();
	}
	public boolean isBeginBossEndTime(){
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getEndTime() > System.currentTimeMillis();
	}
	
	
	public void runOn10Second(){
		try {
			
			WBStateFSM.getInstance().tranfer();
			
		} catch (Throwable e) {
			GameLog.error(LogModule.WorldBoss, "WBMgr[runOn10Second]", "", e);
		}
	}
	

	
	public boolean initNewBoss(WBCfg nextCfg ){
		boolean success = false;
		writeLock.lock();
		try {			
			success = WBDataHolder.getInstance().newBoss(nextCfg);
		} finally {
			writeLock.unlock();			
		}		
		return success;
	}

	
	public ArmyInfo getBossArmy(){
		
		WBData wbData = WBDataHolder.getInstance().get();
		ArmyInfo armyInfo = null;
		
		armyInfo = ArmyInfoHelper.buildMonsterArmy(wbData.getMonsterCfgId());
		ArmyHero armyHero = armyInfo.getHeroList().get(0);
		CurAttrData curAttrData = new CurAttrData();
		
		readLock.lock();
		try {			
			curAttrData.setCurLife(wbData.getCurLife());
			curAttrData.setMaxLife(wbData.getMaxLife());
		} finally {
			readLock.unlock();
		}
		
		armyHero.setCurAttrData(curAttrData);
	
		return armyInfo;		
	}
	
	public boolean decrHp(Player player, long hurt){	
		boolean success = false;
		writeLock.lock();		
		try {			
			WBData wbData = WBDataHolder.getInstance().get();
			if(wbData.getCurLife()>0){				
				WBDataHolder.getInstance().decrHp(player, hurt);
				success = true;			
			}
			
		} finally {
			writeLock.unlock();			
		}		
		synWBData(player, -1);	
		return success;		
	}
	
	public boolean adjustBossLevel(){	
		boolean success = false;
		writeLock.lock();		
		try {			
			WBData wbData = WBDataHolder.getInstance().get();
			if(wbData.isKilled()){
				wbData.setSurvivalCount(0);
				if(isQuickKilled(wbData)){
					wbData.addQuickKillCount();
				}else{
					wbData.setQuickKillCount(0);
				}
			}else{
				wbData.addSurvivalCount();
			}
			
			WBSettingCfg settingCfg = WBSettingCfgDAO.getInstance().getCfg();
			
			int quickKillMax = settingCfg.getQuickKillMax();
			int survialMax = settingCfg.getSurvialMax();
			if(wbData.getQuickKillCount() > quickKillMax){
				wbData.inrcBossLevel();
			}else if(wbData.getSurvivalCount() > survialMax){
				wbData.dercBossLevel();
			}
			
			WBDataHolder.getInstance().update();
			
		} finally {
			writeLock.unlock();			
		}		
		
		return success;		
	}
	
	private boolean isQuickKilled(WBData wbData){
		WBSettingCfg settingCfg = WBSettingCfgDAO.getInstance().getCfg();
		
		long killTimeCost = wbData.getKilledTimeCost();
		
		if(killTimeCost < settingCfg.getQuickKillTimeInMilli()){
			return true;
		}
		return false;
	}
	

	public boolean isSameBoss(int bossVersion) {
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getVersion() == bossVersion;
	}
	
	
	
	
}