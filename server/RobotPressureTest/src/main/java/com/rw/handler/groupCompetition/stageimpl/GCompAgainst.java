package com.rw.handler.groupCompetition.stageimpl;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

import com.rw.handler.groupCompetition.data.group.IGCAgainst;
import com.rw.handler.groupCompetition.data.group.IGCGroup;
import com.rw.handler.groupCompetition.util.GCEventsType;
import com.rw.handler.groupCompetition.util.GCompEventsStatus;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class GCompAgainst implements IGCAgainst {
	
	@JsonProperty("1")
	private int matchId; // 对垒的id
	@JsonProperty("2")
	private GCGroup groupA; // 帮派A
	@JsonProperty("3")
	private GCGroup groupB; // 帮派B
	@JsonProperty("4")
	private String winner; // 胜利的帮派id
	@JsonProperty("5")
	private GCompEventsStatus curStatus; // 当前的战斗状态（同步客户端需要）
	@JsonProperty("6")
	private GCEventsType topType; // 处在哪个阶段（16强、8强。。。）（同步客户端需要）
	@JsonProperty("7")
	private int position; // 位置（同步客户端需要）
	@JsonProperty("8")
	private boolean championEvents; // 是否冠军争夺战，因为Final的时候，会有3、4名争夺，所以这里要判断哪一场是冠军争夺

	public GCompAgainst() {}
	
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
	public String toString() {
		return "GCompAgainst [matchId=" + matchId + ", groupA=" + groupA + ", groupB=" + groupB + ", winner=" + winner + "]";
	}

	@Override
	public IGCGroup getWinGroup() {
		return null;
	}

	public GCompEventsStatus getCurStatus() {
		return curStatus;
	}

	public GCEventsType getTopType() {
		return topType;
	}
}
