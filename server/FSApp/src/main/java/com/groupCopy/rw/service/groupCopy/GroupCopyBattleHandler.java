package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.GroupHelper;
import com.groupCopy.bm.groupCopy.GroupCopyResult;
import com.rwproto.GroupCopyBattleProto;
import com.rwproto.GroupCopyBattleProto.CopyRewardInfo;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComReqMsg;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComRspMsg;
import com.rwproto.GroupCopyBattleProto.GroupCopyMonsterData;
import com.rwproto.GroupCopyBattleProto.RequestType;
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

	
	/**
	 * TODO 协议有改动 详见 {@link GroupCopyBattleProto};
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString beginFight(Player player, GroupCopyBattleComReqMsg req) {
		GroupCopyBattleComRspMsg.Builder commonRsp = GroupCopyBattleComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.FIGHT_BEGIN);	
	
		Group group = GroupHelper.getGroup(player);
		boolean success = false;
		if(group!=null){
			GroupCopyResult result = group.getGroupCopyMgr().beginFight(player, req.getLevel());
			success = result.isSuccess();
			commonRsp.setTipMsg(result.getTipMsg());
		}	

		commonRsp.setIsSuccess(success);
		return commonRsp.build().toByteString();
	}
	
	public ByteString endFight(Player player, GroupCopyBattleComReqMsg req) {
		GroupCopyBattleComRspMsg.Builder commonRsp = GroupCopyBattleComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.FIGHT_END);
		String levelID = req.getLevel();
		GroupCopyMonsterData mData = req.getMData();

		Group group = GroupHelper.getGroup(player);
		boolean success = false;
		if(group!=null){
			GroupCopyResult result = group.getGroupCopyMgr().endFight(player, levelID, mData.getMonsterDataList(), null);
			success = result.isSuccess();
			commonRsp.setTipMsg(result.getTipMsg());
			commonRsp.setDropInfo((CopyRewardInfo) result.getItem());
		}	
		commonRsp.setIsSuccess(success);		
		return commonRsp.build().toByteString();
	}
	


	
}