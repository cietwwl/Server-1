package com.rwbase.common.enu;

public enum EHeroQuality {
	white,
	Green,
	Green_One,
	Blue,
	Blue_One,
	Blue_Two,
	Purple ,
	Purple_One,
	Purple_Two,
	Purple_Three,
	Purple_Four,
	Gold,
	Gold_One,
	Gold_Tow;
	
	
	private static EHeroQuality[] allValue;
	
	public static EHeroQuality[] getAllValue(){
		if(allValue == null){
			allValue = EHeroQuality.values();
		}
		return allValue;
	}
}
