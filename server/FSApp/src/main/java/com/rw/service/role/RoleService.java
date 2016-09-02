package com.rw.service.role;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RoleServiceProtos.RoleRequest;
import com.rwproto.RoleServiceProtos.RoleRequestType;

public class RoleService implements FsService<RoleRequest, RoleRequestType> {

	private RoleHandler handler = RoleHandler.getInstance();
	
	@Override
	public ByteString doTask(RoleRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			RoleRequestType requestType = request.getRequestType();
			switch (requestType) {
			case SELECT_CAREER:
				result = handler.selectCareer(request, player);
				break;
			case CHANGE_ROLE:
				result = handler.changeInfo(request, player);
				break;
			case CAREER_ADVANCE:
				result = handler.careerAdvance(request, player);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public RoleRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		RoleRequest req = RoleRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public RoleRequestType getMsgType(RoleRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}

}
