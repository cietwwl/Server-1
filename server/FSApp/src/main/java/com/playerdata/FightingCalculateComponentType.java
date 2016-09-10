package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.fightinggrowth.fightingfunc.FSGetBasicCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFashionCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFetterCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFixEquipCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGemCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGroupSkillFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetMagicCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetNormEquipCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetSkillCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetTaoistCurrentFightingOfSingleFunc;
import com.rwbase.common.IFunction;

public enum FightingCalculateComponentType {

	BASIC(FSGetBasicCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	FETTERS(FSGetFetterCurrentFightingOfSingleFunc.getInstnce(), EmptyPlayerComponent.singleton),
	FIX_EQUIP(FSGetFixEquipCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	GEM(FSGetGemCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	GROUP_SKILL(FSGetGroupSkillFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	MAGIC(EmptyHeroComponent.singleton, FSGetMagicCurrentFightingFunc.getInstance()),
	NORM_EQUIP(FSGetNormEquipCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	SKILL(FSGetSkillCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	TAOIST(FSGetTaoistCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	FASHION(EmptyHeroComponent.singleton, FSGetFashionCurrentFightingFunc.getInstance()),
	;
	private final IFunction<Hero, Integer> _componentFunc;
	private final IFunction<Player, Integer> _playerOnlyComponentFunc; // 只针对主角的
	
	private static final List<IFunction<Hero, Integer>> _allHeroComponents;
	private static final List<IFunction<Player, Integer>> _allPlayerComponents;
	
	static {
		List<IFunction<Hero, Integer>> allHeroComponents = new ArrayList<IFunction<Hero, Integer>>();
		List<IFunction<Player, Integer>> allPlayerComponents = new ArrayList<IFunction<Player, Integer>>();
		for(FightingCalculateComponentType type : values()) {
			if(!type.getComponentFunc().getClass().equals(EmptyHeroComponent.class)) {
				allHeroComponents.add(type.getComponentFunc());
			}
			if(!type.getPlayerOnlyComponentFunc().getClass().equals(EmptyPlayerComponent.class)) {
				allPlayerComponents.add(type.getPlayerOnlyComponentFunc());
			}
		}
		_allHeroComponents = Collections.unmodifiableList(allHeroComponents);
		_allPlayerComponents = Collections.unmodifiableList(allPlayerComponents);
	}
	
	private FightingCalculateComponentType(IFunction<Hero, Integer> pComponentFunc, IFunction<Player, Integer> playerOnlyComponentFunc) {
		_componentFunc = pComponentFunc;
		_playerOnlyComponentFunc = playerOnlyComponentFunc;
	}
	
	public IFunction<Hero, Integer> getComponentFunc() {
		return _componentFunc;
	}
	
	public IFunction<Player, Integer> getPlayerOnlyComponentFunc() {
		return _playerOnlyComponentFunc;
	}
	
	public static List<IFunction<Hero, Integer>> getAllHeroComponents() {
		return _allHeroComponents;
	}
	
	public static List<IFunction<Player, Integer>> getAllPlayerComponents() {
		return _allPlayerComponents;
	}
	
	private static enum EmptyHeroComponent implements IFunction<Hero, Integer> {

		singleton;
		
		@Override
		public Integer apply(Hero hero) {
			return 0;
		} 
		
	}
	
	private static enum EmptyPlayerComponent implements IFunction<Player, Integer> {
		
		singleton;
		
		@Override
		public Integer apply(Player player) {
			return 0;
		}
		
	}
}
