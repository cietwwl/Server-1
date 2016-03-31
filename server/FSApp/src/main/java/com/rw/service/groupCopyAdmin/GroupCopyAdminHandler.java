package com.rw.service.groupCopyAdmin;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.GroupHelper;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminComReqMsg;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminComRspMsg;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminOpenCopyReqMsg;
import com.groupCopy.rwproto.GroupCopyAdminProto.RequestType;
import com.playerdata.Player;
import com.rwbase.dao.group.pojo.Group;

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

	public ByteString getInfo(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GET_COPY_INFO);	
	

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}
	
	public ByteString open(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.OPEN_COPY);

		GroupCopyAdminOpenCopyReqMsg openReqMsg = req.getOpenReqMsg();
		Group group = GroupHelper.getGroup(player);
		boolean success = false;
		if(group!=null){
			String mapId = openReqMsg.getMapId();
			success = group.getGroupCopyMgr().openMap( mapId );
		}	
		commonRsp.setIsSuccess(success);		
		return commonRsp.build().toByteString();
	}


	public ByteString reset(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.RESET_COPY);

		GroupCopyAdminOpenCopyReqMsg openReqMsg = req.getOpenReqMsg();
		Group group = GroupHelper.getGroup(player);
		boolean success = false;
		if(group!=null){
			String mapId = openReqMsg.getMapId();
			success = group.getGroupCopyMgr().resetMap( mapId );
		}	
		commonRsp.setIsSuccess(success);	
		return commonRsp.build().toByteString();
	}

	
}