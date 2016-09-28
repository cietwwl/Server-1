package com.playerdata.groupcompetition.stageimpl;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompEventsStatus;

@SynClass
public class GCompAgainst implements IGCAgainst {
	
	private int matchId; // 对垒的id
	private GCGroup groupA; // 帮派A
	private GCGroup groupB; // 帮派B
	@IgnoreSynField
	private GCGroup winGroup; // 胜利的帮派
	private String winner; // 胜利的帮派id
	@SuppressWarnings("unused")
	private GCompEventsStatus curStatus; // 当前的战斗状态（同步客户端需要）
	@SuppressWarnings("unused")
	private GCEventsType topType; // 处在哪个阶段（16强、8强。。。）（同步客户端需要）
	@SuppressWarnings("unused")
	private int position; // 位置（同步客户端需要）
	private boolean championEvents; // 是否冠军争夺战，因为Final的时候，会有3、4名争夺，所以这里要判断哪一场是冠军争夺

	GCompAgainst(String idOfGroupA, String idOfGroupB, GCEventsType pTopType, int pPosition) {
		this.matchId = GroupCompetitionMgr.getInstance().getNextAgainstId();
		this.groupA = new GCGroup(idOfGroupA);
		this.groupB = new GCGroup(idOfGroupB);
		this.curStatus = GCompEventsStatus.NONE;
		this.topType = pTopType;
		this.position = pPosition;
	}
	
	public void setWinGroupId(String winGroupId) {
		this.winner = winGroupId;
		this.winGroup = groupA.getGroupId().equals(winner) ? groupA : groupB;
	}
	
	public String getWinGroupId() {
		return winner;
	}
	
	public boolean isChampionEvents() {
		return championEvents;
	}

	public void setChampionEvents(boolean championEvents) {
		this.championEvents = championEvents;
	}
	
	@Override
	public int getId() {
		return matchId;
	}
	
	@Override
	public void setCurrentStatus(GCompEventsStatus currentStatus) {
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
	public GCGroup getGroupA() {
		return groupA;
	}

	@Override
	public GCGroup getGroupB() {
		return groupB;
	}

	@Override
	public GCGroup getWinGroup() {
		return winGroup;
	}

	@Override
	public String toString() {
		return "GCompAgainst [matchId=" + matchId + ", groupA=" + groupA + ", groupB=" + groupB + "]";
	}

}
