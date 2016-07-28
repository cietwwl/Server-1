package com.playerdata.teambattle.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TeamBattleProto.TBRequestType;
import com.rwproto.TeamBattleProto.TeamBattleReqMsg;

/**
 * 组队副本的分发
 * @author AkenWang
 *
 */
public class TeamBattleService implements FsService {

	private TeamBattleHandler mHandler = TeamBattleHandler.getInstance();

	@SuppressWarnings("finally")
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			TeamBattleReqMsg msgTBRequest = TeamBattleReqMsg.parseFrom(request.getBody().getSerializedContent());
			TBRequestType msType = msgTBRequest.getReqType();
			switch (msType) {
			case SYN_TEAM_BATTLE:
				result = mHandler.synTeamBattle(player, msgTBRequest);
				break;
			case NON_SYN_TEAM_BATTLE:
				result = mHandler.nonSynTeamBattle(player, msgTBRequest);
				break;
			case SAVE_TEAM_INFO:
				result = mHandler.saveTeamInfo(player, msgTBRequest);
				break;
			case CREATE_TEAM:
				result = mHandler.createTeam(player, msgTBRequest);
				break;
			case JOIN_TEAM:
				result = mHandler.joinTeam(player, msgTBRequest);
				break;
			case ACCEPT_INVITE:
				result = mHandler.acceptInvite(player, msgTBRequest);
				break;
			case SET_TEAM_FREE_JION:
				result = mHandler.setTeamFreeJion(player, msgTBRequest);
				break;
			case KICK_OFF_MEMBER:
				result = mHandler.kickoffMember(player, msgTBRequest);
				break;
			case INVITE_PLAYER:
				result = mHandler.invitePlayer(player, msgTBRequest);
				break;
			case START_FIGHT:
				result = mHandler.startFight(player, msgTBRequest);
				break;
			case INFORM_FIGHT_RESULT:
				result = mHandler.informFightResult(player, msgTBRequest);
				break;
			case LEAVE_TEAM:
				result = mHandler.leaveTeam(player, msgTBRequest);
				break;
			case SCORE_EXCHANGE:
				result = mHandler.scoreExchage(player, msgTBRequest);
				break;
			default:
				GameLog.error(LogModule.TeamBattle, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			GameLog.error(LogModule.TeamBattle, player.getUserId(), "出现了Exception异常", e);
		} catch (Exception e) {
			GameLog.error(LogModule.TeamBattle, player.getUserId(), "出现了Exception异常", e);
		} finally {
			return result;
		}
	}
}