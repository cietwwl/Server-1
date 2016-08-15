package com.rwbase.dao.assistant.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.assistant.cfg.AssistantCfg;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

@SynClass
public class AssistantData {

	private List<AssistantEventID> assistantEvent;
	private HashMap<AssistantEventID,String> extraParameters; 

	@IgnoreSynField
	private ArrayList<AssistantCfg> cfgList = new ArrayList<AssistantCfg>();

	public void putParam(AssistantEventID id, String param){
		if (id == null || StringUtils.isBlank(param)){
			return;
		}
		if (extraParameters == null){
			extraParameters = new HashMap<AssistantEventID,String>();
		}
		extraParameters.put(id, param);
	}
	
	public HashMap<AssistantEventID, String> getExtraParameters() {
		return extraParameters;
	}

	public void setExtraParameters(HashMap<AssistantEventID, String> extraParameters) {
		this.extraParameters = extraParameters;
	}

	public List<AssistantEventID> getAssistantEvent() {
		return assistantEvent;
	}

	public void setAssistantEvent(List<AssistantEventID> assistantEvent) {
		this.assistantEvent = assistantEvent;
	}

	public ArrayList<AssistantCfg> getCfgList() {
		return cfgList;
	}

}
