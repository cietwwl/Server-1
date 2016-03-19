package com.rwbase.dao.assistant.pojo;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

@SynClass
public class AssistantData {

	private AssistantEventID assistantEvent;
	
	
	public AssistantEventID getAssistantEventID() {
		return assistantEvent;
	}


	public void setAssistantEventID(AssistantEventID assistantEvent) {
		this.assistantEvent = assistantEvent;
	}
	
}
