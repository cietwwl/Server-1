package com.playerdata.groupcompetition.stageimpl;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupLevelCfg;
import com.rwbase.dao.group.pojo.cfg.dao.GroupLevelCfgDAO;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GroupCommonProto.GroupPost;

@SynClass
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class GCGroup implements IGCGroup {
	
	@JsonProperty("1")
	private String groupId; // 帮派的id
	@JsonProperty("2")
	private String groupName; // 帮派的名字
	@JsonProperty("3")
	private String leaderName; // 帮主的名字，客户端需要同步的字段
	@JsonProperty("4")
	private String assistantName; // 副帮主的名字，客户端需要同步的字段
	@JsonProperty("5")
	private String groupIcon; // 帮派的图标
	@JsonProperty("6")
	private int gCompScore; // 当前的积分
	@JsonIgnore
	private int historyNum; // 客户端需要用的数据
	@JsonIgnore
	private int upNum; // 客户端需要用的数据
//	@JsonIgnore
//	private String descr;
	@JsonProperty("7")
	private long gCompPower;
	@JsonProperty("8")
	private int groupLv;
	@JsonProperty("9")
	private int memberNum;
	@JsonProperty("10")
	private int maxMemberNum;
	
	public GCGroup() {}
	
	public static GCGroup createNew(String groupId) {
		GCGroup instance = new GCGroup();
		Group group;
		if (groupId == null || groupId.length() == 0 || (group = GroupBM.get(groupId)) == null) {
			instance.groupId = "";
			instance.groupName = "";
			instance.leaderName = "";
			instance.groupIcon = "";
			instance.assistantName = "";
		} else {
			GroupBaseDataIF baseData = group.getGroupBaseDataMgr().getGroupData();
			GroupMemberMgr groupMemberMgr = group.getGroupMemberMgr();
			Map<Integer, List<GroupMemberDataIF>> map = groupMemberMgr.getAllMemberByPost();
			instance.groupId = groupId;
			instance.groupName = baseData.getGroupName();
			instance.leaderName = groupMemberMgr.getGroupLeader().getName();
			instance.groupIcon = baseData.getIconId();
			List<GroupMemberDataIF> assistants = map.get(GroupPost.ASSISTANT_LEADER_VALUE);
			if (assistants != null && assistants.size() > 0) {
				instance.assistantName = assistants.get(0).getName();
			} else {
				instance.assistantName = "";
			}
			instance.memberNum = group.getGroupMemberMgr().getGroupMemberSize();
			instance.groupLv = baseData.getGroupLevel();
			GroupLevelCfg levelTemplate = GroupLevelCfgDAO.getDAO().getLevelCfg(instance.groupLv);
			instance.maxMemberNum = levelTemplate.getMaxMemberLimit();
			GCompFightingItem fightingRankItem = GCompFightingRankMgr.getFightingRankItem(groupId);
			if (fightingRankItem != null) {
				instance.gCompPower = fightingRankItem.getGroupFight();
			}
		}
//		instance.descr = "GCGroup [groupId=" + groupId + ", groupName=" + instance.groupName + "]";
		return instance;
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public String getGroupName() {
		return groupName;
	}

	@Override
	public String getIcon() {
		return groupIcon;
	}

	@Override
	public int getGCompScore() {
		return gCompScore;
	}
	
	public void updateScore(int offset) {
		this.gCompScore += offset;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public String getAssistantName() {
		return assistantName;
	}

	@Override
	public String toString() {
		return "GCGroup [groupId=" + groupId + ", groupName=" + groupName + ", gCompScore=" + gCompScore + ", gCompPower=" + gCompPower + ", groupLv=" + groupLv + ", memberNum=" + memberNum
				+ ", maxMemberNum=" + maxMemberNum + "]";
	}

	public int getGroupLv() {
		return groupLv;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public void setGroupLv(int groupLv) {
		this.groupLv = groupLv;
	}

	public void setMemberNum(int memberNum) {
		this.memberNum = memberNum;
	}

	public void setMaxMemberNum(int maxMemberNum) {
		this.maxMemberNum = maxMemberNum;
	}

	public void setGroupIcon(String groupIcon) {
		this.groupIcon = groupIcon;
	}
	
	public void setFighting(long fighting) {
		this.gCompPower = fighting;
	}

}
