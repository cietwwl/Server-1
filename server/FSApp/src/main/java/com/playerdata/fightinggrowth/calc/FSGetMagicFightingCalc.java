package com.playerdata.fightinggrowth.calc;

import com.log.GameLog;
import com.rwbase.common.attribute.param.MagicParam;
import com.rwbase.dao.fighting.MagicLevelFightingCfgDAO;
import com.rwbase.dao.fighting.MagicQualityFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.MagicSkillFightingCfg;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.magicweapon.MagicAptitudeCoefficientCfgDAO;
import com.rwbase.dao.magicweapon.pojo.MagicAptitudeCoefficientCfg;

/**
 * @Author HC
 * @date 2016年10月25日 上午11:44:45
 * @desc
 **/

public class FSGetMagicFightingCalc implements IFightingCalc {
	private MagicCfgDAO magicCfgDAO;
	private MagicLevelFightingCfgDAO magicLevelFightingCfgDAO;
	private MagicQualityFightingCfgDAO magicQualityFightingCfgDAO;
	private MagicAptitudeCoefficientCfgDAO magicAptitudeCoefficientCfgDAO;

	protected FSGetMagicFightingCalc() {
		magicCfgDAO = MagicCfgDAO.getInstance();
		magicLevelFightingCfgDAO = MagicLevelFightingCfgDAO.getInstance();
		magicQualityFightingCfgDAO = MagicQualityFightingCfgDAO.getInstance();
		magicAptitudeCoefficientCfgDAO = MagicAptitudeCoefficientCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		MagicParam magicParam = (MagicParam) param;

		String magicId = magicParam.getMagicId();
		MagicCfg cfg = magicCfgDAO.getCfgById(magicId);
		if (cfg == null) {
			GameLog.error("FSMagicFightingCalc", "计算法宝战力", "找不到对应的法宝配置，modelId：" + magicId);
			return 0;
		}
		
		MagicSkillFightingCfg levelSkillFightingCfg = magicLevelFightingCfgDAO.getCfgById(String.valueOf(magicParam.getMagicLevel()));
		MagicSkillFightingCfg qualitySkillFightingCfg = magicQualityFightingCfgDAO.getCfgById(String.valueOf(cfg.getQuality()));
		MagicAptitudeCoefficientCfg aptitudeCoefficientCfg = magicAptitudeCoefficientCfgDAO.getCfgById(String.valueOf(magicParam.getMagicAptitude()));
		float aptitudeCoefficient = 1.0f;
		if(aptitudeCoefficientCfg != null) {
			aptitudeCoefficient = aptitudeCoefficientCfg.getCoefficient();
		}
		if (magicParam.isMainRole()) {
			// （强化主技能战力+强化被动技能战力）*法宝成长系数+（品阶主技能战力+品阶被动技能战力）
//			System.out.println(String.format("强化主动技能战力：%s，强化被动技能战力：%s，品阶主动技能战力：%s，品阶被动技能战力：%s，成长系数：%s", levelSkillFightingCfg.getActiveSkillFighting(), levelSkillFightingCfg.getPassiveSkillFighting(),
//					qualitySkillFightingCfg.getActiveSkillFighting(), qualitySkillFightingCfg.getPassiveSkillFighting(), levelSkillFightingCfg.getPassiveSkillFighting()));
			return Math.round((levelSkillFightingCfg.getActiveSkillFighting() + levelSkillFightingCfg.getPassiveSkillFighting()) * aptitudeCoefficient)
					+ (qualitySkillFightingCfg.getActiveSkillFighting() + qualitySkillFightingCfg.getPassiveSkillFighting());
		} else {
			// 强化被动技能战力*法宝成长系数+品阶被动技能战力
//			System.out.println(String.format("强化被动技能战力：%s，品阶被动技能战力：%s，成长系数：%s",levelSkillFightingCfg.getPassiveSkillFighting(), qualitySkillFightingCfg.getPassiveSkillFighting(), levelSkillFightingCfg.getPassiveSkillFighting()));
			return Math.round(levelSkillFightingCfg.getPassiveSkillFighting() * aptitudeCoefficient) + qualitySkillFightingCfg.getPassiveSkillFighting();
		}
	}
}