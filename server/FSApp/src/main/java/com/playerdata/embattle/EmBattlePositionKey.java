package com.playerdata.embattle;
/*
 * @author xiaofei
 * @date 2016年8月23日 上午11:37:56
 * @Description 存储uid+id,type,key,position中的key;
 */
public enum EmBattlePositionKey {
	posDefault("default"),
	posBattleTower("tower_battle"),
	posCopy("copy"),
	posGroupCopy("group_copy"),
	posFortune("copy_type_fortune"),
	posTrial("copy_type_trial"),
	posCelestrial("copy_type_celestrial_{0}"),
	posTower("tower"),
	posMagicSecret("magic_secret"),
	posGroupComptition("group_competition"),
	posWorldBoss("world_boss");

	
	private String key;
	private  EmBattlePositionKey(String key){
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	} 
	
	
	
}
