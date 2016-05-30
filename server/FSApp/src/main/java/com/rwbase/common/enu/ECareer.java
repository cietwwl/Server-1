package com.rwbase.common.enu;


public enum ECareer {
	None(0, "新手"),		//新手
	Warrior(1, "力士"), 	//力士...
	SwordsMan(2, "行者"),	//行者...
	Magican(3, "术士"),     //术士...
	Priest(4, "祭祀");		//祭祀...
	
	
	private int type;
	private String carrer;
	ECareer(int type, String carrer){
		this.type = type;
		this.carrer = carrer;
	}
	public int getValue(){
		return this.type;
	}
	
	private static ECareer[] allValues;
	
	public static ECareer valueOf(int value,ECareer defaultCareer){
		ECareer result = valueOf(value);
		if (result == null) result = defaultCareer;
		return result;
	}
	
	public static ECareer valueOf(int value){
		if(allValues == null){
			allValues = ECareer.values();
		}
		ECareer result = null;
		for (int i = 0; i < allValues.length; i++) {
			result = allValues[i];
			if(result.getValue() == value){
				break;
			}
		}
		return result;
	}
	
	public static String getCarrer(int value){
		if(allValues == null){
			allValues = ECareer.values();
		}
		for (ECareer eCareer : allValues) {
			if(eCareer.type == value){
				return eCareer.carrer;
			}
		}
		return None.carrer;
	}
	
	public static ECareer valueOf(String careerType, ECareer value) {
		ECareer result = value;
		try {
			result = ECareer.valueOf(careerType);
		} catch (Exception e) {
		}
		return result;
	}
}
