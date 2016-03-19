package com.rwbase.dao.gamble.pojo;

public enum EGambleWeight {
	ITEM_WRITE(1),
	ITEM_GREEN(2),//绿色
	ITEM_BLUE(3),//蓝色
	ITEM_PURPLE(4),//紫色
	ITEM_GOLD(5),//橙色
	ITEM_HEROSTONE(6),//佣兵神魂石
	ITEM_HERO(7),//佣兵整卡
	FIRST_TEN_GUARANTEE(8),//首次十连保底
	TEN_GUARANTEE(9),//非首次十连抽保底
	MAIN_HOT(10),//主要热点
	MINOR_HOT(11),//次要热点
	FREE_FIRST(12),//免费首抽单次必掉
	FIRST_COUNT(13);//非免费首抽必掉次数
	
	private int _value;
	EGambleWeight(int value){
		_value = value;
	}
	public int getValue(){
		return _value;
	}
	
	public static EGambleWeight valueOf(int value){
		EGambleWeight result = null;
		for (int i = 0; i < EGambleWeight.values().length; i++) {
			result = EGambleWeight.values()[i];
			if(result.getValue() == value){
				break;
			}
		}
		return result;
	}
}
