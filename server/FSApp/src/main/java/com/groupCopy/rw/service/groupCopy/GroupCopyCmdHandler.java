package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.GroupHelper;
import com.groupCopy.bm.groupCopy.GroupCopyDataVersion;
import com.groupCopy.bm.groupCopy.GroupCopyDataVersionMgr;
import com.groupCopy.bm.groupCopy.GroupCopyMgr;
import com.groupCopy.bm.groupCopy.GroupCopyResult;
import com.groupCopy.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecordMgr;
import com.playerdata.Player;
import com.rwbase.dao.group.pojo.Group;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdRspMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyHurtRank;
import com.rwproto.GroupCopyCmdProto.GroupCopyReqType;

public class GroupCopyCmdHandler {

	private final static GroupCopyCmdHandler instance = new GroupCopyCmdHandler();
	
	
	public static GroupCopyCmdHandler getInstance() {
		return instance;
	}

	

	public ByteString getGroupCopyInfo(Player player, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.GET_INFO);
		
		Group group = GroupHelper.getGroup(player);
		if(group != null){
			rspCmd.setIsSuccess(true);
			GroupCopyDataVersionMgr.synAllDataByVersion(player, reqMsg.getVersion());
			
		}else{
			rspCmd.setIsSuccess(false);
		}
		return rspCmd.build().toByteString();
	}


	public ByteString getDropApplyInfo(Player player, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.GET_INFO);
		Group group = GroupHelper.getGroup(player);
		if(group != null){
			GroupCopyDataVersionMgr.syncSingleDataByVersion(player, reqMsg.getVersion(),
					GroupCopyDataVersionMgr.TYPE_DROP_APPLY);
			rspCmd.setIsSuccess(true);
		}else{
			rspCmd.setIsSuccess(false);
		}
		
		
		return rspCmd.build().toByteString();
	}
	public ByteString getServerRankInfo(Player player, GroupCopyCmdReqMsg reqMsg){
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.APPLY_SERVER_RANK);
		Group group = GroupHelper.getGroup(player);
		if(group != null){
			GroupCopyDataVersion version = GroupCopyDataVersionMgr.fromJson(reqMsg.getVersion());
			String id = reqMsg.getChaterID();
			ServerGroupCopyDamageRecordMgr.getInstance().synSingleData(player, version.getServerCopyDamageRankData(), id);
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
		Group g = GroupHelper.getGroup(player);
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
		Group g = GroupHelper.getGroup(player);
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

}
