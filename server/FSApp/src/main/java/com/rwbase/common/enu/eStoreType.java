package com.rwbase.common.enu;

import com.rwbase.dao.openLevelLimit.eOpenLevelType;

/**商店类型*/
public enum eStoreType{
	General(0,eOpenLevelType.SHOP),//普通商店
	Blackmark(1, eOpenLevelType.SECRET_SHOP),//黑市商店
	Secret(2, eOpenLevelType.Blackmark_SHOP),//神秘商店
	Sport(3, eOpenLevelType.Sport_Store),//战火商店
	ArenaStore(4, eOpenLevelType.Arena_Store),//竞技场商店
	PeakStore(5, eOpenLevelType.Peak_Store),//巅峰竞技商店
	Union(6, eOpenLevelType.Union_Store),//帮派商店
	Tower(7, eOpenLevelType.Tower_Store),//仙阵商店
	Waken(8, eOpenLevelType.Waken_Store);//觉醒商店
	
	private int order;
	private eOpenLevelType type;
	eStoreType(int order, eOpenLevelType type){
		this.order = order;
		this.type = type;
	}
	public int getOrder() {
		return order;
	}
	
	public eOpenLevelType getType() {
		return type;
	}
	public static eStoreType getDef(int value){
		eStoreType eAttr = null;
		for (int i = 0; i < eStoreType.values().length; i++) {
			eAttr = eStoreType.values()[i];
			if(eAttr.getOrder() == value){
				break;
			}
		}
		return eAttr;
	}
}