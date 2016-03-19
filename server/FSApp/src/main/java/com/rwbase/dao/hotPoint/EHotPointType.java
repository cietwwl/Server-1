package com.rwbase.dao.hotPoint;


public enum EHotPointType {
	Friend_Give(1),//好友
	Email(2),//邮箱
	Gamble(3),//祭坛
	Arena(4),//竞技场
	Role(5),//角色
	Hero(6),//佣兵
	Daily(7),//日常
	Sign(8),//签到
	Friend_Request(9),//好友请求
	Task(11);//任务
	
	private int _value;
	EHotPointType(int value){
		_value = value;
	}
	
	public int getValue(){
		return _value;
	}
	
	public static EHotPointType valueOf(int value){
		EHotPointType result = null;
		for(int i = 0; i < values().length; i++){
			if(values()[i].getValue() == value){
				result = values()[i];
				break;
			}
		}
		return result;
	}
}
