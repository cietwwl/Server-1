package com.rw.service.arena;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ArenaServiceProtos.MsgArenaRequest;
import com.rwproto.ArenaServiceProtos.eArenaType;
import com.rwproto.RequestProtos.Request;


public class ArenaService implements FsService {

	private ArenaHandler arenaHandler = ArenaHandler.getInstance();

	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MsgArenaRequest msgArenaRequest = MsgArenaRequest.parseFrom(request.getBody().getSerializedContent());
			eArenaType arenaType = msgArenaRequest.getArenaType();
			System.out.println("arena"+ arenaType);
			switch (arenaType) {
			case GET_INFO:
				result = arenaHandler.getInfo(msgArenaRequest, player);
				break;
			case CHANGE_ENEMY:
				result = arenaHandler.changeEnemys(msgArenaRequest, player);
				break;
			case CHANGE_HERO:
				result = arenaHandler.changeHeros(msgArenaRequest, player);
				break;
			case ARENA_RECORD:
				result = arenaHandler.getArenaRecordInfo(msgArenaRequest, player);
				break;
			case ENEMY_INFO:
				result = arenaHandler.getEnemyInfoData(msgArenaRequest, player);
				break;
			case CLEAR_TIME:
				result = arenaHandler.clearCD(msgArenaRequest, player);
				break;
			case ARENA_FIGHT_PREPARE:
				result = arenaHandler.initFightInfo(msgArenaRequest, player);
				break;
			case ARENA_FIGHT_START:
				result = arenaHandler.arenaFightStart(msgArenaRequest, player);
				break;
			case ARENA_FIGHT_FINISH:
				result = arenaHandler.arenaFightFinish(msgArenaRequest, player);
				break;
			case GET_PLACE:
				result = arenaHandler.getMyPlace(msgArenaRequest, player);
				break;
			case GET_HURT_VALUE:
				result = arenaHandler.getHurtValue(msgArenaRequest, player);
				break;
			case BUY_TIMES:
				result = arenaHandler.buyTimes(msgArenaRequest, player);
				break;
			case SCORE:
				result = arenaHandler.getScoreInfo(msgArenaRequest, player);
				break;
			case GET_REWARD:
				result = arenaHandler.getScoreReward(msgArenaRequest, player);
				break;
			case HIS_RANK_REWARD_VIEW:
				result = arenaHandler.getHistoryView(msgArenaRequest, player);
				break;
			case HIS_RANK_GET_REWARD:
				result = arenaHandler.getHistoryReward(msgArenaRequest, player);
				break;
			default:
				break;
			}

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

		return result;
	}

}
