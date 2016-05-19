package com.playerdata.mgcsecret.data;

import java.util.ArrayList;

import com.rwbase.dao.copy.pojo.ItemInfo;

public class MSStageInfo {
	private String stageKey;	//关卡ID
	private ArrayList<Integer> monsterBuff; //关卡怪物buff
	private int enimyGroupId; //怪物组id
	private ArrayList<ItemInfo> dropItem;	//关卡掉落物品
	
	public MSStageInfo(String stageKey, String monsterBuffStr, String enimyStr, String dropStr){
		this.stageKey = stageKey;
		this.monsterBuff = getRandomMonsterBuff(monsterBuffStr);
		this.enimyGroupId = getRandomEnimyGroupID(enimyStr);
		this.dropItem = getDropItem(dropStr);
	}
	
	public String getStageKey() {
		return stageKey;
	}

	public ArrayList<Integer> getMonsterBuff() {
		return monsterBuff;
	}

	public int getEnimyGroupId() {
		return enimyGroupId;
	}

	public ArrayList<ItemInfo> getDropItem() {
		return dropItem;
	}

	private ArrayList<Integer> getRandomMonsterBuff(String monsterBuffStr){
		if(monsterBuffStr == null || monsterBuffStr.isEmpty()) return new ArrayList<Integer>();
		return null;
	}
	
	private int getRandomEnimyGroupID(String enimyStr){
		if(enimyStr == null || enimyStr.isEmpty()) return 0;
		return 0;
		
	}
	
	private ArrayList<ItemInfo> getDropItem(String dropStr){
		if(dropStr == null || dropStr.isEmpty()) return new ArrayList<ItemInfo>();
		return null;
	}
}
