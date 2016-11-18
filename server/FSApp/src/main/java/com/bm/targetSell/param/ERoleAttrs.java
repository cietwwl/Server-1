package com.bm.targetSell.param;

import com.common.HPCUtil;
import com.rwbase.dao.targetSell.BenefitAttrCfg;

/**
 * <pre>
 * 角色通用属性
 * 注意，枚举的常量id要跟{@link BenefitAttrCfg}里的id对应
 * </pre>
 * @author Alex
 *
 * 2016年11月17日 下午8:17:51
 */
public enum ERoleAttrs {
	r_Level("1"),                      	//等级
	r_VipLevel("2"),						//vip等级
	r_Charge("3"),						//充值
	r_TeamPower("4"),				 	//五人战力
	r_AllPower("5"),						//全员战力
	r_CreateTime("6"),					//创建时间
	r_LastLoginTime("7"),				//登陆时间
	r_Coin("8"),							//玩家拥有的金币数量
	r_Power("9"), 						//玩家当前体力值
	r_EmbattleQuality("10"),              //上阵英雄的品阶
	r_EmbattleCarrer("11"), 				//上阵英雄的品阶
	
	
	

	;
	private String id;
	private ERoleAttrs(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}
	


	private static ERoleAttrs[] array;

	static {
		ERoleAttrs[] temp = ERoleAttrs.values();
		Object[] copy = HPCUtil.toMappedArray(temp, "id");
		array = new ERoleAttrs[copy.length];
		HPCUtil.copy(copy, array);
	}

	public static ERoleAttrs getRoleAttrs(int type) {
		return array[type];
	}
	
	public static ERoleAttrs[] getAll(){
		return array;
	}
}
