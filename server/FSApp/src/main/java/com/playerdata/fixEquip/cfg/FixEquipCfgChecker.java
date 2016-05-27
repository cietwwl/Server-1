package com.playerdata.fixEquip.cfg;

import java.util.List;

import com.log.GameLog;
import com.log.LogModule;

public class FixEquipCfgChecker {

	public static void checkAll(){
		
		List<RoleFixEquipCfg> allCfg = RoleFixEquipCfgDAO.getInstance().getAllCfg();
		for (RoleFixEquipCfg roleFixEquipCfg : allCfg) {
			List<String> normCfgIdList = roleFixEquipCfg.getNormCfgIdList();
			for (String normCfgId : normCfgIdList) {
				FixEquipCfg equipCfg = FixEquipCfgDAO.getInstance().getCfgById(normCfgId);
				if(equipCfg==null){
					GameLog.error(LogModule.FixEquip, "FixEquipCfg", "配置不存在id:"+normCfgId, null);
				}else{
					FixNormEquipCfgChecker.check(equipCfg);					
				}
				
			}
			
			
			List<String> expCfgIdList = roleFixEquipCfg.getExpCfgIdList();
			for (String expCfgId : expCfgIdList) {
				FixEquipCfg equipCfg = FixEquipCfgDAO.getInstance().getCfgById(expCfgId);
				if(equipCfg==null){
					GameLog.error(LogModule.FixEquip, "FixEquipCfg", "配置不存在id:"+expCfgId, null);
				}else{
					FixExpEquipCfgChecker.check(equipCfg);					
				}
			}
			
			
		}
		
	}

	
	
}
