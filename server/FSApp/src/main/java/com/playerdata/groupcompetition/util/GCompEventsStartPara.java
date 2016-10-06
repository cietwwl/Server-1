package com.playerdata.groupcompetition.util;

import java.util.List;

public class GCompEventsStartPara {

	private GCEventsType eventsType;
	private List<String> winGroupIds; // 战胜的帮派id
	private List<String> loseGroupIds; // 战败的帮派id
	
	public GCEventsType getEventsType() {
		return eventsType;
	}
	
	public void setEventsType(GCEventsType eventsType) {
		this.eventsType = eventsType;
	}
	
	public List<String> getWinGroupIds() {
		return winGroupIds;
	}
	
	public void setWinGroupIds(List<String> relativeGroupIds) {
		this.winGroupIds = relativeGroupIds;
	}

	public List<String> getLoseGroupIds() {
		return loseGroupIds;
	}

	public void setLoseGroupIds(List<String> loseGroupIds) {
		this.loseGroupIds = loseGroupIds;
	}
}
