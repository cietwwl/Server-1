package com.rw.handler.task;

import java.util.HashMap;
import org.codehaus.jackson.annotate.JsonCreator;

public enum eTaskFinishDef {	
	None(0),
	Player_Level(1),//玩家等级
	Transfer(2),//职业选择
	Player_Quality(3),//主角升阶
	Change_Career(4),//职业更换
	Hero_Count(5),//佣兵数量
	Hero_Star(6),
	Hero_Quality(7),//佣兵装备品阶
	Recharge(8),//充值数
	Finish_Section(9),//完成章节ID
	Finish_Copy_Normal(10),//完成副本普通副本
	Section_Star(11),//副本星数
	Finish_Copy_Elite(12),//完成副本精英副本
	Finish_Copy_Hero(13),//完成副本英雄副本
	Add_Friend(14),
	Challage_BattleTower(15);//挑战封神台到指定层数
	
	private int order;
	private String outputName;
	eTaskFinishDef(int order){
		this.order = order;
		this.outputName = String.valueOf(order);
	}
	public int getOrder() {
		return order;
	}
	
	private static HashMap<String, eTaskFinishDef> nameMapping;

	static {
		nameMapping = new HashMap<String, eTaskFinishDef>();
		eTaskFinishDef[] array = eTaskFinishDef.values();
		for (int i = 0; i < array.length; i++) {
			eTaskFinishDef eTask = array[i];
			nameMapping.put(eTask.name(), eTask);
			nameMapping.put(eTask.outputName, eTask);
		}
		nameMapping.put("Hero_Equip_Quality", Hero_Quality);
	}

	@JsonCreator
	public static eTaskFinishDef forValue(String value) {
		//兼容旧数据"Hero_Equip_Quality"，否则会导致Player中的Task初始化失败.
		eTaskFinishDef result = nameMapping.get(value);
		if(result == null){
			throw new ExceptionInInitializerError("can not find enum eTaskFinishDef:"+value);
		}
		return result;
	}
	
	@JsonCreator
	public static eTaskFinishDef forValue(int value) {
		//兼容旧数据"Hero_Equip_Quality"，否则会导致Player中的Task初始化失败.
		eTaskFinishDef result = nameMapping.get(value);
		if(result == null){
			throw new ExceptionInInitializerError("can not find enum eTaskFinishDef:"+value);
		}
		return result;
	}
}
