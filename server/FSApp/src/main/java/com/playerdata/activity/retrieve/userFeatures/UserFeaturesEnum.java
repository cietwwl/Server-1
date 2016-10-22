package com.playerdata.activity.retrieve.userFeatures;

import java.util.HashMap;


public enum UserFeaturesEnum {

	breakfast("1"),
	lunch("2"),
	dinner("3"),
	supper("4"),
	power("5"),//体力，自然增长
	worship("6"),//膜拜
	buyPowerOne("7"),//购买体力1-3
	buyPowerTwo("8"),//购买体力4-6
	buyPowerThree("9"),//购买体力7-9
	buyPowerFour("10"),//购买体力10-12
	buyPowerFive("11"),//购买体力13-15
	jbzd("12"),//财神
	lxsg("13"),//炼息
	celestial_PengLaiIsland("14"),//幻境_蓬莱
	celestial_KunLunWonderLand("15"),//幻境_昆仑
	teamBattle("15"),//心魔
	tower("16"),//万仙
	battleTower("17"),//封神
	magicSecert("18");//法宝秘境
	
	
	
	private String id;
	
	private UserFeaturesEnum(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static HashMap<String, UserFeaturesEnum> getMap() {
		return map;
	}
	
	public static void setMap(HashMap<String, UserFeaturesEnum> map) {
		UserFeaturesEnum.map = map;
	}

	private static HashMap<String, UserFeaturesEnum> map;
	
	static {
		UserFeaturesEnum[] array = values();
		map = new HashMap<String, UserFeaturesEnum>();
		for(int i = 0;i < array.length; i++){
			UserFeaturesEnum featuresEnum = array[i];
			map.put(featuresEnum.getId(), featuresEnum);
		}		
	}
	
	public static UserFeaturesEnum getById(String cfgId) {
	return map.get(cfgId);
}
	
	
}
