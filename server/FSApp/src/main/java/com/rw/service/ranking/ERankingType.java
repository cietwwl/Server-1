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
	
	/**力士排行(实时)*/
	WARRIOR_CURRENT(105),
	
	/**剑士排行(实时)*/
	SWORDMAN_CURRENT(106),
	
	/**术士排行(实时)*/
	MAGICAN_CURRENT(107),
	
	/**祭祀排行(实时)*/
	PRIEST_CURRENT(108),
	
	/**巅峰竞技排行(每日)*/
	ATHLETICS_DAY(109),
	
	/**巅峰竞技排行(实时)*/
	ATHLETICS_CURRENT(110),
	
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
	
	/**获取本职业   每日   排行类型
	 * 	None(0),		//新手
		Warrior(1), 	//力士...
		SwordsMan(2),	//行者...
		Magican(3),     //术士...
		Priest(4);		//祭祀...
	*/
	public static ERankingType getJobDay(int job){
		switch(job){
			case 1:
				return WARRIOR_DAY;
			case 2:
				return SWORDMAN_DAY;
			case 3:
				return MAGICAN_DAY;
			case 4:
				return PRIEST_DAY;
		}
		return ERankingType.WARRIOR_DAY;
	}
	
	/**获取本职业  即时   排行类型
	 * 	0.力士
		1.行者
		2.术士
		3.祭司
		-1.无职业
	*/
	public static ERankingType getJobCurrent(int job){
		switch(job){
			case 0:
				return WARRIOR_CURRENT;
			case 1:
				return SWORDMAN_CURRENT;
			case 2:
				return MAGICAN_CURRENT;
			case 3:
				return PRIEST_CURRENT;
		}
		return ERankingType.WARRIOR_CURRENT;
	}
}
