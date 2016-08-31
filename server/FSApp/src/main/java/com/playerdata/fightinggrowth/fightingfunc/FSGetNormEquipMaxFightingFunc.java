package com.playerdata.fightinggrowth.fightingfunc;

import com.common.Utils;
import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroQualityCfgDAO;
import com.rwbase.dao.fighting.pojo.ExpectedHeroQualityCfg;
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
	
	private int getEquipFighting(int equipId) {
		HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getCfgById(String.valueOf(equipId));
		return 0;
	}

	@Override
	public Integer apply(Player player) {
		// 以主角为基准，满装备的战斗力
		ExpectedHeroQualityCfg cfg = ExpectedHeroQualityCfgDAO.getInstance().getCfgById(String.valueOf(player.getLevel()));
		RoleQualityCfg qualtiyCfg = RoleQualityCfgDAO.getInstance().getConfig(Utils.computeQualityId(player.getModelId(), cfg.getExpectedQuality()));
		return 0;
	}

}
