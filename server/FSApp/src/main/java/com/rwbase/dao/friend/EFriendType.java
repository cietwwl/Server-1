package com.rwbase.dao.friend;

public enum EFriendType {
	FRIEND(1),
	REQUEST(2),
	BLACK(3);
	
	private int value;
	EFriendType(int value){
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
