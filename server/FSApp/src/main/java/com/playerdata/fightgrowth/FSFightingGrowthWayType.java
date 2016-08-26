package com.playerdata.fightgrowth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.fightgrowth.fightingfunc.FSGetBasicFightingFunc;
import com.playerdata.fightgrowth.fightingfunc.FSGetExpEquipFightingFunc;
import com.playerdata.fightgrowth.fightingfunc.FSGetGemFightingFunc;
import com.playerdata.fightgrowth.fightingfunc.FSGetMagicFightingFunc;
import com.playerdata.fightgrowth.fightingfunc.FSGetNormEquipFightingFunc;
import com.rwbase.common.IFunction;

public enum FSFightingGrowthWayType {

	BASIC(1, FSGetBasicFightingFunc.class), // 英雄自身属性的战斗力获取
	NORM_EQUIP(2, FSGetNormEquipFightingFunc.class), // 装备属性的战斗力获取
	GEM(3, FSGetGemFightingFunc.class), // 宝石属性的战斗力获取
	MAGIC(4, FSGetMagicFightingFunc.class), // 法宝属性的战斗力获取
	EXP_EQUIP(5, FSGetExpEquipFightingFunc.class), // 神器属性的战斗力获取
	;
	private final int _sign;
	private final IFunction<Player, Integer> _getFightingFunc;
	
	private static final Map<Integer, FSFightingGrowthWayType> _all;
	
	static {
		Map<Integer, FSFightingGrowthWayType> map = new HashMap<Integer, FSFightingGrowthWayType>();
		for(FSFightingGrowthWayType type : values()) {
			map.put(type._sign, type);
		}
		_all = Collections.unmodifiableMap(map);
	}
	
	private FSFightingGrowthWayType(int pSign, Class<? extends IFunction<Player, Integer>> implClazz) {
		this._sign = pSign;
		try {
			this._getFightingFunc = implClazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getSign() {
		return _sign;
	}
	
	public IFunction<Player, Integer> getGetFightingFunc() {
		return _getFightingFunc;
	}
	
	public static FSFightingGrowthWayType getBySign(int pSign) {
		return _all.get(pSign);
	}
}
