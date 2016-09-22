package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompEventsStatus;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * 帮派争霸赛事全局数据
 * 
 * @author CHEN.P
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
class GCompEventsGlobalData {

	@JsonProperty("1")
	private long _heldTime; // 举办的时间
	@JsonProperty("2")
	private Map<GCEventsType, List<String>> _relativeGroups = new HashMap<GCEventsType, List<String>>(); // 参与的帮派
	private Map<GCEventsType, List<String>> _relativeGroupsRO = new HashMap<GCEventsType, List<String>>();
	@JsonProperty("3")
	private GCEventsType _currentEventsType; // 当前的赛事阶段（16强，8强。。。）
	@JsonProperty("4")
	private boolean _currentStatusFinished; // 当前的赛事阶段是否已经完结
	@JsonProperty("5")
	private GCEventsType _firstEventsType; // 是从哪一强开始比赛的
	@JsonProperty("6")
	private GCompEventsStatus _currentStatus; // 当前的状态
	

	public long getHeldTime() {
		return _heldTime;
	}

	public void setHeldTime(long _heldTime) {
		this._heldTime = _heldTime;
	}

	public List<String> getCurrentRelativeGroupIds() {
		return _relativeGroupsRO.get(_currentEventsType);
	}
	
	public List<String> getRelativeGroupIds(GCEventsType eventsType) {
		return _relativeGroupsRO.get(eventsType);
	}

	public void addRelativeGroups(GCEventsType type, List<String> relativeGroups) {
		List<String> groupIds = new ArrayList<String>(relativeGroups);
		this._relativeGroups.put(type, groupIds);
		this._relativeGroupsRO.put(type, Collections.unmodifiableList(groupIds));
	}

	public GCEventsType getCurrentEventsType() {
		return _currentEventsType;
	}

	public void setCurrentEventsType(GCEventsType currentEventsType) {
		this._currentEventsType = currentEventsType;
	}

	public boolean isCurrentStatusFinished() {
		return _currentStatusFinished;
	}

	public void setCurrentStatusFinished(boolean currentStatusFinished) {
		this._currentStatusFinished = currentStatusFinished;
	}
	
	/**
	 * 
	 * 获取本次比赛是从哪一强开始的
	 * 
	 * @return
	 */
	public GCEventsType getFirstEventsType() {
		return _firstEventsType;
	}

	public void setFirstEventsType(GCEventsType firstEventsType) {
		this._firstEventsType = firstEventsType;
	}
	
	public GCompEventsStatus getCurrentStatus() {
		return _currentStatus;
	}

	public void setCurrentStatus(GCompEventsStatus currentStatus) {
		this._currentStatus = currentStatus;
	}
	
	void reset() {
		this._heldTime = 0;
		this._relativeGroups.clear();
		this._currentEventsType = null;
		this._currentStatusFinished = false;
		this._firstEventsType = null;
		this._currentStatus = null;
	}
}
