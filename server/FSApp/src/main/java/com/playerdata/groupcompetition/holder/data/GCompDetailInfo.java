package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class GCompDetailInfo {

	@JsonProperty("1")
	private int matchId;
	@JsonProperty("2")
	private List<GCompGroupScoreRecord> groupScores;
	@JsonProperty("3")
	private GCompPersonalScore mvp;

	private static GCompGroupScoreRecord createNewGroupScoreData(String groupId) {
		Group group = GroupBM.getInstance().get(groupId);
		if (group != null) {
			GroupBaseDataIF groupBaseData = group.getGroupBaseDataMgr().getGroupData();
			return GCompGroupScoreRecord.createNew(groupId, groupBaseData.getGroupName(), groupBaseData.getIconId());
		} else {
			return GCompGroupScoreRecord.createNew("", "", "");
		}
	}

	public static GCompDetailInfo createNew(int matchId, String idOfGroupA, String idOfGroupB) {
		GCompDetailInfo instance = new GCompDetailInfo();
		instance.matchId = matchId;
		instance.groupScores = new ArrayList<GCompGroupScoreRecord>();
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

	public List<GCompGroupScoreRecord> getGroupScores() {
		return groupScores;
	}

	public void setGroupScores(List<GCompGroupScoreRecord> groupScores) {
		this.groupScores = groupScores;
	}

	public GCompPersonalScore getMvp() {
		return mvp;
	}

	public void setMvp(GCompPersonalScore mvp) {
		this.mvp = mvp;
	}

	public GCompGroupScoreRecord getByGroupId(String groupId) {
		for (GCompGroupScoreRecord g : groupScores) {
			if (g.getGroupId().equals(groupId)) {
				return g;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "GCompDetailInfo [matchId=" + matchId + ", groupScores=" + groupScores + ", mvp=" + mvp + "]";
	}
}
