package com.playerdata.groupcompetition.stageimpl;

import java.util.List;
import java.util.Map;

import com.bm.group.GroupBM;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GroupCommonProto.GroupPost;

@SynClass
public class GCGroup implements IGCGroup {
	
	private String groupId; // 帮派的id
	private String groupName; // 帮派的名字
	private String leaderName; // 帮主的名字，客户端需要同步的字段
	private String assistantName; // 副帮主的名字，客户端需要同步的字段
	@IgnoreSynField
	private String _groupIcon; // 帮派的图标
	private int gCompScore; // 当前的积分
	@SuppressWarnings("unused")
	private int historyNum; // 客户端需要用的数据
	@SuppressWarnings("unused")
	private int upNum; // 客户端需要用的数据
	private String descr;
	
	public GCGroup() {}
	
	GCGroup(String groupId) {
		if (groupId == null || groupId.length() == 0) {
			this.groupId = "";
			this.groupName = "";
			this.leaderName = "";
			this._groupIcon = "";
			this.assistantName = "";
		} else {
			Group group = GroupBM.get(groupId);
			GroupBaseDataIF baseData = group.getGroupBaseDataMgr().getGroupData();
			Map<Integer, List<GroupMemberDataIF>> map = group.getGroupMemberMgr().getAllMemberByPost();
			this.groupId = groupId;
			this.groupName = baseData.getGroupName();
			this.leaderName = group.getGroupMemberMgr().getGroupLeader().getName();
			this._groupIcon = baseData.getIconId();
			List<GroupMemberDataIF> assistants = map.get(GroupPost.ASSISTANT_LEADER_VALUE);
			if (assistants != null && assistants.size() > 0) {
				this.assistantName = assistants.get(0).getName();
			} else {
				this.assistantName = "";
			}
		}
		this.descr = "GCGroup [groupId=" + groupId + ", groupName=" + groupName + "]";
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
		return _groupIcon;
	}

	@Override
	public int getGCompScore() {
		return gCompScore;
	}
	
	public void updateScore(int offset) {
		this.gCompScore += offset;
	}

	@Override
	public String toString() {
		return descr;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public String getAssistantName() {
		return assistantName;
	}

}
