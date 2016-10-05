package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class GCompEventsGlobalData {

	@JsonProperty("1")
	private List<GCompAgainst> matches = new ArrayList<GCompAgainst>();
	@IgnoreSynField
	private List<GCompAgainst> matchesRO = Collections.unmodifiableList(matches);
	@JsonProperty("2")
	private GCEventsType matchNumType; // 开始的比赛类型
	@IgnoreSynField
	@JsonProperty("3")
	private Map<GCEventsType, GCompEventsData> eventsDataMap = new HashMap<GCEventsType, GCompEventsData>();
	
	public List<GCompAgainst> getMatches() {
		return matchesRO;
	}
	
	public GCEventsType getMatchNumType() {
		return matchNumType;
	}
	
	public void setMatchNumType(GCEventsType matchNumType) {
		this.matchNumType = matchNumType;
	}
	
	public void add(GCEventsType eventsType, GCompEventsData eventsData) {
		this.eventsDataMap.put(eventsType, eventsData);
		this.matches.addAll(eventsData.getAgainsts());
	}
	
	public GCompEventsData getEventsData(GCEventsType eventsType) {
		return this.eventsDataMap.get(eventsType);
	}
	
	public void clear() {
		this.eventsDataMap.clear();
		this.matches.clear();
		this.matchNumType = null;
	}

	@Override
	public String toString() {
		return "GCompMatchSynData [matches=" + matches + ", matchNumType=" + matchNumType + "]";
	}
}
