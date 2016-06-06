package com.playerdata.fixEquip.cfg;

import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

public class FixEquipCfgChecker {

	public static void checkAll(){		
		
		checkRoleFixEquipCfg();
		
		
		List<RoleFixEquipCfg> allCfg = RoleFixEquipCfgDAO.getInstance().getAllCfg();
		for (RoleFixEquipCfg roleFixEquipCfg : allCfg) {
			List<String> normCfgIdList = roleFixEquipCfg.getNormCfgIdList();
			for (String normCfgId : normCfgIdList) {
				FixEquipCfg equipCfg = FixEquipCfgDAO.getInstance().getCfgById(normCfgId);
				if(equipCfg==null){
					GameLog.cfgError(LogModule.FixEquip, "FixEquipCfg", "配置不存在id:"+normCfgId);
				}else{
					FixNormEquipCfgChecker.check(equipCfg);					
				}
				
			}
			
			
			List<String> expCfgIdList = roleFixEquipCfg.getExpCfgIdList();
			for (String expCfgId : expCfgIdList) {
				FixEquipCfg equipCfg = FixEquipCfgDAO.getInstance().getCfgById(expCfgId);
				if(equipCfg==null){
					GameLog.cfgError(LogModule.FixEquip, "FixEquipCfg", "配置不存在id:"+expCfgId);
				}else{
					FixExpEquipCfgChecker.check(equipCfg);					
				}
			}
			
			
		}
		
	}

	private static void checkRoleFixEquipCfg() {
		List<RoleCfg> allRoleCfg = RoleCfgDAO.getInstance().getAllCfg();
		for (RoleCfg roleCfg : allRoleCfg) {
			String modelId = String.valueOf(roleCfg.getModelId());
			RoleFixEquipCfg roleFixCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(modelId);
			if(roleFixCfg == null){
				GameLog.cfgError(LogModule.FixEquip, "RoleFixEquipCfg", "配置不存在id:"+modelId);
			}
		}
	}

	
	
}
