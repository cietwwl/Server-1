package com.playerdata.fightinggrowth.fightingfunc;

import java.util.ArrayList;
import java.util.List;

import com.bm.arena.ArenaRobotDataMgr;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.fightinggrowth.calc.param.FixEquipFightingParam.Builder;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.common.IFunction;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FSGetFixEquipCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static FSGetFixEquipCurrentFightingOfSingleFunc _instance = new FSGetFixEquipCurrentFightingOfSingleFunc();

	// private FixEquipLevelFightingCfgDAO fixEquipLevelFightingCfgDAO;
	// private FixEquipQualityFightingCfgDAO fixEquipQualityFightingCfgDAO;
	// private FixEquipStarFightingCfgDAO fixEquipStarFightingCfgDAO;
	private CfgOpenLevelLimitDAO openLevelLimitDAO;

	protected FSGetFixEquipCurrentFightingOfSingleFunc() {
		// fixEquipLevelFightingCfgDAO = FixEquipLevelFightingCfgDAO.getInstance();
		// fixEquipQualityFightingCfgDAO = FixEquipQualityFightingCfgDAO.getInstance();
		// fixEquipStarFightingCfgDAO = FixEquipStarFightingCfgDAO.getInstance();
		openLevelLimitDAO = CfgOpenLevelLimitDAO.getInstance();
	}

	public static final FSGetFixEquipCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Hero hero) {
		Player player = FSHeroMgr.getInstance().getOwnerOfHero(hero);
		boolean robot = player.isRobot();
		boolean open = robot ? true : openLevelLimitDAO.isOpen(eOpenLevelType.FIX_EQUIP, player);
		if (!open) {
			return 0;
		}

		List<HeroFixEquipInfo> fixEquipInfos = new ArrayList<HeroFixEquipInfo>();
		if (!robot) {
			fixEquipInfos.addAll(hero.getFixExpEquipMgr().getHeroFixSimpleInfo(hero.getId())); // 特殊神器
			fixEquipInfos.addAll(hero.getFixNormEquipMgr().getHeroFixSimpleInfo(hero.getId())); // 普通神器
		} else {
			String userId = hero.getOwnerUserId();
			int heroModelId = hero.getModeId();
			ArenaRobotDataMgr mgr = ArenaRobotDataMgr.getMgr();
			List<HeroFixEquipInfo> fixExpList = FixEquipHelper.parseFixExpEquip2SimpleList(mgr.getFixExpEquipList(userId, heroModelId));
			if (!fixExpList.isEmpty()) {
				fixEquipInfos.addAll(fixExpList);
			}

			List<HeroFixEquipInfo> fixNormList = FixEquipHelper.parseFixNormEquip2SimpleList(mgr.getFixNormEquipList(userId, heroModelId));
			if (!fixNormList.isEmpty()) {
				fixEquipInfos.addAll(fixNormList);
			}
		}

		Builder b = new Builder();
		b.setFixEquips(fixEquipInfos);
		return FightingCalcComponentType.FIX_EQUIP.calc.calc(b.build());
	}
}
