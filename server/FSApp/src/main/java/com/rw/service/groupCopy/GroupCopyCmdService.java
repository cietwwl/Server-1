package com.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyReqType;
import com.rwproto.RequestProtos.Request;

/**
 * 帮派副本消息分发器
 * 
 * @author Alex 2016年6月13日 上午9:59:25
 */
public class GroupCopyCmdService implements FsService<GroupCopyCmdReqMsg, GroupCopyReqType> {
	
	@Override
	public ByteString doTask(GroupCopyCmdReqMsg request, Player player) {
		// TODO Auto-generated method stub
		GroupCopyCmdHandler cmdHandler = GroupCopyCmdHandler.getInstance();
		ByteString bStr = null;
		try {
			GroupCopyReqType reqType = request.getReqType();
			switch (reqType) {
			case GET_INFO:
				bStr = cmdHandler.getGroupCopyInfo(player, request);
				break;
			case GET_DROP_APPLY_INFO:// 请求章节战利品及申请人
				bStr = cmdHandler.getDropApplyInfo(player, request);
				break;
			case BUFF_DONATE:// 赞助buff
				bStr = cmdHandler.donateBuff(player, request);
				break;

			case GET_GROUP_HURT_RANK:// 帮派前10排行榜
				bStr = cmdHandler.getGroupDamageRank(player, request);
				break;
			case APPLY_SERVER_RANK:
				bStr = cmdHandler.getServerRankInfo(player, request);
				break;
			case APPLY_WAR_PRICE:// 申请战利品
				bStr = cmdHandler.applyWarPrice(player, request);
				break;
			case CANCEL_APPLY_ITEM:// 取消申请战利品
				bStr = cmdHandler.cancelApplyItem(player, request);
				break;
			case GET_DIST_REWARD_LOG:// 奖励分配记录
				bStr = cmdHandler.getDistRewardLog(player);
				break;
			default:
				GameLog.error(LogModule.GroupCopy, "GroupCopyCmdService[doTask]", "接收到了一个Unknown的消息，无法处理", null);
				break;
			}

		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyCmdService[doTask]", "出现了Exception异常", e);
		}

		return bStr;
	}

	@Override
	public GroupCopyCmdReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		GroupCopyCmdReqMsg reqMsg = GroupCopyCmdReqMsg.parseFrom(request.getBody().getSerializedContent());
		return reqMsg;
	}

	@Override
	public GroupCopyReqType getMsgType(GroupCopyCmdReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}

}
