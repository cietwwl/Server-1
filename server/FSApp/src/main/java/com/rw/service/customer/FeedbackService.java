package com.rw.service.customer;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.QuestionServiceProtos.MsgSubmitQuestionRequest;
import com.rwproto.QuestionServiceProtos.eFeedbackType;
import com.rwproto.RequestProtos.Request;

public class FeedbackService implements FsService{

	@Override
	public ByteString doTask(Request request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			MsgSubmitQuestionRequest req = MsgSubmitQuestionRequest.parseFrom(request.getBody().getSerializedContent());
			eFeedbackType requestType = req.getRequestType();
			switch (requestType) {
			case FEEDBACK:
				result = FeedbackHandler.SubmitFeedback(player, req);
				break;
			case REPORT:
				result = FeedbackHandler.SubmitReport(player, req);
				break;
			default:
				return null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
