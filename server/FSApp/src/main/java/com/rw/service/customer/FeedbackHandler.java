package com.rw.service.customer;

import com.gm.customer.QuestionType;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerQuestionMgr;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserExtendInfo;
import com.rwproto.QuestionServiceProtos.MsgSubmitQuestionRequest;
import com.rwproto.QuestionServiceProtos.MsgSubmitQuestionResponse;

public class FeedbackHandler {
	public static ByteString SubmitFeedback(Player player, MsgSubmitQuestionRequest request){
		MsgSubmitQuestionResponse.Builder resp = MsgSubmitQuestionResponse.newBuilder();
		int type = request.getType();
		QuestionType questionType = QuestionType.getQuestionType(type);
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
}
