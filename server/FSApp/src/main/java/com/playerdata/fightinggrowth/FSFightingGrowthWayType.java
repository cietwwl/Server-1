package com.playerdata.fightinggrowth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.fightinggrowth.fightingfunc.FSGetBasicCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetBasicMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFetterCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFixEquipCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFixEquipMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGemCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGemMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGroupSkillCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetMagicCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetMagicMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetNormEquipCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetNormEquipMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetTaoistCurrentFightingFunc;
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

	BASIC(1, FSGetBasicCurrentFightingFunc.getInstance(), FSGetBasicMaxFightingFunc.getInstance()), // 英雄自身属性的战斗力获取
	NORM_EQUIP(2, FSGetNormEquipCurrentFightingFunc.getInstance(), FSGetNormEquipMaxFightingFunc.getInstance()), // 装备属性的战斗力获取
	FETTERS(3, FSGetFetterCurrentFightingFunc.getInstance(), FSGetFetterCurrentFightingFunc.getInstance()), // 仙缘战斗力获取
	MAGIC(4, FSGetMagicCurrentFightingFunc.getInstance(), FSGetMagicMaxFightingFunc.getInstance()), // 法宝属性的战斗力获取
	FIX_EQUIP(5, FSGetFixEquipCurrentFightingFunc.getInstance(), FSGetFixEquipMaxFightingFunc.getInstance()), // 神器属性的战斗力获取
	GROUP_SKILL(6, FSGetGroupSkillCurrentFightingFunc.getInstance(), FSGetGroupSkillCurrentFightingFunc.getInstance()), // 帮派技能
	TAOIST(7, FSGetTaoistCurrentFightingFunc.getInstance(), FSGetTaoistCurrentFightingFunc.getInstance()), // 道术战斗力获取
	GEM(8, FSGetGemCurrentFightingFunc.getInstance(), FSGetGemMaxFightingFunc.getInstance()), // 宝石属性的战斗力获取
	FASHION(9, FSGetGemCurrentFightingFunc.getInstance(), FSGetGemMaxFightingFunc.getInstance()), // 时装战斗力获取
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
	private FSFightingGrowthWayType(int pSign, IFunction<Player, Integer> getCurrentFightingFunc, IFunction<Player, Integer> getMaxFightingFunc) {
		this._sign = pSign;
		try {
			this._getCurrentFightingFunc = getCurrentFightingFunc;
			this._getMaxFightingFunc = getMaxFightingFunc;
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
