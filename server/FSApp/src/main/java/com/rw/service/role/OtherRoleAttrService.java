package com.rw.service.role;



import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.OtherRoleAttrProtos.EMsgType;
import com.rwproto.OtherRoleAttrProtos.OtherRoleAttrRequest;
import com.rwproto.RequestProtos.Request;


public class OtherRoleAttrService implements FsService {	
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			OtherRoleAttrRequest req = OtherRoleAttrRequest.parseFrom(request.getBody().getSerializedContent());
			EMsgType requestType = req.getType();
			switch (requestType) {
			case BaseMsg:
				result = OtherRoleHandler.getInstance().getRoleInfo(req.getUserId(),player);
				
				//player.delMsgMap(req.getId());
				break;
			case OtherMsg:
				//result = handler.selectCareer(req, player);
				//
				//res.setType(EMsgType.ClientMsg);
				//res.setId(req.getId());
				//result = res.build().toByteString();
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
