package com.playerdata.mgcsecret.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MagicSecretProto.MagicSecretReqMsg;
import com.rwproto.MagicSecretProto.msRequestType;
import com.rwproto.RequestProtos.Request;

/**
 * 法宝秘境的分发
 * @author AkenWang
 *
 */
public class MagicSecretService implements FsService {

	private MagicSecretHandler mHandler = MagicSecretHandler.getInstance();

	@SuppressWarnings("finally")
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MagicSecretReqMsg msgMSRequest = MagicSecretReqMsg.parseFrom(request.getBody().getSerializedContent());
			msRequestType msType = msgMSRequest.getReqType();
			switch (msType) {
			case GET_MS_RANK:
				result = mHandler.getMSRankData(player, msgMSRequest);
				break;
			case ENTER_MS_FIGHT:
				result = mHandler.enterMSFight(player, msgMSRequest);
				break;
			case GET_MS_SINGLE_REWARD:
				result = mHandler.getMSSingleReward(player, msgMSRequest);
				break;
			case GET_MS_SWEEP_REWARD:
				result = mHandler.getMSSweepReward(player, msgMSRequest);
				break;
			case EXCHANGE_BUFF:
				result = mHandler.exchangeBuff(player, msgMSRequest);
				break;
			case OPEN_REWARD_BOX:
				result = mHandler.openRewardBox(player, msgMSRequest);
				break;
			case CHANGE_ARMY:
				result = mHandler.changeMSArmy(player, msgMSRequest);
				break;
			case GET_SCORE_REWARD:
				result = mHandler.getScoreReward(player, msgMSRequest);
				break;
			case GET_SELF_MS_RANK:
				result = mHandler.getSelfMSRank(player, msgMSRequest);
				break;
			case GIVE_UP_REWARD_BOX:
				result = mHandler.giveUpRewardBox(player, msgMSRequest);
				break;
			default:
				GameLog.error(LogModule.MagicSecret, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			GameLog.error(LogModule.MagicSecret, player.getUserId(), "出现了Exception异常", e);
		} finally {
			return result;
		}
	}
}