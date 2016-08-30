package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroCountCfgDAO;
import com.rwbase.dao.fighting.FixExpEquipLevelFightingCfgDAO;
import com.rwbase.dao.fighting.FixExpEquipQualityFightingCfgDAO;
import com.rwbase.dao.fighting.FixExpEquipStarFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.FixExpEquipLevelFightingCfg;
import com.rwbase.dao.fighting.pojo.FixExpEquipQualityFightingCfg;
import com.rwbase.dao.fighting.pojo.FixExpEquipStarFightingCfg;

/**
 * 
 * 获取玩家当前等级下，神器所能达到的最大战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetFixExpEquipMaxFightingFunc implements IFunction<Player, Integer> {

	@Override
	public Integer apply(Player player) {
		int expectedHeroCount = ExpectedHeroCountCfgDAO.getInstance().getExpectedHeroCount(player.getLevel()); // 系统期望的最大英雄数量
		FixExpEquipLevelFightingCfg lvFightingCfg = FixExpEquipLevelFightingCfgDAO.getInstance().getByLevel(player.getLevel()); // 神器等级的战斗力配置
		FixExpEquipQualityFightingCfg qualityFightingCfg = FixExpEquipQualityFightingCfgDAO.getInstance().getByLevel(player.getLevel()); // 神器进阶的战斗力配置
		FixExpEquipStarFightingCfg starFightingCfg = FixExpEquipStarFightingCfgDAO.getInstance().getByLevel(player.getLevel()); // 神器觉醒的战斗力配置
		return expectedHeroCount * (lvFightingCfg.getAllFighting() + qualityFightingCfg.getAllFighting() + starFightingCfg.getAllFighting());
	}

}
