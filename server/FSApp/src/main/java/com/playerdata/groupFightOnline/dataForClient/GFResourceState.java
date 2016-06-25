package com.playerdata.groupFightOnline.dataForClient;

public enum GFResourceState {
	/**
	 * 初始化阶段（该阶段只能跳去休战和竞标阶段）
	 */
	INIT(0),
	/**
	 * 休战期间（只能跳去竞标阶段）
	 */
	REST(0),
	/**
	 * 竞标阶段（只能跳去备战阶段）
	 */
	BIDDING(1),
	/**
	 * 备战阶段（只能跳去开战阶段）
	 */
	PREPARE(2),
	/**
	 * 开战阶段（只能跳去休战阶段）
	 */
	FIGHT(3);
	
	private int value;
	GFResourceState(int value){
		this.value = value;
	}
	
	public int getValue(){
		if(value == 5) return 1;
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
		case 0:
			return REST;
		case 1:
			return BIDDING;
		case 2:
			return PREPARE;
		case 3:
			return FIGHT;
		default:
			return REST;
		}
	}
}
