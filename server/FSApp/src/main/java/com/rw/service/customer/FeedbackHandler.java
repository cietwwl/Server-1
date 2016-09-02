package com.rw.service.customer;

import com.gm.customer.QuestionType;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerQuestionMgr;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserExtendInfo;
import com.rwproto.PrivilegeProtos.LoginPrivilegeNames;
import com.rwproto.QuestionServiceProtos.MsgReportReponse;
import com.rwproto.QuestionServiceProtos.MsgReportRequest;
import com.rwproto.QuestionServiceProtos.MsgSubmitQuestionRequest;
import com.rwproto.QuestionServiceProtos.MsgSubmitQuestionResponse;
import com.rwproto.QuestionServiceProtos.eFeedbackType;

public class FeedbackHandler {
	/**
	 * 游戏反馈
	 * @param player
	 * @param request
	 * @return
	 */
	public static ByteString SubmitFeedback(Player player, MsgSubmitQuestionRequest request){
		MsgSubmitQuestionResponse.Builder resp = MsgSubmitQuestionResponse.newBuilder();
		int type = request.getType();
		QuestionType questionType = QuestionType.getQuestionType(type);
		resp.setRequestType(eFeedbackType.FEEDBACK);
		resp.setType(type);
		if (questionType == QuestionType.Q_READFEEDBACK) {
			UpdateFeedbackReadStatus(player);
		} else {
			String phone = request.getPhone();
			String model = request.getModel();
			String channel = request.getChannel();
			String feedbackContent = request.getFeedbackContent();
			PlayerQuestionMgr playerQuestionMgr = player.getPlayerQuestionMgr();
			FeedbackResult result = playerQuestionMgr.submitQuestion(questionType, phone, model, channel, feedbackContent);
			resp.setResult(result.getResultType());
			resp.setResponseResult(result.getResult());
		}
		return resp.build().toByteString();
	}
	
	public static void UpdateFeedbackReadStatus(Player player){
		User user = player.getUserDataMgr().getUser();
		UserExtendInfo extendInfo = user.getExtendInfo();
		extendInfo.setFeedbackId(0);
	}
	
	/**
	 * 举报玩家
	 * @param player
	 * @param request
	 * @return
	 */
	public static ByteString SubmitReport(Player player, MsgSubmitQuestionRequest request) {

		// 需要vip权限判斷
		boolean isAllow = player.getPrivilegeMgr().getBoolPrivilege(LoginPrivilegeNames.isAllowReport);
		String result;
		if (!isAllow) {
			result = "VIP2开启举报玩家功能，充值可提升VIP等级。";
		} else {

			MsgReportRequest reportRequest = request.getReportRequest();
			String targetUserId = reportRequest.getUserId();
			String chatContent = reportRequest.getChatContent();
			String channel = reportRequest.getChannel();
			int reportChannel = reportRequest.getReportChannel();
			result = player.getPlayerQuestionMgr().reportOtherPlayer(player, targetUserId, chatContent, reportChannel, channel, 1);
		}
		MsgSubmitQuestionResponse.Builder resp = MsgSubmitQuestionResponse.newBuilder();
		MsgReportReponse.Builder reportResp = MsgReportReponse.newBuilder();
		reportResp.setResult(result);
		resp.setRequestType(eFeedbackType.REPORT);
		resp.setReportResponse(reportResp);
		return resp.build().toByteString();
	}
}
