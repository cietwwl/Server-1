package com.playerdata.groupcompetition.stageimpl;

import java.util.List;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.GCCombatRecord;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.data.IGCUnit;
import com.playerdata.groupcompetition.data.match.IGCMatcher;
import com.playerdata.groupcompetition.util.GCEventsStatus;
import com.playerdata.groupcompetition.util.GCEventsType;

@SynClass
public class GCompAgainst implements IGCAgainst {
	
	private int matchId; // 对垒的id
	private GCGroup groupA; // 帮派A
	private GCGroup groupB; // 帮派B
	@IgnoreSynField
	private GCGroup winGroup; // 胜利的帮派
	private String winner; // 胜利的帮派id
	private GCEventsStatus curStatus; // 当前的战斗状态
	private GCEventsType topType; // 处在哪个阶段（16强、8强。。。）
	private int position; // 位置
	
	GCompAgainst(String idOfGroupA, String idOfGroupB, GCEventsType pTopType, int pPosition) {
		this.matchId = GroupCompetitionMgr.getInstance().getNextAgainstId();
		this.groupA = new GCGroup(idOfGroupA);
		this.groupB = new GCGroup(idOfGroupB);
		this.curStatus = GCEventsStatus.NONE;
		this.topType = pTopType;
		this.position = pPosition;
	}
	
	@Override
	public int getId() {
		return matchId;
	}
	
	@Override
	public void setCurrentStatus(GCEventsStatus currentStatus) {
		this.curStatus = currentStatus;
	}

	@Override
	public boolean isGroupInThisAgainst(String groupId) {
		if(this.groupA != null && groupA.getGroupId().equals(groupId)) {
			return true;
		} else if(this.groupB != null && groupB.getGroupId().equals(groupId)) {
			return true;
		}
		return false;
	}

	@Override
	public IGCGroup getGroupA() {
		return groupA;
	}

	@Override
	public IGCGroup getGroupB() {
		return groupB;
	}

	@Override
	public IGCGroup getWinGroup() {
		return winGroup;
	}

	@Override
	public List<GCCombatRecord> getHistorys() {
		return null;
	}

	@Override
	public IGCMatcher<IGCUnit> getMatcher() {
		return null;
	}

}
