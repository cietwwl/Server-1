package com.playerdata.groupcompetition.holder.data;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.util.GCompStageType;

@SynClass
public class GCompSelectionData {

	private List<GCompMatchSynData> lastMatches;
	private GCompStageType lastMatchNumType;
	private List<GCompGroupSynData> historyChampion;
}
