package com.rw.handler.groupFight.dataForClient;

import java.util.List;

import com.rw.handler.groupFight.dataForRank.GFOnlineHurtItem;
import com.rw.handler.groupFight.dataForRank.GFOnlineKillItem;

/**
 * 用于前端请求帮派内部排名的结构
 * @author aken
 */
public class GFOnlineGroupInnerInfo {
	private List<GFOnlineHurtItem> hurtRank; //帮派内部伤害排行，只三个
	private List<GFOnlineKillItem> killRank; //帮派内部杀敌数排行，只三个
	private int totalKill;	//总杀敌数
	private String groupName;
	
	public List<GFOnlineHurtItem> getHurtRank() {
		return hurtRank;
	}

	public void setHurtRank(List<GFOnlineHurtItem> hurtRank) {
		this.hurtRank = hurtRank;
	}

	public List<GFOnlineKillItem> getKillRank() {
		return killRank;
	}

	public void setKillRank(List<GFOnlineKillItem> killRank) {
		this.killRank = killRank;
	}

	public int getTotalKill() {
		return totalKill;
	}

	public void setTotalKill(int totalKill) {
		this.totalKill = totalKill;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
