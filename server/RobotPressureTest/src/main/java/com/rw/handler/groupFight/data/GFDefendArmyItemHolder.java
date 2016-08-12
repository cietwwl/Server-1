package com.rw.handler.groupFight.data;


public class GFDefendArmyItemHolder{
	
	private static GFDefendArmyItemHolder instance = new GFDefendArmyItemHolder();
	
	public static GFDefendArmyItemHolder getInstance(){
		return instance;
	}

	private GFDefendArmyItem enimy = null;
	
	public void updateSelectedEnimy(GFDefendArmyItem item){
		enimy = item;
	}
	
	public GFDefendArmyItem getSelfEnimy(){
		return enimy;
	}
}
