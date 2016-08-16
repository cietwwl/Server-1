package com.playerdata.groupcompetition.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MagicSecretProto.MagicSecretReqMsg;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;
import com.rwproto.MagicSecretProto.msRequestType;
import com.rwproto.MagicSecretProto.msResultType;
import com.rwproto.RequestProtos.Request;

/**
 * 帮派争霸赛的分发
 * @author AkenWang
 *
 */
public class GroupCompetitionService implements FsService<CommonReqMsg, ResultType> {

	private GroupCompetitionHandler mHandler = GroupCompetitionHandler.getInstance();

	@Override
	public ByteString doTask(MagicSecretReqMsg request, Player player) {
		ByteString result = null;
		try {
			msRequestType msType = request.getReqType();
			switch (msType) {
			case GET_MS_RANK:
				result = mHandler.getMSRankData(player, request);
				break;
			case ENTER_MS_FIGHT:
				result = mHandler.enterMSFight(player, request);
				break;
			case GET_MS_SINGLE_REWARD:
				result = mHandler.getMSSingleReward(player, request);
				break;
			case GET_MS_SWEEP_REWARD:
				result = mHandler.getMSSweepReward(player, request);
				break;
			case EXCHANGE_BUFF:
				result = mHandler.exchangeBuff(player, request);
				break;
			case OPEN_REWARD_BOX:
				result = mHandler.openRewardBox(player, request);
				break;
			case CHANGE_ARMY:
				result = mHandler.changeMSArmy(player, request);
				break;
			case GET_SCORE_REWARD:
				result = mHandler.getScoreReward(player, request);
				break;
			case GET_SELF_MS_RANK:
				result = mHandler.getSelfMSRank(player, request);
				break;
			case GIVE_UP_REWARD_BOX:
				result = mHandler.giveUpRewardBox(player, request);
				break;	
			case GIVE_UP_BUFF:
				result = mHandler.giveUpBuff(player, request);
				break;
			default:
				GameLog.error(LogModule.MagicSecret, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), "出现了Exception异常", e);
			MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
			msRsp.setReqType(request.getReqType());
			msRsp.setRstType(msResultType.DATA_ERROR);
			result = msRsp.build().toByteString();
		}
		return result;
	}

	@Override
	public MagicSecretReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MagicSecretReqMsg msgMSRequest = MagicSecretReqMsg.parseFrom(request.getBody().getSerializedContent());
		return msgMSRequest;
	}

	@Override
	public msRequestType getMsgType(MagicSecretReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}