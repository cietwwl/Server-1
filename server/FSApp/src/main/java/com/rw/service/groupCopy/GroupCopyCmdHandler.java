package com.rw.service.groupCopy;

import com.bm.groupCopy.GroupCopyDataVersion;
import com.bm.groupCopy.GroupCopyDataVersionMgr;
import com.bm.groupCopy.GroupCopyResult;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecordMgr;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdRspMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyHurtRank;
import com.rwproto.GroupCopyCmdProto.GroupCopyReqType;

public class GroupCopyCmdHandler {

	private static GroupCopyCmdHandler instance = new GroupCopyCmdHandler();
	
	
	public static GroupCopyCmdHandler getInstance() {
		return instance;
	}

	

	public ByteString getGroupCopyInfo(Player player, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.GET_INFO);
		
		Group group = com.rw.service.group.helper.GroupHelper.getGroup(player);
		if(group != null){
			rspCmd.setIsSuccess(true);
			GroupCopyDataVersionMgr.synAllDataByVersion(player, reqMsg.getVersion());
			
		}else{
			rspCmd.setIsSuccess(false);
		}
		return rspCmd.build().toByteString();
	}

	/**
	 * 同步单章节掉落数据到客户端
	 * @param player
	 * @param reqMsg
	 * @return
	 */
	public ByteString getDropApplyInfo(Player player, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.GET_DROP_APPLY_INFO);
		Group group = com.rw.service.group.helper.GroupHelper.getGroup(player);
		if(group != null){
			group.synGroupCopyDropApplyData(player, reqMsg.getId());
			rspCmd.setIsSuccess(true);
		}else{
			rspCmd.setIsSuccess(false);
		}
		return rspCmd.build().toByteString();
	}
	
	public ByteString getServerRankInfo(Player player, GroupCopyCmdReqMsg reqMsg){
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.APPLY_SERVER_RANK);
		Group group = com.rw.service.group.helper.GroupHelper.getGroup(player);
		if(group != null){
			GroupCopyDataVersion version = GroupCopyDataVersionMgr.fromJson(reqMsg.getVersion());
			String id = reqMsg.getId();
			ServerGroupCopyDamageRecordMgr.getInstance().synSingleData(player, version.getServerCopyDamageRankData(), id, true);
			rspCmd.setIsSuccess(true);
		}else{
			rspCmd.setIsSuccess(false);
		}
		
		
		return rspCmd.build().toByteString();
	}

	/**
	 * 赞助buff
	 * @param player
	 * @param reqMsg
	 * @return
	 */
	public ByteString donateBuff(Player player, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.BUFF_DONATE);
		Group g = com.rw.service.group.helper.GroupHelper.getGroup(player);
		if(g != null){
			GroupCopyResult suc = g.getGroupCopyMgr().donateBuff(player, g, reqMsg);
			rspCmd.setIsSuccess(suc.isSuccess());
			rspCmd.setTipMsg(suc.getTipMsg());
		}else{
			rspCmd.setIsSuccess(false);
			rspCmd.setTipMsg("角色不在帮派内");
		}
		
		return rspCmd.build().toByteString();
	}


	/**
	 * 获取帮派前10排行榜
	 * @param player
	 * @param reqMsg
	 * @return
	 */
	public ByteString getGroupDamageRank(Player player, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.BUFF_DONATE);
		Group g = com.rw.service.group.helper.GroupHelper.getGroup(player);
		if(g != null){
			GroupCopyResult rs = g.getGroupCopyMgr().getDamageRank(player, g, reqMsg);
			rspCmd.setIsSuccess(rs.isSuccess());
			rspCmd.setTipMsg(rs.getTipMsg());
			if(rs.isSuccess()){
				rspCmd.setHurtRank((GroupCopyHurtRank.Builder) rs.getItem());
			}
		}else{
			rspCmd.setIsSuccess(false);
			rspCmd.setTipMsg("角色不在帮派内");
		}
		return rspCmd.build().toByteString();
	}



	/**
	 * 取消申请战利品
	 * @param player
	 * @param reqMsg
	 * @return
	 */
	public ByteString cancelApplyItem(Player player, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.CANCEL_APPLY_ITEM);
		Group g = com.rw.service.group.helper.GroupHelper.getGroup(player);
		if(g != null){
			GroupCopyResult result = g.getGroupCopyMgr().ApplyOrCancelItem(player, reqMsg, false);
			rspCmd.setIsSuccess(result.isSuccess());
			if(!result.isSuccess()){
				rspCmd.setTipMsg(result.getTipMsg());
			}
		}else{
			rspCmd.setIsSuccess(false);
			rspCmd.setTipMsg("角色不在帮派内");
		}
		return rspCmd.build().toByteString();
	}



	/**
	 * 申请战利品
	 * @param player
	 * @param reqMsg
	 * @return
	 */
	public ByteString applyWarPrice(Player player, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.APPLY_WAR_PRICE);
		Group g = com.rw.service.group.helper.GroupHelper.getGroup(player);
		if(g != null){
			GroupCopyResult result = g.getGroupCopyMgr().ApplyOrCancelItem(player, reqMsg, true);
			rspCmd.setIsSuccess(result.isSuccess());
			if(!result.isSuccess()){
				rspCmd.setTipMsg(result.getTipMsg());
			}
		}else{
			rspCmd.setIsSuccess(false);
			rspCmd.setTipMsg("角色不在帮派内");
		}
		return rspCmd.build().toByteString();
	}



	/**
	 * 获取奖励分配记录
	 * @param player
	 * @return
	 */
	public ByteString getDistRewardLog(Player player) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.GET_DIST_REWARD_LOG);
		Group g = com.rw.service.group.helper.GroupHelper.getGroup(player);
		if(g != null){
			g.getGroupCopyMgr().synRewardLogData(player);
			rspCmd.setIsSuccess(true);
		}else{
			rspCmd.setIsSuccess(false);
			rspCmd.setTipMsg("角色不在帮派内");
		}
		return rspCmd.build().toByteString();
	}

}
