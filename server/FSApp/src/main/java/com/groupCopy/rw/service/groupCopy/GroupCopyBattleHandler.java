package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.GroupHelper;
import com.groupCopy.bm.groupCopy.GroupCopyResult;
import com.groupCopy.rwproto.GroupCopyBattleProto.GroupCopyBattleBeginReqMsg;
import com.groupCopy.rwproto.GroupCopyBattleProto.GroupCopyBattleComReqMsg;
import com.groupCopy.rwproto.GroupCopyBattleProto.GroupCopyBattleComRspMsg;
import com.groupCopy.rwproto.GroupCopyBattleProto.GroupCopyBattleEndReqMsg;
import com.groupCopy.rwproto.GroupCopyBattleProto.RequestType;
import com.playerdata.Player;
import com.rwbase.dao.group.pojo.Group;

/*
 * @author HC
 * @date 2016年2月18日 下午3:16:30
 * @Description 帮派的基础处理
 */
public class GroupCopyBattleHandler {
	private static GroupCopyBattleHandler instance;

	public static GroupCopyBattleHandler getInstance() {
		if (instance == null) {
			instance = new GroupCopyBattleHandler();
		}
		return instance;
	}

	private GroupCopyBattleHandler() {}

	public ByteString beginFight(Player player, GroupCopyBattleComReqMsg req) {
		GroupCopyBattleComRspMsg.Builder commonRsp = GroupCopyBattleComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.FIGHT_BEGIN);	
		GroupCopyBattleBeginReqMsg beginReq = req.getBegin();
	
		Group group = GroupHelper.getGroup(player);
		boolean success = false;
		if(group!=null){
			GroupCopyResult result = group.getGroupCopyMgr().beginFight(player, beginReq.getLevel());
			success = result.isSuccess();
			commonRsp.setTipMsg(result.getTipMsg());
		}	

		commonRsp.setIsSuccess(success);
		return commonRsp.build().toByteString();
	}
	
	public ByteString endFight(Player player, GroupCopyBattleComReqMsg req) {
		GroupCopyBattleComRspMsg.Builder commonRsp = GroupCopyBattleComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.FIGHT_END);

		GroupCopyBattleEndReqMsg endReq = req.getEnd();
		Group group = GroupHelper.getGroup(player);
		boolean success = false;
		if(group!=null){
			GroupCopyResult result = group.getGroupCopyMgr().endFight(player, endReq.getLevel());
			success = result.isSuccess();
			commonRsp.setTipMsg(result.getTipMsg());
		}	
		commonRsp.setIsSuccess(success);		
		return commonRsp.build().toByteString();
	}


	
}