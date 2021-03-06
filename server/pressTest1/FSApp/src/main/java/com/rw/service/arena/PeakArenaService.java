package com.rw.service.arena;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.PeakArenaServiceProtos.MsgArenaRequest;
import com.rwproto.PeakArenaServiceProtos.eArenaType;
import com.rwproto.RequestProtos.Request;

public class PeakArenaService implements FsService {

	private PeakArenaHandler peakArenaHandler = PeakArenaHandler.getInstance();

	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MsgArenaRequest msgArenaRequest = MsgArenaRequest.parseFrom(request.getBody().getSerializedContent());
			eArenaType arenaType = msgArenaRequest.getArenaType();
			switch (arenaType) {
			case GET_DATA:
				result = peakArenaHandler.getPeakArenaData(msgArenaRequest, player);
				break;
			case CHANGE_ENEMY:
				result = peakArenaHandler.getEnemys(msgArenaRequest, player);
				break;
			case CHANGE_HERO:
				result = peakArenaHandler.changeHeros(msgArenaRequest, player);
				break;
			case ARENA_RECORD:
				result = peakArenaHandler.getRecords(msgArenaRequest, player);
				break;
			case ENEMY_INFO:
				result = peakArenaHandler.getEnemyInfo(msgArenaRequest, player);
				break;
			case CLEAR_TIME:
				result = peakArenaHandler.clearCD(msgArenaRequest, player);
				break;
			case ARENA_FIGHT_PREPARE:
				result = peakArenaHandler.initFightInfo(msgArenaRequest, player);
				break;
			case ARENA_FIGHT_START:
				result = peakArenaHandler.arenaFightStart(msgArenaRequest, player);
				break;
			case ARENA_FIGHT_FINISH:
				result = peakArenaHandler.arenaFightFinish(msgArenaRequest, player);
				break;
			case GET_PLACE:
				result = peakArenaHandler.getPlaceByteString(msgArenaRequest, player);
				break;
			case GET_SCORE:
				result = peakArenaHandler.gainScore(msgArenaRequest, player);
				break;
			case SWITCH_OVER:
				result = peakArenaHandler.switchTeam(msgArenaRequest, player);
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
