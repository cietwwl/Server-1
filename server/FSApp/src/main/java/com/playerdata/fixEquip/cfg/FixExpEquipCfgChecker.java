package com.playerdata.fixEquip.cfg;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCostCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCostCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfgDAO;

public class FixExpEquipCfgChecker {

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
			FixExpEquipLevelCostCfg  costcfg= FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(planId, level);
			while(costcfg!=null){
				level++;
				costcfg= FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(planId, level);
			}
		}
		for (int i = 1; i < level; i++) {
			FixExpEquipLevelCostCfg  costcfg= FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(planId, i);
			if(costcfg == null){
				GameLog.error(LogModule.FixEquip, "FixExpEquipLevelCostCfg", "配置不存在 planId:"+planId+" level:"+i, null);
			}
			
		}	
		
		
	}
	
	private static void checkLevel(String planId){
		
		for (int i = 1; i < level; i++) {
			FixExpEquipLevelCfg  costcfg= FixExpEquipLevelCfgDAO.getInstance().getByPlanIdAndLevel(planId, i);
			if(costcfg == null){
				GameLog.error(LogModule.FixEquip, "FixExpEquipLevelCfg", "配置不存在 planId:"+planId+" level:"+i, null);
			}
			
		}	
		
		
	}
	private static void checkQuality(String planId){
		if(quality == null){
			quality = 1;
			FixExpEquipQualityCfg  costcfg= FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(planId, quality);
			while(costcfg!=null){
				quality++;
				costcfg= FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(planId, quality);
			}
		}
		for (int i = 1; i < quality; i++) {
			FixExpEquipQualityCfg  costcfg= FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(planId, i);
			if(costcfg == null){
				GameLog.error(LogModule.FixEquip, "FixExpEquipQualityCostCfg", "配置不存在 planId:"+planId+" level:"+i, null);
			}
			
		}	
		
	}
	private static void checkStar(String planId){
		if(star == null){
			star = 0;
			FixExpEquipStarCfg  costcfg= FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(planId, star);
			while(costcfg!=null){
				star++;
				costcfg= FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(planId, star);
			}
		}
		for (int i = 0; i < star; i++) {
			FixExpEquipStarCfg  costcfg= FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(planId, i);
			if(costcfg == null){
				GameLog.error(LogModule.FixEquip, "FixExpEquipStarCostCfg", "配置不存在 planId:"+planId+" level:"+i, null);
			}
			
		}	
		
	}
}
