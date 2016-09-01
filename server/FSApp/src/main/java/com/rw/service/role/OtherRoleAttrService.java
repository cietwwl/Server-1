package com.rw.service.role;



import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.OtherRoleAttrProtos.EMsgType;
import com.rwproto.OtherRoleAttrProtos.OtherRoleAttrRequest;
import com.rwproto.RequestProtos.Request;


public class OtherRoleAttrService implements FsService<OtherRoleAttrRequest, EMsgType> {	

	@Override
	public ByteString doTask(OtherRoleAttrRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			EMsgType requestType = request.getType();
			switch (requestType) {
			case BaseMsg:
				result = OtherRoleHandler.getInstance().getRoleInfo(request.getUserId(),player);
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
	public OtherRoleAttrRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		OtherRoleAttrRequest req = OtherRoleAttrRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public EMsgType getMsgType(OtherRoleAttrRequest request) {
		// TODO Auto-generated method stub
		return request.getType();
	}

}
