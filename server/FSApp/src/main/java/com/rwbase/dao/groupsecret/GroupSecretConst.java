package com.rwbase.dao.groupsecret;

/*
 * @author HC
 * @date 2016年5月30日 下午12:01:18
 * @Description 
 */
public class GroupSecretConst {
	// 秘境的矿点Index
	public static enum GroupSecretIndex {
		MAIN, // 主矿点
		LEFT, // 第二个矿点
		RIGHT;// 第三个矿点
	}

	// 匹配的阵容信息
	public static enum MatchSecretState {
		NON_BATTLE, // 未开战
		IN_BATTLE, // 战斗中
		IN_ROB_PROTECT;// 掠夺保护中
	}
}