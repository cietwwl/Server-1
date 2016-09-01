package com.rw.service.arena;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ArenaServiceProtos.MsgArenaRequest;
import com.rwproto.ArenaServiceProtos.eArenaType;
import com.rwproto.RequestProtos.Request;


public class ArenaService implements FsService<MsgArenaRequest, eArenaType> {

	private ArenaHandler arenaHandler = ArenaHandler.getInstance();

	@Override
	public ByteString doTask(MsgArenaRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			eArenaType arenaType = request.getArenaType();
			switch (arenaType) {
			case GET_INFO:
				result = arenaHandler.getInfo(request, player);
				break;
			case CHANGE_ENEMY:
				result = arenaHandler.changeEnemys(request, player);
				break;
			case CHANGE_HERO:
				result = arenaHandler.changeHeros(request, player);
				break;
			case ARENA_RECORD:
				result = arenaHandler.getArenaRecordInfo(request, player);
				break;
			case ENEMY_INFO:
				result = arenaHandler.getEnemyInfoData(request, player);
				break;
			case CLEAR_TIME:
				result = arenaHandler.clearCD(request, player);
				break;
			case ARENA_FIGHT_PREPARE:
				result = arenaHandler.initFightInfo(request, player);
				break;
			case ARENA_FIGHT_START:
				result = arenaHandler.arenaFightStart(request, player);
				break;
			case ARENA_FIGHT_FINISH:
				result = arenaHandler.arenaFightFinish(request, player);
				break;
			case GET_PLACE:
				result = arenaHandler.getMyPlace(request, player);
				break;
			case GET_HURT_VALUE:
				result = arenaHandler.getHurtValue(request, player);
				break;
			case BUY_TIMES:
				result = arenaHandler.buyTimes(request, player);
				break;
			case SCORE:
				result = arenaHandler.getScoreInfo(request, player);
				break;
			case GET_REWARD:
				result = arenaHandler.getScoreReward(request, player);
				break;
			case HIS_RANK_REWARD_VIEW:
				result = arenaHandler.getHistoryView(request, player);
				break;
			case HIS_RANK_GET_REWARD:
				result = arenaHandler.getHistoryReward(request, player);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public MsgArenaRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgArenaRequest msgArenaRequest = MsgArenaRequest.parseFrom(request.getBody().getSerializedContent());
		return msgArenaRequest;
	}

	@Override
	public eArenaType getMsgType(MsgArenaRequest request) {
		// TODO Auto-generated method stub
		return request.getArenaType();
	}

}
