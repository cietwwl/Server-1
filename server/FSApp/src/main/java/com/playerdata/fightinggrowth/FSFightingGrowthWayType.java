package com.playerdata.fightinggrowth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.fightinggrowth.fightingfunc.FSGetBasicCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetBasicMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFixExpEquipCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFixExpEquipMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGemCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGemMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetMagicCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetMagicMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetNormEquipCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetNormEquipMaxFightingFunc;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthWayInfoCfg;

/**
 * 
 * 战力提升途径的枚举配置
 * 
 * @author CHEN.P
 *
 */
public enum FSFightingGrowthWayType {

	BASIC(1, FSGetBasicCurrentFightingFunc.class, FSGetBasicMaxFightingFunc.class), // 英雄自身属性的战斗力获取
	NORM_EQUIP(2, FSGetNormEquipCurrentFightingFunc.class, FSGetNormEquipMaxFightingFunc.class), // 装备属性的战斗力获取
	GEM(3, FSGetGemCurrentFightingFunc.class, FSGetGemMaxFightingFunc.class), // 宝石属性的战斗力获取
	MAGIC(4, FSGetMagicCurrentFightingFunc.class, FSGetMagicMaxFightingFunc.class), // 法宝属性的战斗力获取
	EXP_EQUIP(5, FSGetFixExpEquipCurrentFightingFunc.class, FSGetFixExpEquipMaxFightingFunc.class), // 神器属性的战斗力获取
	;
	private final int _sign;
	private final IFunction<Player, Integer> _getCurrentFightingFunc;
	private final IFunction<Player, Integer> _getMaxFightingFunc;
	
	private static final Map<Integer, FSFightingGrowthWayType> _all;
	
	static {
		Map<Integer, FSFightingGrowthWayType> map = new HashMap<Integer, FSFightingGrowthWayType>();
		for(FSFightingGrowthWayType type : values()) {
			map.put(type._sign, type);
		}
		_all = Collections.unmodifiableMap(map);
	}
	
	/**
	 * 
	 * @param pSign 与{@link FSUserFightingGrowthWayInfoCfg#getTypeForServer()} 一一对应
	 * @param getCurrentFightingClazz 根据角色等级获取当前该模块的战斗力函数
	 * @param getMaxFightingClazz 根据角色等级获取当前该模块所能达到的最大战斗力函数
	 */
	private FSFightingGrowthWayType(int pSign, Class<? extends IFunction<Player, Integer>> getCurrentFightingClazz, Class<? extends IFunction<Player, Integer>> getMaxFightingClazz) {
		this._sign = pSign;
		try {
			this._getCurrentFightingFunc = getCurrentFightingClazz.newInstance();
			this._getMaxFightingFunc = getMaxFightingClazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getSign() {
		return _sign;
	}
	
	/**
	 * 
	 * 获取当前战斗力的函数实现
	 * 
	 * @return
	 */
	public IFunction<Player, Integer> getGetCurrentFightingFunc() {
		return _getCurrentFightingFunc;
	}
	
	/**
	 * 
	 * 获取最大战斗力的函数实现
	 * 
	 * @return
	 */
	public IFunction<Player, Integer> getGetMaxFightingFunc() {
		return _getMaxFightingFunc;
	}
	
	public static FSFightingGrowthWayType getBySign(int pSign) {
		return _all.get(pSign);
	}
}
