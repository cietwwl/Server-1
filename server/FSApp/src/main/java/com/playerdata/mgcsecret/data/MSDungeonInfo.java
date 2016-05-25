package com.playerdata.mgcsecret.data;

import java.util.ArrayList;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.copy.pojo.ItemInfo;

@SynClass
public class MSDungeonInfo {
	private String dungeonKey;	//关卡ID
	private ArrayList<Integer> monsterBuff; //关卡怪物buff
	private int enimyGroupId; //怪物组id
	private ArrayList<ItemInfo> dropItem;	//关卡掉落物品
	private int finishStar;  //完成的星级（只有完成后才会有这个值，失败为0）

	public MSDungeonInfo(String dungeonKey, ArrayList<Integer> monsterBuff, int enimyGroupId, ArrayList<ItemInfo> dropItem){
		this.dungeonKey = dungeonKey;
		this.monsterBuff = monsterBuff;
		this.enimyGroupId = enimyGroupId;
		this.dropItem = dropItem;
	}
	
	public MSDungeonInfo(){}
	
	public int getFinishStar() {
		return finishStar;
	}

	public void setFinishStar(int finishStar) {
		this.finishStar = finishStar;
	}
	
	public String getDungeonKey() {
		return dungeonKey;
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
}
