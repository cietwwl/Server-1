package com.rw.service.role;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RoleServiceProtos.RoleRequest;
import com.rwproto.RoleServiceProtos.RoleRequestType;

public class RoleService implements FsService {

	private RoleHandler handler = RoleHandler.getInstance();

	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			RoleRequest req = RoleRequest.parseFrom(request.getBody().getSerializedContent());
			RoleRequestType requestType = req.getRequestType();
			switch (requestType) {
			//case CREATE_ROLE:
			//	result = handler.createRole(req, player);
			//	break;
			case SELECT_CAREER:
				result = handler.selectCareer(req, player);
				break;
			case CHANGE_ROLE:
				result = handler.changeInfo(req, player);
				break;
			case CAREER_ADVANCE:
				result = handler.careerAdvance(req,player);
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return result;
	}

}
