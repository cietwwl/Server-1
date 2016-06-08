package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.GroupHelper;
import com.groupCopy.bm.groupCopy.GroupCopyResult;
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


	
	/**
	 * 开启副本地图
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString open(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.OPEN_COPY);

		GroupCopyAdminOpenCopyReqMsg openReqMsg = req.getOpenReqMsg();
		Group group = GroupHelper.getGroup(player);
		boolean success = false;
		
		if(group!=null){
			String mapId = openReqMsg.getMapId();
			GroupCopyResult openResult = group.getGroupCopyMgr().openMap( mapId );
			success = openResult.isSuccess();
			commonRsp.setTipMsg(commonRsp.getTipMsg());
		}	
		commonRsp.setIsSuccess(success);		
		return commonRsp.build().toByteString();
	}


	/**
	 * 重置副本地图
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString reset(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.RESET_COPY);

		GroupCopyAdminOpenCopyReqMsg openReqMsg = req.getOpenReqMsg();
		Group group = GroupHelper.getGroup(player);
		boolean success = false;
		if(group!=null){
			String mapId = openReqMsg.getMapId();
			GroupCopyResult openResult = group.getGroupCopyMgr().resetMap( mapId );
			success = openResult.isSuccess();
			commonRsp.setTipMsg(commonRsp.getTipMsg());
		}	
		commonRsp.setIsSuccess(success);	
		return commonRsp.build().toByteString();
	}

	
}