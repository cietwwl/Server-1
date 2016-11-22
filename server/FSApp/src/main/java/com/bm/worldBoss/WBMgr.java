package com.bm.worldBoss;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBCfgDAO;
import com.bm.worldBoss.cfg.WBHPCfg;
import com.bm.worldBoss.cfg.WBHPCfgDAO;
import com.bm.worldBoss.cfg.WBSettingCfg;
import com.bm.worldBoss.cfg.WBSettingCfgDAO;
import com.bm.worldBoss.data.WBBroatCastData;
import com.bm.worldBoss.data.WBBroatCastDataHolder;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.rank.WBHurtRankMgr;
import com.bm.worldBoss.state.WBStateFSM;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
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
		WBHurtRankMgr.syn(player);
	}
	
	public boolean isBossDie(){
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getCurLife()<=0;
	}
	
	public boolean isAfterBossStartTime(){
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getStartTime() <= System.currentTimeMillis();
	}
	public boolean isBeforeBossEndTime(){
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getEndTime() > System.currentTimeMillis();
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
		WBCfg cfg = WBCfgDAO.getInstance().getCfgById(wbData.getWbcfgId());
		ArmyInfo armyInfo = null;
		
		armyInfo = ArmyInfoHelper.buildMonsterArmy(wbData.getMonsterCfgId(), cfg.getCopyId());
		ArmyHero armyHero = armyInfo.getPlayer();
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
				long curLife = WBDataHolder.getInstance().decrHp(player, hurt);
				System.out.println("curlift :" + curLife);
				if(curLife <= 0) WBMgr.getInstance().broatCastBossLeave();
				if(isBossDie()){
					System.err.println("--------------world boss was killed!!");
					broatCastBossDie();
				}
				success = true;			
			}
			
		} finally {
			writeLock.unlock();			
		}		
		synWBData(player, -1);	
		return success;		
	}
	
	private void broatCastBossDie() {
		
		WBBroatCastData broatCastData = new WBBroatCastData();
		broatCastData.setBossDie(true);
		
		broatCast(broatCastData);
		
	}
	public void broatCastBossLeave() {
		
		WBBroatCastData broatCastData = new WBBroatCastData();
		broatCastData.setBossLeave(true);
		
		broatCast(broatCastData);
		
	}

	private void broatCast(WBBroatCastData broatCastData) {
		List<String> allOnFightUserIds = WBOnFightMgr.getInstance().getAllOnFightUserIds();
		for (String userIdTmp: allOnFightUserIds) {
			Player online = PlayerMgr.getInstance().findPlayerFromMemory(userIdTmp);
			if(online!=null){				
				System.out.println("broatcast world boss !, rec role:" + online.getUserName() + ", death:" + broatCastData.isBossDie()
						+", leave :" + broatCastData.isBossLeave());
				WBBroatCastDataHolder.getInstance().syn(online, broatCastData);
			}
		}
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
				
				WBHPCfg nextLevel = WBHPCfgDAO.getInstance().getCfgById(String.valueOf(wbData.getBossLevel()+1));
				if(nextLevel!=null){					
					wbData.inrcBossLevel();
					GameLog.info(LogModule.WorldBoss.getName(), "WBMgr[adjustBossLevel]", "boss 升级:"+wbData.getBossLevel());
				}
				
			}else if(wbData.getSurvivalCount() > survialMax){
				WBHPCfg nextLevel = WBHPCfgDAO.getInstance().getCfgById(String.valueOf(wbData.getBossLevel()-1));
				if(nextLevel!=null){						
					wbData.dercBossLevel();
					GameLog.info(LogModule.WorldBoss.getName(), "WBMgr[adjustBossLevel]", "boss 降级:"+wbData.getBossLevel());
				}
			}
			
			WBDataHolder.getInstance().update();
			
		} finally {
			writeLock.unlock();			
		}		
		
		return success;		
	}
	
	private boolean isQuickKilled(WBData wbData){
		WBSettingCfg settingCfg = WBSettingCfgDAO.getInstance().getCfg();
		
		long killTimeCost = wbData.killedTimeCost();
		
		if(killTimeCost < settingCfg.getQuickKillTimeInMilli()){
			return true;
		}
		return false;
	}
	

	public boolean isSameBoss(int bossVersion) {
		WBData wbData = WBDataHolder.getInstance().get();
		if(wbData == null){
			return false;
		}
		return wbData.getVersion() == bossVersion;
	}

	public void onPlayerLogin(Player player) {
		synWBData(player, -10);//登录通知的版本不作维护
	}
	
	
	
	
}