package com.playerdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.fightinggrowth.fightingfunc.FSGetBasicCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFashionCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFetterCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFixEquipCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGemCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGroupSkillFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetMagicFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetNormEquipCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetSkillCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetSpriteAttachCurrentFightingOfSingleFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetTaoistCurrentFightingOfSingleFunc;
import com.rwbase.common.IFunction;

public enum FightingCalculateComponentType {

	BASIC("基础属性", FSGetBasicCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	FETTERS("羁绊系统", FSGetFetterCurrentFightingOfSingleFunc.getInstnce(), EmptyPlayerComponent.singleton),
	FIX_EQUIP("神器系统", FSGetFixEquipCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	GEM("宝石系统", FSGetGemCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	GROUP_SKILL("帮派技能", FSGetGroupSkillFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	MAGIC("法宝系统", FSGetMagicFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	NORM_EQUIP("装备系统", FSGetNormEquipCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	SKILL("技能战力", FSGetSkillCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	TAOIST("道术系统", FSGetTaoistCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	FASHION("时装系统", EmptyHeroComponent.singleton, FSGetFashionCurrentFightingOfSingleFunc.getInstance()),
	SPRITE_ATTACH("附灵系统", FSGetSpriteAttachCurrentFightingOfSingleFunc.getInstance(), EmptyPlayerComponent.singleton),
	;
	private final IFunction<Hero, Integer> _componentFunc;
	private final IFunction<Player, Integer> _playerOnlyComponentFunc; // 只针对主角的
	private final String _chineseName;
	
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
	
	private FightingCalculateComponentType(String pName, IFunction<Hero, Integer> pComponentFunc, IFunction<Player, Integer> playerOnlyComponentFunc) {
		_componentFunc = pComponentFunc;
		_playerOnlyComponentFunc = playerOnlyComponentFunc;
		_chineseName = pName;
	}
	
	public String getChineseName() {
		return _chineseName;
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
