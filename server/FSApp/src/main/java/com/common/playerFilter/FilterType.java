package com.common.playerFilter;

public enum FilterType {
	LEVEL_SPAN(1),  //按等级赛选
	CREATE_TIME(2); //按角色创建时间赛选
	
	private int value;
	private FilterType(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	public static FilterType[] allValues;
	
	public static FilterType valueOf(int ordinal){
		if(allValues == null){
			allValues = FilterType.values();
		}
		for (FilterType filterType : allValues) {
			if(filterType.getValue() == ordinal){
				return filterType;
			}
		}
		
		return LEVEL_SPAN;
	}
}
