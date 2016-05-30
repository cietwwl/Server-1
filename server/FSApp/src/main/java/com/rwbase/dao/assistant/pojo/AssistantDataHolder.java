package com.rwbase.dao.assistant.pojo;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class AssistantDataHolder {

	final private eSynType roleAttrSynType = eSynType.ASSISTANT;

	private AssistantData assistantData;

	private Player player;

	public AssistantDataHolder(Player playerP) {
		player = playerP;
		assistantData = new AssistantData();
	}

	public AssistantData get() {
		return assistantData;
	}
	
	public void setAssistantEventID(AssistantEventID newEvent) {
		AssistantEventID oldEvent = assistantData.getAssistantEventID();
		if (oldEvent == newEvent) {
			return;
		}

		if (newEvent != AssistantEventID.Invaild || oldEvent != AssistantEventID.Invaild) {
			assistantData.setAssistantEventID(newEvent);
			ClientDataSynMgr.updateData(player, assistantData, roleAttrSynType, eSynOpType.UPDATE_SINGLE);
		}
	}

	public void synData() {
		if (assistantData != null && assistantData.getAssistantEventID() != null) {
			ClientDataSynMgr.updateData(player, assistantData, roleAttrSynType, eSynOpType.UPDATE_SINGLE);
		}
	}
	
}
