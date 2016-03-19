package com.rwbase.dao.guildSecretArea.projo;

import com.rwbase.common.enu.ECareer;

public enum ESecretType {
	GOLD_TYPE_ONE(0),//金币副本1
	GOLD_TYPE_THREE(1),//金币副本3
	GOLD_TYPE_TEN(2),//金币副本10
	EXP_TYPE_ONE(3),//经验副本1
	EXP_TYPE_THREE(4),//经验副本3
	EXP_TYPE_TEN(5),//经验副本10
	STONG_TYPE_ONE(6),//强化时1
	STONG_TYPE_THREE(7),//强化时3
	STONG_TYPE_TEN(8);//强化时10
	private int type;
	ESecretType(int type){
		this.type = type;
	}
	public int getNumber(){
		return this.type;
	}
	public static ESecretType valueOf(int value){
		ESecretType result = null;
		for (int i = 0; i < ESecretType.values().length; i++) {
			result = ESecretType.values()[i];
			if(result.getNumber() == value){
				break;
			}
		}
		return result;
	}
}
