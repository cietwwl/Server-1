package com.rwbase.common.enu;

public enum eHeroStar {
	STAR_ONE(1),
	STAR_TWO(2),
	STAR_THREE(3),
	STAR_FOUR(4),
	STAR_FIVE(5);
	
	private int star;
	
	private eHeroStar(int star){
		this.star = star;
	}

	public int getStar() {
		return star;
	}
	
	private static eHeroStar[] allValue;
	
	public static eHeroStar[] getAllValue(){
		if(allValue == null){
			allValue = eHeroStar.values();
		}
		return allValue;
	}
}
