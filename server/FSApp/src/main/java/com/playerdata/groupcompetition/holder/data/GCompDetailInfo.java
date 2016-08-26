package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;
import java.util.List;

import com.bm.group.GroupBM;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;

/**
 * 
 * 帮派争霸中赛事直播的同步数据
 * 
 * @author CHEN.P
 *
 */
@SynClass
public class GCompDetailInfo {

	private int matchId;
	private List<GCompGroupScore> groupScores;
	private GCompPersonalScore mvp;
	
	private static GCompGroupScore createNewGroupScoreData(String groupId) {
		Group group = GroupBM.get(groupId);
		if (group != null) {
			GroupBaseDataIF groupBaseData = group.getGroupBaseDataMgr().getGroupData();
			return GCompGroupScore.createNew(groupId, groupBaseData.getGroupName(), groupBaseData.getIconId());
		} else {
			return GCompGroupScore.createNew("", "", "");
		}
	}
	
	public static GCompDetailInfo createNew(int matchId, String idOfGroupA, String idOfGroupB) {
		GCompDetailInfo instance = new GCompDetailInfo();
		instance.matchId = matchId;
		instance.groupScores = new ArrayList<GCompGroupScore>();
		instance.groupScores.add(createNewGroupScoreData(idOfGroupA));
		instance.groupScores.add(createNewGroupScoreData(idOfGroupB));
		return instance;
	}
	
	public int getMatchId() {
		return matchId;
	}
	
	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}
	
	public List<GCompGroupScore> getGroupScores() {
		return groupScores;
	}
	
	public void setGroupScores(List<GCompGroupScore> groupScores) {
		this.groupScores = groupScores;
	}
	
	public GCompPersonalScore getMvp() {
		return mvp;
	}
	
	public void setMvp(GCompPersonalScore mvp) {
		this.mvp = mvp;
	}
}
