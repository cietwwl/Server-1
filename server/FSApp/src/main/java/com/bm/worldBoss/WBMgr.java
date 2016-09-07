package com.bm.worldBoss;

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
	
	public static WBMgr wbMgr = new WBMgr();
	
	public static WBMgr getInstance(){
		return wbMgr;
	} 

	public void synWBData(Player player, int wbDataVersion){
		
		WBDataHolder.getInstance().syn(player, wbDataVersion);
		
	}
	
	public void synWBUserData(Player player, int wbUserDataVersion){		
		WBUserDataHolder.getInstance().syn(player, wbUserDataVersion);
		
	}
	
	public ArmyInfo GetBossArmy(){
		
		WBData wbData = WBDataHolder.getInstance().get();
		String wbcfgId = wbData.getWbcfgId();
		WBCfg wbCfg = WBCfgDAO.getInstance().getCfgById(wbcfgId);
		
		ArmyInfo armyInfo = ArmyInfoHelper.buildMonsterArmy(wbCfg.getBossId());
		ArmyHero armyHero = armyInfo.getHeroList().get(0);
		CurAttrData curAttrData = new CurAttrData();
//		curAttrData.setCurLife(wbData.getCurLife());
//		curAttrData.setMaxLife(wbData.getMaxLife());
//		
		return armyInfo;
		
	}

	
}