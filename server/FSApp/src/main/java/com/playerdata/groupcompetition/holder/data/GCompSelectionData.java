package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.util.GCEventsType;

@SynClass
public class GCompSelectionData {

	private List<GCompEventsSynData> lastMatches;
	private GCEventsType lastMatchNumType;
	private List<GCGroup> historyChampion;
	
	public static GCompSelectionData createNew() {
		GCompSelectionData data = new GCompSelectionData();
		data.lastMatches = new ArrayList<GCompEventsSynData>();
		data.lastMatchNumType = GCEventsType.TOP_16;
		data.historyChampion = new ArrayList<GCGroup>();
		return data;
	}
	
	public void addChampion(GCGroup group) {
		this.historyChampion.add(group);
	}
	
	public void setLastMatchNumType(GCEventsType eventsType) {
		this.lastMatchNumType = eventsType;
	}
	
	public void addLastMatches(List<GCompEventsSynData> dataList) {
		lastMatches.addAll(dataList);
	}
}
