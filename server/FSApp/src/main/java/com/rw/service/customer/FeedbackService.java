package com.rw.service.customer;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MsgDef.Command;
import com.rwproto.QuestionServiceProtos.MsgSubmitQuestionRequest;
import com.rwproto.RequestProtos.Request;

public class FeedbackService implements FsService<MsgSubmitQuestionRequest, Command>{

	@Override
	public ByteString doTask(MsgSubmitQuestionRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try{
			
			result = FeedbackHandler.SubmitFeedback(player, request);
		}catch(Exception ex){
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
	public Command getMsgType(MsgSubmitQuestionRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
