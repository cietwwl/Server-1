package com.rwbase.common.enu;

import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
import com.common.HPCUtil;

/**
 * 开服活动类型
 * 
 * @author lida
 *
 */
public enum eActivityType {

	A_PlayerLv(0), // 玩家等级 0
	A_NormalCopyLv(1), // 普通副本等级 1
	A_EliteCopyLv(2), // 精英副本等级 2
	A_HeroGrade(3), // 英雄阶级 3
	A_HeroStar(4), // 英雄星级 4
	A_HeroNum(5), // 英雄个数 5
	A_ArenaRank(6), // 竞技场排名 6
	A_MagicLv(7), // 法宝等级 7
	A_CollectionLevel(8), // 宝石收集等级 8
	A_CollectionType(9), // 宝石手机类型 9
	A_Tower(10), // 封神台闯关层数 10
	A_Final(11), // 最终奖励 11
	A_ArenaChallengeTime(12), // 竞技场挑战次数
	A_CollectionMagic(13), // 收集法宝
	A_OpenBox(14); // 开启宝箱

	;

	private int type;
	private String name;

	eActivityType(int type) {
		this.type = type;
		this.name = String.valueOf(type);
	}

	private static HashMap<String, eActivityType> nameMapping;
	private static eActivityType[] array;

	static {
		eActivityType[] temp = values();
		nameMapping = new HashMap<String, eActivityType>();
		for (eActivityType type : temp) {
			nameMapping.put(type.name(), type);
			nameMapping.put(type.getOutputName(), type);
		}
		Object[] copy = HPCUtil.toMappedArray(temp, "type");
		array = new eActivityType[copy.length];
		HPCUtil.copy(copy, array);
	}

	@JsonValue
	public String getOutputName() {
		return name;
	}

	public int getType() {
		return type;
	}

	@JsonCreator
	public static eActivityType forValue(String value) {
		eActivityType result = nameMapping.get(value);
		if (result == null) {
			throw new ExceptionInInitializerError("can not find enum eTaskFinishDef:" + value);
		}
		return result;
	}

	public static eActivityType getTypeByOrder(int index) {
		return array[index];
	}

}
