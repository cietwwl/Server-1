package com.playerdata.teambattle.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TeamBattleProto.TBRequestType;
import com.rwproto.TeamBattleProto.TBResultType;
import com.rwproto.TeamBattleProto.TeamBattleReqMsg;
import com.rwproto.TeamBattleProto.TeamBattleRspMsg;

/**
 * 组队副本的分发
 * @author AkenWang
 *
 */
public class TeamBattleService implements FsService<TeamBattleReqMsg, TBRequestType> {

	private TeamBattleHandler mHandler = TeamBattleHandler.getInstance();
	
	@Override
	public ByteString doTask(TeamBattleReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			
			TBRequestType msType = request.getReqType();
			switch (msType) {
			case SYN_TEAM_BATTLE:
				result = mHandler.synTeamBattle(player, request);
				break;
			case NON_SYN_TEAM_BATTLE:
				result = mHandler.nonSynTeamBattle(player, request);
				break;
			case SAVE_TEAM_INFO:
				result = mHandler.saveTeamInfo(player, request);
				break;
			case CREATE_TEAM:
				result = mHandler.createTeam(player, request);
				break;
			case JOIN_TEAM:
				result = mHandler.joinTeam(player, request);
				break;
			case ACCEPT_INVITE:
				result = mHandler.acceptInvite(player, request);
				break;
			case SET_TEAM_FREE_JION:
				result = mHandler.setTeamFreeJion(player, request);
				break;
			case KICK_OFF_MEMBER:
				result = mHandler.kickoffMember(player, request);
				break;
			case INVITE_PLAYER:
				result = mHandler.invitePlayer(player, request);
				break;
			case START_FIGHT:
				result = mHandler.startFight(player, request);
				break;
			case INFORM_FIGHT_RESULT:
				result = mHandler.informFightResult(player, request);
				break;
			case LEAVE_TEAM:
				result = mHandler.leaveTeam(player, request);
				break;
			case SCORE_EXCHANGE:
				result = mHandler.scoreExchage(player, request);
				break;
			case SAVE_MEMBER_POSITION:
				result = mHandler.saveMemPosition(player, request);
				break;
			case BUY_TIMES:
				result = mHandler.buyBattleTimes(player, request);
				break;
			case ADD_ROBOT:
				result = mHandler.addRobot(player, request);
				break;
			case GET_CAN_JION_TEAMS:
				result = mHandler.getCanJionTeams(player, request);
			default:
				GameLog.error(LogModule.TeamBattle, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.TeamBattle, player.getUserId(), "出现了Exception异常", e);
			TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder().setRstType(TBResultType.DATA_ERROR).setTipMsg("服务端数据异常");
			result = tbRsp.build().toByteString();
		} 
		return result;
	}

	@Override
	public TeamBattleReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		TeamBattleReqMsg msgTBRequest = TeamBattleReqMsg.parseFrom(request.getBody().getSerializedContent());
		return msgTBRequest;
	}

	@Override
	public TBRequestType getMsgType(TeamBattleReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}