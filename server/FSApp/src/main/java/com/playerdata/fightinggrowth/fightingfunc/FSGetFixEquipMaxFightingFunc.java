package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
import com.rwbase.dao.fighting.FixEquipLevelFightingCfgDAO;
import com.rwbase.dao.fighting.FixEquipQualityFightingCfgDAO;
import com.rwbase.dao.fighting.FixEquipStarFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.FixEquipLevelFightingCfg;
import com.rwbase.dao.fighting.pojo.FixEquipQualityFightingCfg;
import com.rwbase.dao.fighting.pojo.FixEquipStarFightingCfg;

/**
 * 
 * 获取玩家当前等级下，神器所能达到的最大战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetFixEquipMaxFightingFunc implements IFunction<Player, Integer> {
	
	private static FSGetFixEquipMaxFightingFunc _instance = new FSGetFixEquipMaxFightingFunc();
	
	private FixEquipLevelFightingCfgDAO fixEquipLevelFightingCfgDAO;
	private FixEquipQualityFightingCfgDAO fixEquipQualityFightingCfgDAO;
	private FixEquipStarFightingCfgDAO fixEquipStarFightingCfgDAO;
	private ExpectedHeroStatusCfgDAO expectedHeroStatusCfgDAO;
	
	protected FSGetFixEquipMaxFightingFunc() {
		fixEquipLevelFightingCfgDAO = FixEquipLevelFightingCfgDAO.getInstance();
		fixEquipQualityFightingCfgDAO = FixEquipQualityFightingCfgDAO.getInstance();
		fixEquipStarFightingCfgDAO = FixEquipStarFightingCfgDAO.getInstance();
		expectedHeroStatusCfgDAO = ExpectedHeroStatusCfgDAO.getInstance();
	}
	
	public static FSGetFixEquipMaxFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		int expectedHeroCount = expectedHeroStatusCfgDAO.getExpectedHeroCount(player.getLevel()); // 系统期望的最大英雄数量
		FixEquipLevelFightingCfg lvFightingCfg = fixEquipLevelFightingCfgDAO.getByLevel(player.getLevel()); // 神器等级的战斗力配置
		FixEquipQualityFightingCfg qualityFightingCfg = fixEquipQualityFightingCfgDAO.getByLevel(player.getLevel()); // 神器进阶的战斗力配置
		FixEquipStarFightingCfg starFightingCfg = fixEquipStarFightingCfgDAO.getByLevel(player.getLevel()); // 神器觉醒的战斗力配置
		return expectedHeroCount * (lvFightingCfg.getAllFighting() + qualityFightingCfg.getAllFighting() + starFightingCfg.getAllFighting());
	}

}
