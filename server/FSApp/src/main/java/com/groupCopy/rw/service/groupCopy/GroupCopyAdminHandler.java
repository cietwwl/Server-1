package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.GroupHelper;
import com.groupCopy.bm.groupCopy.GroupCopyResult;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminComReqMsg;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminComRspMsg;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminOpenCopyReqMsg;
import com.groupCopy.rwproto.GroupCopyAdminProto.RequestType;
import com.playerdata.Player;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.db.GroupBaseData;
import com.rwbase.dao.group.pojo.db.dao.GroupBaseDataHolder;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GroupCommonProto.GroupPost;

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
		GroupCopyResult openResult;
		String mapId = openReqMsg.getMapId();
		
		if(group!=null){
			//检查是不是管理员
			GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(player.getUserId(), false);
			if(memberData.getPost() != GroupPost.LEADER.getNumber() && memberData.getPost() != GroupPost.ASSISTANT_LEADER.getNumber() ){
				commonRsp.setTipMsg("你不是帮派管理员，无法进行此操作！");
				commonRsp.setIsSuccess(success);		
				return commonRsp.build().toByteString();
			}
			
			int supplies = group.getGroupBaseDataMgr().getGroupData().getSupplies();
			GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapId);
			//测试 暂时去了这个
//			if(cfg.getOpenCost() > supplies){
//				commonRsp.setTipMsg("帮派物资不足");
//			}else{
				openResult = group.getGroupCopyMgr().openMap( mapId );
				success = openResult.isSuccess();
				commonRsp.setTipMsg(commonRsp.getTipMsg());
				if(success){
					//TODO 扣除帮派物资
				}
//			}
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