package com.playerdata.fightinggrowth;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@SynClass
public class FSUserFightingGrowthData {

	@JsonProperty("1")
	@Id
	@IgnoreSynField
	private String userId; // 所属的玩家的UserId
	@JsonProperty("2")
	private String _currentTitleKey; // 当前的称号等级
	
	public void setUserId(String pUserId) {
		this.userId = pUserId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getCurrentTitleKey() {
		return _currentTitleKey;
	}
	
	public void setCurrentTitlKey(String pKey) {
		this._currentTitleKey = pKey;
	}
	
}
