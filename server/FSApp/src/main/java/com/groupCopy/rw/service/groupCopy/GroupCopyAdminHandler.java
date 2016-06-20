package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.GroupHelper;
import com.groupCopy.bm.groupCopy.GroupCopyResult;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminComReqMsg;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminComRspMsg;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminOpenCopyReqMsg;
import com.groupCopy.rwproto.GroupCopyAdminProto.GroupCopyAdminResetCopyReqMsg;
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
//			group.getGroupBaseDataMgr().setGroupSupplier(100000);
//			group.getGroupBaseDataMgr().updateAndSynGroupData(player);
			int supplies = group.getGroupBaseDataMgr().getGroupData().getSupplies();
			GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapId);
			
			if(cfg.getOpenCost() > supplies){
				commonRsp.setTipMsg("帮派物资不足");
			}else{
				openResult = group.getGroupCopyMgr().openMap(player, mapId );
				success = openResult.isSuccess();
				if(success){
					// 扣除帮派物资
					supplies -= cfg.getOpenCost();
					group.getGroupBaseDataMgr().setGroupSupplier(supplies);
					group.getGroupBaseDataMgr().updateAndSynGroupData(player);
				}else{
					commonRsp.setTipMsg("开启失败");
				}
			}
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

		GroupCopyAdminResetCopyReqMsg openReqMsg = req.getResetReqMsg();
		Group group = GroupHelper.getGroup(player);
		boolean success = false;
		if(group!=null){
			String mapId = openReqMsg.getMapId();
			int supplies = group.getGroupBaseDataMgr().getGroupData().getSupplies();
			GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapId);
			if(cfg == null){
				commonRsp.setIsSuccess(success);	
				commonRsp.setTipMsg("数据异常~");
				return commonRsp.build().toByteString();
			}
			
			if(cfg.getOpenCost() > supplies){
				commonRsp.setTipMsg("帮派物资不足");
			}else{
				GroupCopyResult openResult = group.getGroupCopyMgr().resetMap( player, mapId );
				success = openResult.isSuccess();
				if(success){
					// 扣除帮派物资
					supplies -= cfg.getOpenCost();
					group.getGroupBaseDataMgr().setGroupSupplier(supplies);
					group.getGroupBaseDataMgr().updateAndSynGroupData(player);
					commonRsp.setTipMsg("重置成功");
				}else{
					commonRsp.setTipMsg("开启失败");
				}
				
			}
			
		}	
		commonRsp.setIsSuccess(success);	
		return commonRsp.build().toByteString();
	}

	
}