package com.gm.customer.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	private boolean isQuestioning = false;   //是否有问题没有回复 true 有问题待回复  false 所有问题都有回复或者没有问题
	private boolean blnInit = true;
	
	public QuestionListDataHolder(String userId){
		this.userId = userId;
	}
	
	public void syn(Player player, int version) {
		updateList(player, this.questionList);
	}
	
	public Map<Integer, QueryListResponse> getQuestionMap(){
		Map<Integer, QueryListResponse> map = new HashMap<Integer, QueryListResponse>();
		for (QueryListResponse queryListResponse : questionList) {
			map.put(queryListResponse.getId(), queryListResponse);
		}
		return map;
	}

	public void initQuestionList(Player player, List<QueryListResponse> questionList){
		for (Iterator iterator = questionList.iterator(); iterator.hasNext();) {
			QueryListResponse queryListResponse = (QueryListResponse) iterator.next();
			if(queryListResponse.getAccount() == null){
				iterator.remove();
			}
		}
		if (blnInit) {
			blnInit = false;
			this.questionList = questionList;
			updateList(player, this.questionList);
		} else {
			Map<Integer, QueryListResponse> questionMap = getQuestionMap();
			List<Integer> keys = new ArrayList<Integer>(questionMap.keySet());
			for (QueryListResponse queryListResponse : questionList) {
				int id = queryListResponse.getId();
				if (queryListResponse.getReply()== null) {
					isQuestioning = true;
				}
				if (!questionMap.containsKey(queryListResponse.getId())) {
					this.questionList.add(queryListResponse);
					add(player, queryListResponse);
				} else {
					keys.remove((Integer)id);
				}
			}
			for (Integer id : keys) {
				remove(player, questionMap.get(id));
			}
		}
	}
	
	public QueryListResponse getUnReplyFeedback(){
		for (QueryListResponse queryListResponse : questionList) {
			if(queryListResponse.getReply() == null){
				return queryListResponse;
			}
		}
		return null;
	}
	
	public void updateList(Player player, List<QueryListResponse> list){
		ClientDataSynMgr.updateDataList(player, this.questionList, synType, eSynOpType.UPDATE_LIST);
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

	public boolean isQuestioning() {
		return isQuestioning;
	}

	public boolean isBlnInit() {
		return blnInit;
	}

	public void setBlnInit(boolean blnInit) {
		this.blnInit = blnInit;
	}
}
