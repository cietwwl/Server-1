package com.gm.customer.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gm.customer.response.QueryListResponse;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class QuestionListDataHolder {
	private static eSynType synType = eSynType.QuestionList;
	private final String userId;
	private List<QueryListResponse> questionList = new ArrayList<QueryListResponse>();
	
	public QuestionListDataHolder(String userId){
		this.userId = userId;
	}
	
	public void syn(Player player, int version) {
		Map<Integer, QueryListResponse> questionMap = getQuestionMap();
		if (questionMap != null) {
			ClientDataSynMgr.updateData(player, questionMap, synType, eSynOpType.UPDATE_SINGLE);
		}
	}
	
	public Map<Integer, QueryListResponse> getQuestionMap(){
		Map<Integer, QueryListResponse> map = new HashMap<Integer, QueryListResponse>();
		for (QueryListResponse queryListResponse : questionList) {
			map.put(queryListResponse.getId(), queryListResponse);
		}
		return map;
	}
	
	public void initQuestionList(List<QueryListResponse> questionList){
		this.questionList = questionList;
	}
	
	public void update(Player player, QueryListResponse response){
		ClientDataSynMgr.updateData(player, response, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void add(Player player, QueryListResponse response){
		ClientDataSynMgr.updateData(player, response, synType, eSynOpType.ADD_SINGLE);
	}
	
	public void remove(Player player, QueryListResponse response){
		ClientDataSynMgr.updateData(player, response, synType, eSynOpType.REMOVE_SINGLE);
	}
}
