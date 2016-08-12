package com.rwbase.dao.assistant.pojo;

import java.util.List;

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

	public void setAssistantEventID(List<AssistantEventID> newEvent) {
		List<AssistantEventID> oldEvent = assistantData.getAssistantEvent();
		if (newEvent.equals(oldEvent)) {
			return;
		}

		assistantData.setAssistantEvent(newEvent);
		ClientDataSynMgr.updateData(player, assistantData, roleAttrSynType, eSynOpType.UPDATE_SINGLE);
	}

	public void synData() {
		if (assistantData != null && assistantData.getAssistantEvent() != null) {
			ClientDataSynMgr.updateData(player, assistantData, roleAttrSynType, eSynOpType.UPDATE_SINGLE);
		}
	}

}
