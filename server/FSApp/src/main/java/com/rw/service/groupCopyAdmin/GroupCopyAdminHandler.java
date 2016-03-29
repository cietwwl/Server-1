package com.rw.service.groupCopyAdmin;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminComReqMsg;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminComRspMsg;
import com.rwproto.GroupCopyAdminProto.RequestType;

/*
 * @author HC
 * @date 2016年2月18日 下午3:16:30
 * @Description 帮派的基础处理
 */
public class GroupCopyAdminHandler {
	private static GroupCopyAdminHandler instance;

	public static GroupCopyAdminHandler getInstance() {
		if (instance == null) {
			instance = new GroupCopyAdminHandler();
		}
		return instance;
	}

	private GroupCopyAdminHandler() {
	}


	public ByteString open(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.OPEN_COPY);

		commonRsp.setIsSuccess(true);		
		return commonRsp.build().toByteString();
	}


	public ByteString reset(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.RESET_COPY);

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	public ByteString getInfo(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GET_COPY_INFO);

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}
	
}