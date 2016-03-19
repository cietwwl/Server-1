package com.rw.service.numericAnalysis;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MsgDef.Command;
import com.rwproto.NumericAnalysisProtos.NumericAnalysisRequest;
import com.rwproto.RequestProtos.Request;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;

public class NumericAnalysisService{

	public static Response doTask(Request request) {
		// TODO Auto-generated method stub
		Response result = null;
		try {
			NumericAnalysisRequest numericAnalysisRequest = NumericAnalysisRequest.parseFrom(request.getBody().getSerializedContent());
			Response.Builder builder = Response.newBuilder().setHeader(getSimpleResponseHeader(request, 
					Command.MSG_NUMERIC_ANALYSIS));
			ByteString resultContent = NumericAnalysisHandler.getArmyInfo(numericAnalysisRequest);
			if (resultContent != null) {
				builder.setSerializedContent(resultContent);
			}
			result = builder.build();
		} catch (InvalidProtocolBufferException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	
	private static ResponseHeader getSimpleResponseHeader(Request req, Command command) {
		String token = req.getHeader().getToken();
		return ResponseHeader.newBuilder().setToken(token).setCommand(command).setStatusCode(200).build();
	}
	
}
