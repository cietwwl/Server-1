package com.playerdata.groupcompetition.stageimpl;

import java.util.List;

import com.bm.group.GroupBM;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.data.IGCUnit;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;

@SynClass
public class GCGroup implements IGCGroup {
	
	private final String groupId; // 帮派的id
	private final String groupName; // 帮派的名字
	private final String leaderName; // 帮主的名字
	private final String assistantName; // 副帮主的名字
	@IgnoreSynField
	private final String _groupIcon; // 帮派的图标
	private int gCompScore; // 当前的积分
	private int historyNum;
	private int upNum;
	
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
			this.groupId = groupId;
			this.groupName = baseData.getGroupName();
			this.leaderName = group.getGroupMemberMgr().getGroupLeader().getName();
			this._groupIcon = baseData.getIconId();
			this.assistantName = "";
		}
	}

	@Override
	public List<IGCUnit> getAvaliableSource() {
		return null;
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

	@Override
	public List<IGCUnit> getAllUnits() {
		return null;
	}

	@Override
	public void addUnit(IGCUnit unit) {
		
	}

}
