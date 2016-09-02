package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.fightinggrowth.fightingfunc.FSGetBasicCurrentFightingOfSingleFunc;
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

	BASIC(FSGetBasicCurrentFightingOfSingleFunc.class, EmptyPlayerComponent.class),
	FETTERS(FSGetFetterCurrentFightingOfSingleFunc.class, EmptyPlayerComponent.class),
	FIX_EQUIP(FSGetFixEquipCurrentFightingOfSingleFunc.class, EmptyPlayerComponent.class),
	GEM(FSGetGemCurrentFightingOfSingleFunc.class, EmptyPlayerComponent.class),
	GROUP_SKILL(FSGetGroupSkillFightingOfSingleFunc.class, EmptyPlayerComponent.class),
	MAGIC(EmptyHeroComponent.class, FSGetMagicCurrentFightingFunc.class),
	NORM_EQUIP(FSGetNormEquipCurrentFightingOfSingleFunc.class, EmptyPlayerComponent.class),
	SKILL(FSGetSkillCurrentFightingOfSingleFunc.class, EmptyPlayerComponent.class),
	TAOIST(FSGetTaoistCurrentFightingOfSingleFunc.class, EmptyPlayerComponent.class),
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
	
	private FightingCalculateComponentType(Class<? extends IFunction<Hero, Integer>> componentClazz, Class<? extends IFunction<Player, Integer>> playerOnlyComponentClazz) {
		try {
			_componentFunc = componentClazz.newInstance();
			_playerOnlyComponentFunc = playerOnlyComponentClazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	
	private static class EmptyHeroComponent implements IFunction<Hero, Integer>{
		
		public EmptyHeroComponent() {
		}

		@Override
		public Integer apply(Hero hero) {
			return 0;
		} 
		
	}
	
	private static class EmptyPlayerComponent implements IFunction<Player, Integer> {
		
		public EmptyPlayerComponent() {
		}

		@Override
		public Integer apply(Player player) {
			return 0;
		}
		
	}
}
