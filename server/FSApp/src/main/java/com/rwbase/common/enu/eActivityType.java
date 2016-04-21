package com.rwbase.common.enu;

/**
 * 开服活动类型
 * @author lida
 *
 */
public enum eActivityType {
	A_PlayerLv,   			//玩家等级 0
	A_NormalCopyLv,			//普通副本等级 1
	A_EliteCopyLv,			//精英副本等级 2
	A_HeroGrade,			//英雄阶级 3
	A_HeroStar,				//英雄星级 4
	A_HeroNum,				//英雄个数 5
	A_ArenaRank,			//竞技场排名 6
	A_MagicLv,          	//法宝等级 7
	A_CollectionLevel,  	//宝石收集等级 8
	A_CollectionType,   	//宝石手机类型 9
	A_Tower,            	//封神台闯关层数 10
	A_Final,            	//最终奖励 11
	A_ArenaChallengeTime,	//竞技场挑战次数
	A_CollectionMagic,      //收集法宝
	A_OpenBox;              //开启宝箱
	
	public static eActivityType getTypeByOrder(int index){
		eActivityType[] values = eActivityType.values();
		for (eActivityType type : values) {
			if(type.ordinal() == index){
				return type;
			}
		}
		return A_PlayerLv;
	}
}
