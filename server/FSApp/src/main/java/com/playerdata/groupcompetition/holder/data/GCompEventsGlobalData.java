package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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

	private List<GCompAgainst> matches = null;
	@IgnoreSynField
	private List<GCompAgainst> matchesRO = null;
	@JsonProperty("1")
	private GCEventsType matchNumType; // 开始的比赛类型
	@IgnoreSynField
	@JsonProperty("2")
	private Map<GCEventsType, GCompEventsData> eventsDataMap = new HashMap<GCEventsType, GCompEventsData>();
	
	private void initMatchesList() {
		this.matches = new ArrayList<GCompAgainst>();
		for (Iterator<GCEventsType> itr = eventsDataMap.keySet().iterator(); itr.hasNext();) {
			matches.addAll(eventsDataMap.get(itr.next()).getAgainsts());
		}
	}
	
	public List<GCompAgainst> getMatches() {
		if (matches == null) {
			synchronized (eventsDataMap) {
				if (matches == null) {
					initMatchesList();
				}
			}
		}
		if (matchesRO == null) {
			synchronized (matches) {
				if (matchesRO == null) {
					matchesRO = Collections.unmodifiableList(matches);
				}
			}
		}
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
		this.initMatchesList();
	}
	
	public GCompEventsData getEventsData(GCEventsType eventsType) {
		return this.eventsDataMap.get(eventsType);
	}
	
	public void clear() {
		this.eventsDataMap.clear();
		if (this.matches != null) {
			this.matches.clear();
		}
		this.matches = null;
		this.matchNumType = null;
	}

	@Override
	public String toString() {
		return "GCompMatchSynData [matches=" + matches + ", matchNumType=" + matchNumType + "]";
	}
}
