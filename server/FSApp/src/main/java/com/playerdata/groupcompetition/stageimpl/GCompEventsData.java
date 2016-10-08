package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompEventsStatus;

@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class GCompEventsData {

	@JsonProperty("1")
	private GCEventsType _eventsType; // 赛事类型
	@JsonProperty("2")
	private List<GCompAgainst> _againsts; // 对阵关系
	private List<GCompAgainst> _againstsRO; // 对阵关系（只读）
	@JsonProperty("3")
	private GCompEventsStatus _currentStatus = GCompEventsStatus.NONE; // 赛事的当前状态
	@JsonProperty("4")
	private List<String> _winGroupIds;
	private List<String> _winGroupIdsRO;
	@JsonProperty("5")
	private List<String> _loseGroupIds;
	private List<String> _loseGroupIdsRO;
	@JsonProperty("6")
	private List<String> _relativeGroupIds;
	private List<String> _relativeGroupIdsRO;
	
	void setEventsType(GCEventsType pEventsType) {
		this._eventsType = pEventsType;
	}
	
	public GCEventsType getEventsType() {
		return _eventsType;
	}
	
	void setAgainsts(List<GCompAgainst> list) {
		this._againsts = new ArrayList<GCompAgainst>(list);
		this._againstsRO = Collections.unmodifiableList(_againsts);
	}
	
	public List<GCompAgainst> getAgainsts() {
		if (_againstsRO == null) {
			synchronized (this._againsts) {
				if (_againstsRO == null) {
					_againstsRO = Collections.unmodifiableList(_againsts);
				}
			}
		}
		return _againstsRO;
	}
	
	void setCurrentStatus(GCompEventsStatus pStatus) {
		this._currentStatus = pStatus;
	}
	
	public GCompEventsStatus getCurrentStatus() {
		return _currentStatus;
	}
	
	void setWinGroupIds(List<String> groupIds) {
		this._winGroupIds = new ArrayList<String>(groupIds);
		this._winGroupIdsRO = Collections.unmodifiableList(_winGroupIds);
	}
	
	public List<String> getWinGroupIds() {
		return _winGroupIdsRO;
	}
	
	void setLostGroupIds(List<String> groupIds) {
		this._loseGroupIds = new ArrayList<String>(groupIds);
		this._loseGroupIdsRO = Collections.unmodifiableList(_loseGroupIds);
	}
	
	public List<String> getLoseGroupIds() {
		return _loseGroupIdsRO;
	}
	
	void setRelativeGroupIds(List<String> groupIds) {
		this._relativeGroupIds = new ArrayList<String>(groupIds);
		this._relativeGroupIdsRO = Collections.unmodifiableList(_relativeGroupIds);
	}
	
	public List<String> getRelativeGroupIds() {
		if (_relativeGroupIdsRO == null) {
			synchronized (_relativeGroupIds) {
				if (_relativeGroupIdsRO == null) {
					this._relativeGroupIdsRO = Collections.unmodifiableList(_relativeGroupIds);
				}
			}
		}
		return _relativeGroupIdsRO;
	}
}
