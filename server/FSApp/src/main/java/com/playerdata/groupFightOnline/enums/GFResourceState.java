package com.playerdata.groupFightOnline.enums;

public enum GFResourceState {
	/**
	 * 初始化阶段（该阶段只能跳去休战和竞标阶段）
	 */
	INIT(0),
	/**
	 * 休战期间（只能跳去竞标阶段）（这个阶段用来结算，很短暂）
	 */
	REST(1),
	/**
	 * 竞标阶段（只能跳去备战阶段）
	 */
	BIDDING(2),
	/**
	 * 备战阶段（只能跳去开战阶段）
	 */
	PREPARE(3),
	/**
	 * 开战阶段（只能跳去休战阶段）
	 */
	FIGHT(4);
	
	private int value;
	GFResourceState(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public boolean equals(int value){
		return this.value == value;
	}
	
	public boolean equals(GFResourceState state){
		return this.value == state.value;
	}
	
	public GFResourceState getState(int value){
		switch (value) {
		case 1:
			return REST;
		case 2:
			return BIDDING;
		case 3:
			return PREPARE;
		case 4:
			return FIGHT;
		default:
			return REST;
		}
	}
}
