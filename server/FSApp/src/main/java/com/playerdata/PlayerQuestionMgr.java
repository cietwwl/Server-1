package com.playerdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.gm.customer.PlayerQuestionService;
import com.gm.customer.QuestionOpType;
import com.gm.customer.QuestionReply;
import com.gm.customer.QuestionTips;
import com.gm.customer.QuestionType;
import com.gm.customer.data.QuestionListDataHolder;
import com.gm.customer.response.QueryListResponse;
import com.gm.customer.response.QuestionSubmitResponse;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;
import com.rw.manager.ServerSwitch;
import com.rw.service.customer.FeedbackResult;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserExtendInfo;
import com.rwproto.QuestionServiceProtos.eSubmitResultType;

public class PlayerQuestionMgr implements PlayerEventListener{
	private QuestionListDataHolder questionListDataHolder;
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
	
	public FeedbackResult submitQuestion(QuestionType type, String phone, String model, String channel, String feedbackContent){
		
		FeedbackResult result = new FeedbackResult(); 
		if(!ServerSwitch.isGiftCodeOpen()){
			result.setResultType(eSubmitResultType.FAIL);
			result.setResult(QuestionTips.FEEDBACK_SERVICE_CLOSE_TIPS);
			return result;
		}
		
		
		if(questionListDataHolder.isQuestioning()){
			result.setResultType(eSubmitResultType.FAIL);
			result.setResult(QuestionTips.UNANSWER_TIPS);
			return result;
		}
		String userId = mPlayer.getUserId();
		UserDataMgr userDataMgr = mPlayer.getUserDataMgr();
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("channel", channel);
		content.put("type", type.getType());
		content.put("serverId", GameManager.getZoneId());
		content.put("roleId", userId);
		content.put("account", userDataMgr.getAccount());
		content.put("roleName", mPlayer.getUserName());
		content.put("feedbackTime", System.currentTimeMillis()/1000);
		content.put("feedbackContent", feedbackContent);
		content.put("phone", phone);
		content.put("model", model);
		content.put("iSequenceNum", ServerStatusMgr.getiSequenceNum());
		if(!type.getAutoAnswer().equals("")){
			QuestionReply reply = new QuestionReply();
			String replyTime = DateUtils.getDateTimeFormatString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm");
			reply.setReplyTime(replyTime);
			reply.setContent(type.getAutoAnswer());
			reply.setRoleId(userId);
			reply.setServerId(GameManager.getZoneId());
			result.setResult(type.getAutoAnswer());
		}else{
			result.setResult(QuestionTips.DEFAULT_TIPS);
		}
		
		PlayerQuestionService.getInstance().submitRequest(content, QuestionOpType.Question_Submit.getOpType(), userId, false, QuestionSubmitResponse.class);
		result.setResultType(eSubmitResultType.SUCCESS);
		return result;
	}
	
	
	public void processResponse(List objs, int opType){
		QuestionOpType questionOpType = QuestionOpType.getByOpType(opType);
		switch (questionOpType) {
		case Question_Query:
			initQuestionList(objs);
			break;
		case Question_Submit:
			if (objs.size() > 0) {
				processSubmitQuestionResult(objs.get(0));
			}
			break;
		default:
			break;
		}
	}
	
	public void processSubmitQuestionResult(Object obj) {
		QuestionSubmitResponse response = (QuestionSubmitResponse) obj;

		queryRequestList();
	}
	
	public void processNotifyReply(QuestionReply reply){
		QueryListResponse unReplyFeedback = questionListDataHolder.getUnReplyFeedback();
		unReplyFeedback.setReply(reply);
		questionListDataHolder.update(mPlayer, unReplyFeedback);
		UserDataMgr userDataMgr = mPlayer.getUserDataMgr();
		User user = userDataMgr.getUser();
		UserExtendInfo extendInfo = user.getExtendInfo();
		extendInfo.setFeedbackId(unReplyFeedback.getId());
	}
	
	@SuppressWarnings("unchecked")
	public void initQuestionList(List objs){
		questionListDataHolder.initQuestionList(mPlayer, (List<QueryListResponse>)objs);
	}
	
	
	public void sync(int version){
		
		questionListDataHolder.syn(mPlayer, version);
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		// TODO Auto-generated method stub
		questionListDataHolder.setBlnInit(true);
		queryRequestList();
	}
	
}
