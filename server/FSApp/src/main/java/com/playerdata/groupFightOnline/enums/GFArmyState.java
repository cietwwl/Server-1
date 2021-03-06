package com.playerdata.groupFightOnline.enums;

public enum GFArmyState {
	/**
	 * 未上阵或撤阵
	 */
	EMPTY(-1),
	/**
	 * 正常状态
	 */
	NORMAL(1),
	/**
	 * 被选中锁定
	 */
	SELECTED(2),
	/**
	 * 正在被攻击
	 */
	FIGHTING(3),
	/**
	 * 战败
	 */
	DEFEATED(4),
	/**
	 * 新添加（添加完毕，状态立刻变NORMAL）
	 */
	NEWADD(5);
	
	private int value;
	GFArmyState(int value){
		this.value = value;
	}
	
	public int getValue(){
		if(value == 5) return 1;
		return value;
	}
	
	public boolean equals(int value){
		return this.value == value;
	}
	
	public boolean equals(GFArmyState state){
		return this.value == state.value;
	}
	
	public static GFArmyState getState(int value){
		switch (value) {
		case -1:
			return EMPTY;
		case 1:
			return NORMAL;
		case 2:
			return SELECTED;
		case 3:
			return FIGHTING;
		case 4:
			return DEFEATED;
		default:
			return EMPTY;
		}
	}
}
