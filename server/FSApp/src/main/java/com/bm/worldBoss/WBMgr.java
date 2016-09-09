package com.bm.worldBoss;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBCfgDAO;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBUserDataHolder;
import com.playerdata.Player;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;


public class WBMgr {
	
	private static WBMgr wbMgr = new WBMgr();
	
//	private ReadLock readLock;
	private WriteLock writeLock;
	
	public WBMgr(){
		ReentrantReadWriteLock treeRwLock = new ReentrantReadWriteLock();
//		this.readLock = treeRwLock.readLock();
		this.writeLock = treeRwLock.writeLock();
		
	}
	
	public static WBMgr getInstance(){
		return wbMgr;
	} 

	public void synWBData(Player player, int wbDataVersion){
		
		WBDataHolder.getInstance().syn(player, wbDataVersion);
		
	}
	
	public void synWBUserData(Player player, int wbUserDataVersion){		
		WBUserDataHolder.getInstance().syn(player, wbUserDataVersion);
		
	}
	
	public ArmyInfo getBossArmy(){
		
		WBData wbData = WBDataHolder.getInstance().get();
		String wbcfgId = wbData.getWbcfgId();
		WBCfg wbCfg = WBCfgDAO.getInstance().getCfgById(wbcfgId);
		
		ArmyInfo armyInfo = ArmyInfoHelper.buildMonsterArmy(wbCfg.getBossId());
		ArmyHero armyHero = armyInfo.getHeroList().get(0);
		CurAttrData curAttrData = new CurAttrData();
		curAttrData.setCurLife(wbData.getCurLife());
		curAttrData.setMaxLife(wbData.getMaxLife());
		armyHero.setCurAttrData(curAttrData);
	
		return armyInfo;
		
	}
	
	public long decrHp(long hurt){		
		writeLock.lock();
		long curLife = 0;
		try {
			
			curLife = WBDataHolder.getInstance().decrHp(hurt);
	
		} finally {
			writeLock.unlock();			
		}		
		
		return curLife;
		
	}

	
}