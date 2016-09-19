package com.rw.service.skill;

public interface SkillConstant {

	// 技能监听的操作类型
	public static enum SkillOptType {
		ADD_LISTEN_ID(1), // 增加监听Id
		REPLACE_LISTEN_ID(2), // 替换监听Id
		DELETE_LISTENT_ID(3);// 删除监听Id

		public final int value;

		private SkillOptType(int value) {
			this.value = value;
		}
	}

	String HERO_NOT_EXIST = "你还没拥有这个佣兵";

	public static final int NORMAL_SKILL_ORDER = 6;// 普攻的技能Id
}
