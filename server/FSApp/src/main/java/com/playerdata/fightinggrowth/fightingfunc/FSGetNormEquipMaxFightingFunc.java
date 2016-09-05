package com.playerdata.fightinggrowth.fightingfunc;

import com.common.Utils;
import com.playerdata.FightingCalculator;
import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
import com.rwbase.dao.fighting.pojo.ExpectedHeroStatusCfg;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;

/**
 * 
 * 获取当前等级下，玩家装备所能达到的最大战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetNormEquipMaxFightingFunc implements IFunction<Player, Integer> {
	
	private HeroEquipCfgDAO heroEquipCfgDAO;
	private ExpectedHeroStatusCfgDAO expectedHeroStatusCfgDAO;
	private RoleQualityCfgDAO roleQualityCfgDAO;
	
	public FSGetNormEquipMaxFightingFunc() {
		heroEquipCfgDAO = HeroEquipCfgDAO.getInstance();
		expectedHeroStatusCfgDAO = ExpectedHeroStatusCfgDAO.getInstance();
		roleQualityCfgDAO = RoleQualityCfgDAO.getInstance();
	}
	
	private int getEquipFighting(int equipId, String heroTemplateId) {
		HeroEquipCfg cfg = heroEquipCfgDAO.getCfgById(String.valueOf(equipId));
		return FightingCalculator.calculateFighting(cfg.getAttrDataMap(), heroTemplateId);
	}

	@Override
	public Integer apply(Player player) {
		// 以主角为基准，满装备的战斗力
		ExpectedHeroStatusCfg expectedHeroSatausCfg = expectedHeroStatusCfgDAO.getCfgById(String.valueOf(player.getLevel()));
		RoleQualityCfg qualtiyCfg = roleQualityCfgDAO.getConfig(Utils.computeQualityId(player.getModelId(), expectedHeroSatausCfg.getExpectedQuality()));
		String templateId = player.getTemplateId();
		int fighting1 = this.getEquipFighting(qualtiyCfg.getEquip1(), templateId);
		int fighting2 = this.getEquipFighting(qualtiyCfg.getEquip2(), templateId);
		int fighting3 = this.getEquipFighting(qualtiyCfg.getEquip3(), templateId);
		int fighting4 = this.getEquipFighting(qualtiyCfg.getEquip4(), templateId);
		int fighting5 = this.getEquipFighting(qualtiyCfg.getEquip5(), templateId);
		int fighting6 = this.getEquipFighting(qualtiyCfg.getEquip6(), templateId);
		return (fighting1 + fighting2 + fighting3 + fighting4 + fighting5 + fighting6) * expectedHeroSatausCfg.getExpectedHeroCount();
	}

}
