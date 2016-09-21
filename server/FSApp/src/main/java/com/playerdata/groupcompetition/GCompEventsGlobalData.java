package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.playerdata.groupcompetition.util.GCEventsType;

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
	@JsonProperty("3")
	private GCEventsType _currentStatus; // 当前的赛事阶段（16强，8强。。。）
	@JsonProperty("4")
	private boolean _currentStatusFinished; // 当前的赛事阶段是否已经完结
	@JsonProperty("5")
	private GCEventsType _firstEventsType; // 是从哪一强开始比赛的

	public long getHeldTime() {
		return _heldTime;
	}

	public void setHeldTime(long _heldTime) {
		this._heldTime = _heldTime;
	}

	public Map<GCEventsType, List<String>> getRelativeGroups() {
		return _relativeGroups;
	}

	public void addRelativeGroups(GCEventsType type, List<String> relativeGroups) {
		this._relativeGroups.put(type, new ArrayList<String>(relativeGroups));
	}

	public GCEventsType getCurrentStatus() {
		return _currentStatus;
	}

	public void setCurrentStatus(GCEventsType currentStatus) {
		this._currentStatus = currentStatus;
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
	
	void reset() {
		this._heldTime = 0;
		this._relativeGroups.clear();
		this._currentStatus = null;
		this._currentStatusFinished = false;
		this._firstEventsType = null;
	}
}
