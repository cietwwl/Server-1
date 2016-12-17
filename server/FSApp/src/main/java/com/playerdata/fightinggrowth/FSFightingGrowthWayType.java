package com.playerdata.fightinggrowth;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fightinggrowth.fightingfunc.FSGetBasicCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetBasicMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFashionCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFashionMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFetterCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFetterMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFixEquipCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetFixEquipMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGemCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGemMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGroupSkillCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetGroupSkillMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetMagicCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetMagicMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetNormEquipCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetNormEquipMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetSkillCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetSkillMaxFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetSpriteAttachCurrentFighting;
import com.playerdata.fightinggrowth.fightingfunc.FSGetTaoistCurrentFightingFunc;
import com.playerdata.fightinggrowth.fightingfunc.FSGetTaoistMaxFightingFunc;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.IBIFunction;
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
	HERO_SKILL(2, FSGetSkillCurrentFightingFunc.getInstance(), FSGetSkillMaxFightingFunc.getInstance()), // 装备属性的战斗力获取
	FETTERS(3, FSGetFetterCurrentFightingFunc.getInstance(), FSGetFetterMaxFightingFunc.getInstance()), // 仙缘战斗力获取
	MAGIC(4, FSGetMagicCurrentFightingFunc.getInstance(), FSGetMagicMaxFightingFunc.getInstance()), // 法宝属性的战斗力获取
	FIX_EQUIP(5, FSGetFixEquipCurrentFightingFunc.getInstance(), FSGetFixEquipMaxFightingFunc.getInstance()), // 神器属性的战斗力获取
	GROUP_SKILL(6, FSGetGroupSkillCurrentFightingFunc.getInstance(), FSGetGroupSkillMaxFightingFunc.getInstance()) {
		@Override
		public boolean isPlayerCanUse(Player player) {
			return GroupHelper.getInstance().hasGroup(player.getUserId());
		}
	}, // 帮派技能
	TAOIST(7, FSGetTaoistCurrentFightingFunc.getInstance(), FSGetTaoistMaxFightingFunc.getInstance()), // 道术战斗力获取
	GEM(8, FSGetGemCurrentFightingFunc.getInstance(), FSGetGemMaxFightingFunc.getInstance()), // 宝石属性的战斗力获取
	FASHION(9, FSGetFashionCurrentFightingFunc.getInstance(), FSGetFashionMaxFightingFunc.getInstance()), // 时装战斗力获取
	SPRITE_ATTACH(10, FSGetSpriteAttachCurrentFighting.getInstance(), null), // 附灵战力获取
	NORMAL_EQUIPMENT(11, FSGetNormEquipCurrentFightingFunc.getInstance(), FSGetNormEquipMaxFightingFunc.getInstance()), // 普通装备战力获取
	;
	private final int _sign;
	private final IBIFunction<Player, List<Hero>, Integer> _getCurrentFightingFunc;
	private final IFunction<Player, Integer> _getMaxFightingFunc;

	private static final Map<Integer, FSFightingGrowthWayType> _all;

	static {
		Map<Integer, FSFightingGrowthWayType> map = new HashMap<Integer, FSFightingGrowthWayType>();
		for (FSFightingGrowthWayType type : values()) {
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
	private FSFightingGrowthWayType(int pSign, IBIFunction<Player, List<Hero>, Integer> getCurrentFightingFunc, IFunction<Player, Integer> getMaxFightingFunc) {
		this._sign = pSign;
		this._getCurrentFightingFunc = getCurrentFightingFunc;
		this._getMaxFightingFunc = getMaxFightingFunc;
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
	public IBIFunction<Player, List<Hero>, Integer> getGetCurrentFightingFunc() {
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

	/**
	 * 
	 * 判断player是否能够使用此项
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayerCanUse(Player player) {
		return true;
	}

	public static FSFightingGrowthWayType getBySign(int pSign) {
		return _all.get(pSign);
	}
}
