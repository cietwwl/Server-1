package com.rw.handler.teamBattle.enums;

public enum TBMemberState {
	/**
	 * 离队
	 */
	Leave(0),
	/**
	 * 准备中，还未开打，但也没离开队伍
	 */
	Ready(1),
	/**
	 * 战斗中
	 */
	Fight(2),
	/**
	 * 至少完成了一个循环
	 */
	HalfFinish(3),
	/**
	 * 打完
	 */
	Finish(4);
	
	private int value;
	TBMemberState(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public boolean equals(int value){
		return this.value == value;
	}
	
	public boolean equals(TBMemberState state){
		return this.value == state.value;
	}
	
	public static TBMemberState getState(int value){
		switch (value) {
		case 0:
			return Leave;
		case 1:
			return Ready;
		case 2:
			return Fight;
		case 3:
			return Finish;
		default:
			return Leave;
		}
	}
}
