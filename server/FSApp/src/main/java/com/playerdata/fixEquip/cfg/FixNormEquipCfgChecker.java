package com.playerdata.fixEquip.cfg;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCostCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCostCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfgDAO;

public class FixNormEquipCfgChecker {

	private static Integer level= null;
	private static Integer quality= null;
	private static Integer star= null;
	
	public static void check(FixEquipCfg equipCfg){
		String levelCostPlanId = equipCfg.getLevelCostPlanId();	
		checkLevelCost(levelCostPlanId);
		
		String levelPlanId = equipCfg.getLevelPlanId();
		checkLevel(levelPlanId);
		
		String qualityPlanId = equipCfg.getQualityPlanId();
		checkQuality(qualityPlanId);
		
		String starPlanId = equipCfg.getStarPlanId();
		checkStar(starPlanId);
		
	}
	
	private static void checkLevelCost(String planId){
		if(level == null){
			level = 1;
			FixNormEquipLevelCostCfg  costcfg= FixNormEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(planId, level);
			while(costcfg!=null){
				level++;
				costcfg= FixNormEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(planId, level);
			}
		}
		for (int i = 1; i < level; i++) {
			FixNormEquipLevelCostCfg  costcfg= FixNormEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(planId, i);
			if(costcfg == null){
				GameLog.cfgError(LogModule.FixEquip, "FixNormEquipLevelCostCfg", "配置不存在 planId:"+planId+" level:"+i);
			}
			
		}	
		
		
	}
	
	private static void checkLevel(String planId){
		
		for (int i = 1; i < level; i++) {
			FixNormEquipLevelCfg  costcfg= FixNormEquipLevelCfgDAO.getInstance().getByPlanIdAndLevel(planId, i);
			if(costcfg == null){
				GameLog.cfgError(LogModule.FixEquip, "FixNormEquipLevelCfg", "配置不存在 planId:"+planId+" level:"+i);
			}
			
		}	
		
		
	}
	private static void checkQuality(String planId){
		if(quality == null){
			quality = 1;
			FixNormEquipQualityCfg  costcfg= FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(planId, quality);
			while(costcfg!=null){
				quality++;
				costcfg= FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(planId, quality);
			}
		}
		for (int i = 1; i < quality; i++) {
			FixNormEquipQualityCfg  costcfg= FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(planId, i);
			if(costcfg == null){
				GameLog.cfgError(LogModule.FixEquip, "FixNormEquipQualityCostCfg", "配置不存在 planId:"+planId+" level:"+i);
			}
			
		}	
		
	}
	private static void checkStar(String planId){
		if(star == null){
			star = 0;
			FixNormEquipStarCfg  costcfg= FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(planId, star);
			while(costcfg!=null){
				star++;
				costcfg= FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(planId, star);
			}
		}
		for (int i = 0; i < star; i++) {
			FixNormEquipStarCfg  costcfg= FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(planId, i);
			if(costcfg == null){
				GameLog.cfgError(LogModule.FixEquip, "FixNormEquipStarCostCfg", "配置不存在 planId:"+planId+" level:"+i);
			}
			
		}	
		
	}
}
