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

	@Override
	public Integer apply(Player player) {
		int expectedHeroCount = ExpectedHeroStatusCfgDAO.getInstance().getExpectedHeroCount(player.getLevel()); // 系统期望的最大英雄数量
		FixEquipLevelFightingCfg lvFightingCfg = FixEquipLevelFightingCfgDAO.getInstance().getByLevel(player.getLevel()); // 神器等级的战斗力配置
		FixEquipQualityFightingCfg qualityFightingCfg = FixEquipQualityFightingCfgDAO.getInstance().getByLevel(player.getLevel()); // 神器进阶的战斗力配置
		FixEquipStarFightingCfg starFightingCfg = FixEquipStarFightingCfgDAO.getInstance().getByLevel(player.getLevel()); // 神器觉醒的战斗力配置
		return expectedHeroCount * (lvFightingCfg.getAllFighting() + qualityFightingCfg.getAllFighting() + starFightingCfg.getAllFighting());
	}

}
