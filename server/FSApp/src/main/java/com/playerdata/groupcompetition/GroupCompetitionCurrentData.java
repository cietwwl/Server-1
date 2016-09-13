package com.playerdata.groupcompetition;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.groupcompetition.stageimpl.CompetitionEventsStatus;

/**
 * 
 * 帮派争霸当前的一些数据
 * 
 * @author CHEN.P
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
class GroupCompetitionCurrentData {

	@JsonProperty("1")
	private long _heldTime; // 举办的时间
	@JsonProperty("2")
	private Map<CompetitionEventsStatus, List<String>> _relativeGroups; // 参与的帮派
	@JsonProperty("3")
	private CompetitionEventsStatus _currentStatus; // 当前的赛事阶段
	@JsonProperty("4")
	private boolean _currentStatusFinished; // 当前的赛事阶段是否已经完结
}
