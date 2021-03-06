package com.rw.service.PeakArena;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.PeakArena.datamodel.TablePeakArenaDataDAO;
import com.rwproto.PeakArenaServiceProtos.MsgArenaRequest;
import com.rwproto.PeakArenaServiceProtos.eArenaType;
import com.rwproto.RequestProtos.Request;

public class PeakArenaService implements FsService<MsgArenaRequest, eArenaType> {

	private PeakArenaHandler peakArenaHandler = PeakArenaHandler.getInstance();

	@Override
	public ByteString doTask(MsgArenaRequest request, Player player) {
		ByteString result = null;
		try {
			eArenaType arenaType = request.getArenaType();			
			switch (arenaType) {
			case GET_DATA:
				result = peakArenaHandler.getPeakArenaData(request, player);
				break;
			case CHANGE_ENEMY:
				result = peakArenaHandler.selectEnemys(request, player);
				break;
			case CHANGE_HERO:
				result = peakArenaHandler.changeHeros(request, player);
				break;
			case ARENA_RECORD:
				result = peakArenaHandler.getRecords(request, player);
				break;
			case ENEMY_INFO:
				result = peakArenaHandler.getEnemyInfo(request, player);
				break;
			case CLEAR_TIME:
				result = peakArenaHandler.clearCD(request, player);
				break;
			case ARENA_FIGHT_PREPARE:
				result = peakArenaHandler.initFightInfo(request, player);
				break;
			case ARENA_FIGHT_START:
				result = peakArenaHandler.fightStart(request, player);
				break;
			case FIGHT_CONTINUE:
				result = peakArenaHandler.fightContinue(request, player);
				break;
			case ARENA_FIGHT_FINISH:
				result = peakArenaHandler.fightFinish(request, player);
				break;
			case GET_PLACE:
				result = peakArenaHandler.getPlaceByteString(request, player);
				break;
			case SWITCH_OVER:
				result = peakArenaHandler.switchTeam(request, player);
				break;
			case BUY_CHALLENGE_COUNT:
				result = peakArenaHandler.buyChallengeCount(request, player);
				break;
			case GET_SCORE_REWARD_VIEW:
				result = peakArenaHandler.getScoreRewardView(request, player);
				break;
			case GET_SCORE_REWARD:
				result = peakArenaHandler.getOneScoreReward(request, player);
				break;
			case GET_ALL_SCORE_REWARD:
				result = peakArenaHandler.getAllScoreReward(player);
				break;
			case GET_RECORD:
				result = peakArenaHandler.getRecords(request, player);
				break;
			case GET_RECORD_DETAIL:
				result = peakArenaHandler.getPeakArenaRecordDetail(request, player);
				break;
			case GET_HURT_DETAIL:
				result = peakArenaHandler.getHurtDetail(request, player);
				break;
			default:
				break;
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally{
			TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
			if (arenaData != null) {
				TablePeakArenaDataDAO.getInstance().commit(arenaData);
			}
		}

		return result;
	}

	@Override
	public MsgArenaRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		MsgArenaRequest msgArenaRequest = MsgArenaRequest.parseFrom(request.getBody().getSerializedContent());
		return msgArenaRequest;
	}

	@Override
	public eArenaType getMsgType(MsgArenaRequest request) {
		return request.getArenaType();
	}

}
