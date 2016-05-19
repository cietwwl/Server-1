package com.playerdata.mgcsecret.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
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
				result = mHandler.getMSSingleReward(player, msgMSRequest);
				break;
			case EXCHANGE_BUFF:
				result = mHandler.exchangeBuff(player, msgMSRequest);
			case OPEN_REWARD_BOX:
				result = mHandler.openRewardBox(player, msgMSRequest);
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		} finally {
			return result;
		}
	}
}