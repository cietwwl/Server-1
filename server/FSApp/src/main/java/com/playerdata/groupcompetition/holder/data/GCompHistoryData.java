package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;

/**
 * 
 * 帮派战历史数据
 * 
 * @author CHEN.P
 *
 */
@SynClass
@JsonAutoDetect(setterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GCompHistoryData {

	@JsonProperty("1")
	private List<GCompAgainst> lastMatches; // 上一次帮派争霸的比赛
	@JsonProperty("2")
	private GCEventsType lastMatchNumType; // 上一次帮派争霸的初赛类型
	@IgnoreSynField
	@JsonProperty("3")
	private List<GCGroup> historyChampion; // 历史冠军
	@JsonProperty("4")
	private long startTime;
	@JsonProperty("5")
	private long endTime;
	@IgnoreSynField
	@JsonProperty("6")
	private List<String> _selectedGroupIds;
	
	public static GCompHistoryData createNew() {
		GCompHistoryData data = new GCompHistoryData();
		data.lastMatches = new ArrayList<GCompAgainst>();
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
	
	public void setSelectedGroupIds(List<String> groupIds) {
		this._selectedGroupIds = new ArrayList<String>(groupIds);
	}
	
	public List<String> getSelectedGroupIds() {
		return Collections.unmodifiableList(_selectedGroupIds);
	}
	
	public void copy(GCompEventsGlobalData copy, long startTime, long endTime) {
		this.lastMatches.clear();
		this.lastMatchNumType = copy.getMatchNumType();
		this.lastMatches.addAll(copy.getMatches());
		this.startTime = startTime;
		this.endTime = endTime;
		GCompAgainst against;
		for (int i = lastMatches.size(); i-- > 0;) {
			against = lastMatches.get(i);
			if (against.isChampionEvents()) {
				this.historyChampion.add(against.getWinGroup());
				break;
			}
		}
	}
	
	public List<GCGroup> getHistoryChampions() {
		return Collections.unmodifiableList(historyChampion);
	}
	
	public List<GCompAgainst> getAgainsts() {
		return Collections.unmodifiableList(lastMatches);
	}

	@Override
	public String toString() {
		return "GCompHistoryData [lastMatches=" + lastMatches + ", lastMatchNumType=" + lastMatchNumType + ", historyChampion=" + historyChampion + ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}
	
	
}
