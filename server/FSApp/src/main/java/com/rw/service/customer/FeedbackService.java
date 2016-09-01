package com.rw.service.customer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.QuestionServiceProtos.MsgSubmitQuestionRequest;
import com.rwproto.QuestionServiceProtos.eFeedbackType;
import com.rwproto.RequestProtos.Request;

public class FeedbackService implements FsService<MsgSubmitQuestionRequest, eFeedbackType>{

	@Override
	public ByteString doTask(MsgSubmitQuestionRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			eFeedbackType requestType = request.getRequestType();
			switch (requestType) {
			case FEEDBACK:
				result = FeedbackHandler.SubmitFeedback(player, request);
				break;
			case REPORT:
				result = FeedbackHandler.SubmitReport(player, request);
				break;
			default:
				return null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	public MsgSubmitQuestionRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgSubmitQuestionRequest req = MsgSubmitQuestionRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public eFeedbackType getMsgType(MsgSubmitQuestionRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}

}
