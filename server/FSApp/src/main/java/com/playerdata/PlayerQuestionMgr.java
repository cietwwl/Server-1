package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.gm.customer.PlayerQuestionService;
import com.gm.customer.QuestionOpType;
import com.gm.customer.data.QuestionListDataHolder;
import com.gm.customer.response.QueryListResponse;
import com.rw.manager.GameManager;

public class PlayerQuestionMgr {
	private QuestionListDataHolder questionListDataHolder;
	private boolean isNoQuestion = false;   //是否有问题没有回复 true 有问题待回复  false 所有问题都有回复或者没有问题
	private Player mPlayer;
	
	public void init(Player _Player){
		this.mPlayer = _Player;
		questionListDataHolder = new QuestionListDataHolder(_Player.getUserId());
	}
	
	public void queryRequestList(){
		String roleId = this.mPlayer.getUserId();
		String serverId = GameManager.getServerId();
		long iSequenceNum = ServerStatusMgr.getiSequenceNum();
		
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("roleId", roleId);
		content.put("serverId", serverId);
		content.put("iSequenceNum", iSequenceNum);
		
		PlayerQuestionService.getInstance().submitRequest(content, QuestionOpType.Question_Query.getOpType(), roleId, true, QueryListResponse.class);
	}
	
	public void submitQuestion(){
		
	}
	
	public void processResponse(Object obj, int opType){
		QuestionOpType questionOpType = QuestionOpType.getByOpType(opType);
		switch (questionOpType) {
		case Question_Query:
			break;
		case Question_Submit:
			break;
		default:
			break;
		}
	}
	
	
	public void processResponse(List objs, int opType){
		QuestionOpType questionOpType = QuestionOpType.getByOpType(opType);
		switch (questionOpType) {
		case Question_Query:
			initQuestionList(objs);
			break;
		case Question_Submit:
			break;
		default:
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void initQuestionList(List objs){
		questionListDataHolder.initQuestionList((List<QueryListResponse>)objs);
	}
	
	
	public void sync(int version){
		questionListDataHolder.syn(mPlayer, version);
	}
	
}
