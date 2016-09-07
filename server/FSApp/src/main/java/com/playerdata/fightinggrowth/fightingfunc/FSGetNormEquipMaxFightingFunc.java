package com.playerdata.fightinggrowth.fightingfunc;

import java.util.HashMap;
import java.util.Map;

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

	@Override
	public Integer apply(Player player) {
		// 以主角为基准，满装备的战斗力
		ExpectedHeroStatusCfg expectedHeroSatausCfg = expectedHeroStatusCfgDAO.getCfgById(String.valueOf(player.getLevel()));
		RoleQualityCfg qualtiyCfg = roleQualityCfgDAO.getConfig(Utils.computeQualityId(player.getModelId(), expectedHeroSatausCfg.getExpectedQuality()));
		Map<Integer, Integer> attrMap = new HashMap<Integer, Integer>();
		if (qualtiyCfg != null){
			int[] tmp = { qualtiyCfg.getEquip1(), qualtiyCfg.getEquip2(), qualtiyCfg.getEquip3(), qualtiyCfg.getEquip4(), qualtiyCfg.getEquip5(), qualtiyCfg.getEquip6() };
			for (int i = 0; i < tmp.length; i++) {
				HeroEquipCfg cfg = heroEquipCfgDAO.getCfgById(String.valueOf(tmp[i]));
				if (cfg == null) {
					continue;
				}
				Utils.combineAttrMap(cfg.getAttrDataMap(), attrMap);
			}
		}
		String templateId = player.getTemplateId();
		return FightingCalculator.calculateFighting(templateId, attrMap) * expectedHeroSatausCfg.getExpectedHeroCount();
	}

}
