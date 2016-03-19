package com.rwbase.common.enu;

/**商店类型*/
public enum eStoreType{
	General(0),//普通商店
	Blackmark(1),//黑市商店
	Secret(2),//神秘商店
	Sport(3),//战火商店
	ArenaStore(4),//竞技场商店
	PeakStore(5),//巅峰竞技商店
	Union(6),//帮派商店
	Tower(7);//仙阵商店
	
	private int order;
	eStoreType(int order){
		this.order = order;
	}
	public int getOrder() {
		return order;
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