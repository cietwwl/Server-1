package com.rw.service.role;



import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MainMsgProtos.EMsgType;
import com.rwproto.MainMsgProtos.MainMsgRequest;
import com.rwproto.RequestProtos.Request;


public class MainMsgService implements FsService {

	
	
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MainMsgRequest req = MainMsgRequest.parseFrom(request.getBody().getSerializedContent());
			
			EMsgType requestType = req.getType();
			switch (requestType) {
			case PmdMsg:
		
				break;
			case OtherMsg:
			
		
			default:
				break;
			}
			
			//System.out.println(""+req.getId());
		
	
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return result;
	}

}
