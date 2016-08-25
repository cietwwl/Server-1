package com.rw.service.role;



import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MainMsgProtos.EMsgType;
import com.rwproto.MainMsgProtos.MainMsgRequest;
import com.rwproto.RequestProtos.Request;


public class MainMsgService implements FsService<MainMsgRequest, EMsgType> {

	@Override
	public ByteString doTask(MainMsgRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			
			EMsgType requestType = request.getType();
			switch (requestType) {
			case PmdMsg:
		
				break;
			case OtherMsg:
			
		
			default:
				break;
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public MainMsgRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MainMsgRequest req = MainMsgRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public EMsgType getMsgType(MainMsgRequest request) {
		// TODO Auto-generated method stub
		return request.getType();
	}

}
