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
import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.GameManager;
import com.rw.manager.ServerSwitch;
import com.rw.service.customer.FeedbackResult;
import com.rw.service.log.infoPojo.ZoneRegInfo;
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
		content.put("account", channel + "_" + userDataMgr.getAccount());
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
		//同步最新反馈列表给客户端
		questionListDataHolder.syn(mPlayer, -1);
		return result;
	}
	
	/**
	 * 
	 * @param player
	 * @param targetUserId
	 * @param feedBackContent
	 * @param reportChannel	举报频道 1:世界 2:帮派 3:附近 4:私聊
	 * @param channel	
	 * @param type 1 聊天举报 	2外挂举报
	 * @return
	 */
	public String reportOtherPlayer(Player player, String targetUserId, String content, int reportChannel, String channel, int type){
		if(!ServerSwitch.isGiftCodeOpen()){
			return QuestionTips.FEEDBACK_SERVICE_CLOSE_TIPS;
		}
		if(player.getUserId() == targetUserId){
			return QuestionTips.CAN_NOT_REPORT_SELF;
		}
		
		if(System.currentTimeMillis() - questionListDataHolder.getReportPlayerTime() < 10*1000){
			return QuestionTips.REPORT_TOO_MUCH;
		}
		questionListDataHolder.setReportPlayerTime(System.currentTimeMillis());
		String userId = player.getUserId();
		Player targetPlayer = PlayerMgr.getInstance().find(targetUserId);
		UserDataMgr userDataMgr = targetPlayer.getUserDataMgr();
		ZoneRegInfo zoneRegInfo = userDataMgr.getZoneRegInfo();
		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("channel", channel);
		contentMap.put("type", type);
		contentMap.put("reportChannel", reportChannel);
		contentMap.put("byReportAccount", zoneRegInfo.getRegChannelId() + "_" + userDataMgr.getAccount());
		contentMap.put("byReportId", targetUserId);
		contentMap.put("byReportName", targetPlayer.getUserName());
		contentMap.put("byReportVip", targetPlayer.getVip());
		contentMap.put("byReportPhone", "");
		contentMap.put("byReportRoleLev", targetPlayer.getLevel());
		contentMap.put("reportId", userId);
		contentMap.put("reportName", player.getUserName());
		contentMap.put("reportVip", player.getVip());
		contentMap.put("reportTime", System.currentTimeMillis()/1000);
		contentMap.put("reportContent", content);
		contentMap.put("iSequenceNum", ServerStatusMgr.getiSequenceNum());
		
		PlayerQuestionService.getInstance().submitRequest(contentMap, 25002, userId, false, QuestionSubmitResponse.class);
		
		return QuestionTips.REPORT_SUCCESS;
//		return getReportResponseMsg(response);
	}
	
	private String getReportResponseMsg(QuestionSubmitResponse response){
		if(response ==null){
			return QuestionTips.REPORT_FAIL;
		}
		
		switch (response.getType()) {
		case 0:
			return QuestionTips.REPORT_SUCCESS;
		case 1:
			return QuestionTips.REPORT_OVERTIME;
		case 2:
			return QuestionTips.REPORT_ALREADY;
		default:
			return QuestionTips.REPORT_FAIL;
		}
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
		case Feedback_Player:
			String reportResponseMsg = getReportResponseMsg((QuestionSubmitResponse)objs.get(0));
			GameLog.error("report player", this.mPlayer.getUserId(), reportResponseMsg);
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
