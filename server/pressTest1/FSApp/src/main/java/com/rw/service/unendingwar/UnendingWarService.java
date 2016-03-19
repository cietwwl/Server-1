package com.rw.service.unendingwar;



import com.google.protobuf.ByteString;


import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.OtherRoleAttrProtos.EMsgType;
import com.rwproto.OtherRoleAttrProtos.OtherRoleAttrRequest;
import com.rwproto.OtherRoleAttrProtos.OtherRoleAttrResponse;
import com.rwproto.RequestProtos.Request;
import com.rwproto.UnendingWarProtos.EUnendingWarType;
import com.rwproto.UnendingWarProtos.UnendingWarRequest;


public class UnendingWarService implements FsService {	
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			UnendingWarRequest req = UnendingWarRequest.parseFrom(request.getBody().getSerializedContent());
			EUnendingWarType requestType = req.getType();
			switch (requestType) {
			case BaseMsg:
				
				result=UnendingWarHandler.getInstance().getInfo(player);
				//player.delMsgMap(req.getId());
				break;
			case OtherMsg:
			
				result=UnendingWarHandler.getInstance().endMap(player,req.getNum());
				break;
			case AddMsg:
				
				result=UnendingWarHandler.getInstance().addNum(player);
				break;
	        case EndMsg:
				result=UnendingWarHandler.getInstance().end(player,req.getNum());
				break;
	        case ResetMsg:
				result=UnendingWarHandler.getInstance().ResetNum(player);
				break;
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
