package com.rw.service.ranodmBoss;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RandomBossProto.MsgType;
import com.rwproto.RandomBossProto.RandomMsgRequest;
import com.rwproto.RequestProtos.Request;

/**
 * 随机boss系统消息分发器
 * @author Alex
 * 2016年9月8日 下午10:09:04
 */
public class RandomBossService implements FsService<RandomMsgRequest, MsgType>{

	@Override
	public ByteString doTask(RandomMsgRequest request, Player player) {
		RandomBossMsgHandler handler = RandomBossMsgHandler.getHandler();
		ByteString bstr = null;
		try {
			MsgType msgType = getMsgType(request);
			switch (msgType) {
			case ACCEPTED_INVITED://接受邀请
				bstr = handler.acceptedInvited(player, request);
				break;
			case END_BATTLE://结束战斗
				bstr = handler.endBattle(player, request);
				break;
			case APPLY_BATTLE://申请进入战斗
				bstr = handler.applyEnterBattle(player, request);
				break;
			case GET_BATTLE_INFO://获取讨伐列表
				bstr = handler.getBattleInfo(player, request);
				break;
			case GET_BOSS_LIST://获取boss列表
				bstr = handler.getBossList(player);
				break;
			case INVITE_FRIEND_BATTLE://邀请好友战斗
				bstr = handler.invitedFriendBattle(player, request);
				break;
			default:
				break;
			}
			
		} catch (Exception e) {
			GameLog.error("RandomBoss", "RandomBossService[doTask]", "出现了异常", e);
		}
		
		return bstr;
	}

	@Override
	public RandomMsgRequest parseMsg(Request request)
			throws InvalidProtocolBufferException {
		return RandomMsgRequest.parseFrom(request.getBody().getSerializedContent());
	}

	@Override
	public MsgType getMsgType(RandomMsgRequest request) {
		return request.getMsgType();
	}

}
