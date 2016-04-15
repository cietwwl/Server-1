package com.rw.service.ranking;


public enum ERankingType {
	
	/**力士排行(每日)*/
	WARRIOR_DAY(101),
	
	/**剑士排行(每日)*/
	SWORDMAN_DAY(102),
	
	/**术士排行(每日)*/
	MAGICAN_DAY(103),
	
	/**祭祀排行(每日)*/
	PRIEST_DAY(104),
	
	/**巅峰竞技排行(每日)*/
	ATHLETICS_DAY(109),
	
	/**全员战斗力排行榜*/
	FIGHTING_ALL(201),
	
	/**巅峰竞技战斗力排行*/
	ATHLETICS_FIGHTING(202),
	
	/**五人小队战斗力排行*/
	TEAM_FIGHTING_ALL(203),
	
	/**全员等级榜*/
	LEVEL_ALL(301),
	
	/**帮派排行*/
	GANGS(401),
	
	/**帮派人员排行*/
	GANGS_NUMBER(402),
	
	/**帮派建筑排行*/
	GANGS_BUILDING(403),
	
	/**帮派矿藏排行*/
	GANGS_RESOURCES(404),
	
	/**无尽战火排行*/
	ENDLESS(501),
	
	/**荣耀山谷排行*/
	GLORY(502);
	
	private int _value;
	ERankingType(int value){
		_value = value;
	}
	
	public int getValue(){
		return _value;
	}
	
	public static ERankingType valueOf(int value){
		ERankingType result = null;
		for (int i = 0; i < ERankingType.values().length; i++) {
			result = ERankingType.values()[i];
			if(result.getValue() == value){
				break;
			}
		}
		return result;
	}
}
