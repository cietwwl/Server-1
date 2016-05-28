package com.playerdata.mgcsecret.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.team.TeamInfo;
import com.rwbase.dao.copy.pojo.ItemInfo;

@SynClass
public class MSDungeonInfo {
	private String dungeonKey;	//关卡ID
	private ArrayList<Integer> monsterBuff; //关卡怪物buff
	private TeamInfo enimyTeam; //怪物队伍信息
	private List<ItemInfo> dropItem;	//关卡掉落物品
	private int finishStar;  //完成的星级（只有完成后才会有这个值，失败为0）

	public MSDungeonInfo(String dungeonKey, ArrayList<Integer> monsterBuff, TeamInfo enimyTeam, List<? extends ItemInfo> dropItem){
		this.dungeonKey = dungeonKey;
		this.monsterBuff = monsterBuff;
		this.enimyTeam = enimyTeam;
		this.dropItem = Collections.unmodifiableList(dropItem);
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

	public TeamInfo getEnimyTeam() {
		return enimyTeam;
	}

	public List<ItemInfo> getDropItem() {
		return dropItem;
	}
}
