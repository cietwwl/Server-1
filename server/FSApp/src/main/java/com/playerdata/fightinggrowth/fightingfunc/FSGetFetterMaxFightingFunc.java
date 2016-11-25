package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
import com.rwbase.dao.fighting.FixEquipFetterFightingCfgDAO;
import com.rwbase.dao.fighting.HeroFetterFightingCfgDAO;
import com.rwbase.dao.fighting.MagicFetterFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.FixEquipFetterFightingCfg;
import com.rwbase.dao.fighting.pojo.HeroFetterFightingCfg;
import com.rwbase.dao.fighting.pojo.MagicFetterFightingCfg;

public class FSGetFetterMaxFightingFunc implements IFunction<Player, Integer> {

	private static FSGetFetterMaxFightingFunc _instance = new FSGetFetterMaxFightingFunc();
	
	private ExpectedHeroStatusCfgDAO _expectedHeroStatusCfgDAO;
	private HeroFetterFightingCfgDAO _heroFetterFightingCfgDAO;
	private MagicFetterFightingCfgDAO _magicFetterFightingCfgDAO;
	private FixEquipFetterFightingCfgDAO _fixEquipFetterFightingCfgDAO;
	
	protected FSGetFetterMaxFightingFunc() {
		_expectedHeroStatusCfgDAO = ExpectedHeroStatusCfgDAO.getInstance();
		_heroFetterFightingCfgDAO = HeroFetterFightingCfgDAO.getInstance();
		_magicFetterFightingCfgDAO = MagicFetterFightingCfgDAO.getInstance();
		_fixEquipFetterFightingCfgDAO = FixEquipFetterFightingCfgDAO.getInstnce();
	}
	
	public static FSGetFetterMaxFightingFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Player player) {
		// (期望英雄数-1) * (英雄羁绊期望战力 + 神器羁绊期望战力) + (法宝羁绊期望战力 + 神器羁绊期望战力)
		int expectedHeroCount = _expectedHeroStatusCfgDAO.getExpectedHeroCount(player.getLevel());
		MagicFetterFightingCfg magicFetterFightingCfg = _magicFetterFightingCfgDAO.getByLevel(player.getLevel());
		HeroFetterFightingCfg heroFetterFightingCfg = _heroFetterFightingCfgDAO.getByLevel(player.getLevel());
		FixEquipFetterFightingCfg fixEquipFetterFightingCfg = _fixEquipFetterFightingCfgDAO.getByLevel(player.getLevel());
		int fighting = (heroFetterFightingCfg.getAllFighting() + fixEquipFetterFightingCfg.getAllFighting()) * (expectedHeroCount - 1);
		fighting += (fixEquipFetterFightingCfg.getAllFighting() + magicFetterFightingCfg.getAllFighting());
		return fighting;
	}

}
