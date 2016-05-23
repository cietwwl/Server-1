package com.rw.service.inlay;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.eGMType;
import com.rwproto.InlayProtos.EInlayType;
import com.rwproto.InlayProtos.InlayResult;
import com.rwproto.InlayProtos.MsgInlayRequest;
import com.rwproto.InlayProtos.MsgInlayResponse;
import com.rwproto.MsgErrInfoProtos.EMsgErrorInfoType;
import com.rwproto.MsgErrInfoProtos.MsgErrorInfoRequest;
import com.rwproto.RequestProtos.Request;

public class InlayService implements FsService{


	
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			
			MsgInlayRequest msgRequest = MsgInlayRequest.parseFrom(request.getBody().getSerializedContent());
			EInlayType type = msgRequest.getType();
			switch (type) {
			case Inlay_One:
				result=InlayHandler.getInstance().InlayOne(player,msgRequest);
				
				break;
				
	         case Inlay_All:
	        		result=InlayHandler.getInstance().InlayAll(player,msgRequest);
				break;
				
	         case XieXia_All:
	        	 result=InlayHandler.getInstance().XieXiaAll(player,msgRequest);
					break;

			default:
				break;
			}
			if (result == null){
				MsgInlayResponse.Builder res = MsgInlayResponse.newBuilder();
				res.setType(msgRequest.getType());
				res.setResult(InlayResult.InlayFailed);
				result = res.build().toByteString();
			}
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
