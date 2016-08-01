package com.rwbase.dao.assistant.pojo;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.assistant.cfg.AssistantCfg;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

@SynClass
public class AssistantData {

	private List<AssistantEventID> assistantEvent;

	@IgnoreSynField
	private ArrayList<AssistantCfg> cfgList = new ArrayList<AssistantCfg>();

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
