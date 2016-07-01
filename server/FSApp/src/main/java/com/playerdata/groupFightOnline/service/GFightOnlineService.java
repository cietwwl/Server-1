package com.playerdata.groupFightOnline.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.version.GFightDataVersionMgr;
import com.rw.service.FsService;
import com.rwproto.GrouFightOnlineProto.GFRequestType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineReqMsg;
import com.rwproto.RequestProtos.Request;

public class GFightOnlineService implements FsService {
	
	private GFightOnlineHandler gfHandler = GFightOnlineHandler.getInstance();

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			GroupFightOnlineReqMsg msgGFRequest = GroupFightOnlineReqMsg.parseFrom(request.getBody().getSerializedContent());
			GFRequestType gfType = msgGFRequest.getReqType();
			
			String clientDataVersion = msgGFRequest.getClientVersion();
			switch (gfType) {
			case GET_RESOURCE_INFO:
				result = gfHandler.getResourceInfo(player, msgGFRequest);
				break;
			case GROUP_BIDDING:
				result = gfHandler.groupBidding(player, msgGFRequest);
				break;
			case PERSONAL_BIDDING:
				result = gfHandler.personalBidding(player, msgGFRequest);
				break;
			case MODIFY_SELF_DEFENDER:
				result = gfHandler.modifySelfDefender(player, msgGFRequest);
				break;
			case GET_ENIMY_DEFENDER:
				result = gfHandler.getEnimyDefender(player, msgGFRequest);
				break;
			case CHANGE_ENIMY_DEFENDER:
				result = gfHandler.changeEnimyDefender(player, msgGFRequest);
				break;
			case START_FIGHT:
				result = gfHandler.startFight(player, msgGFRequest);
				break;
			case INFORM_FIGHT_RESULT:
				result = gfHandler.informFightResult(player, msgGFRequest);
				break;
			case GET_GROUP_BID_RANK:
				result = gfHandler.getGroupBidRank(player, msgGFRequest);
				break;
			case GET_KILL_RANK:
				result = gfHandler.getKillRank(player, msgGFRequest);
				break;
			case GET_HURT_RANK:
				result = gfHandler.getHurtRank(player, msgGFRequest);
				break;
			case GET_ALL_RANK_IN_GROUP:
				result = gfHandler.getAllRankInGroup(player, msgGFRequest);
				break;
			case GET_DEFENDER_TEAMS:
				result = gfHandler.getDefenderTeams(player, msgGFRequest);
				break;
			case VIEW_DEFENDER_TEAM:
				result = gfHandler.viewDefenderTeam(player, msgGFRequest);
				break;
			case GET_FIGHT_RECORD:
				result = gfHandler.getFightRecord(player, msgGFRequest);
				break;
			case GET_FIGHT_OVER_REWARD:
				result = gfHandler.getFightOverReward(player, msgGFRequest);
				break;
			case SYN_GROUP_DATA:
				result = gfHandler.synGroupData(player, msgGFRequest);
				break;
			default:
				GameLog.error(LogModule.GroupFightOnline, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
			GFightDataVersionMgr.synByVersion(player, clientDataVersion);
			
		} catch (InvalidProtocolBufferException e) {
			GameLog.error(LogModule.GroupFightOnline, player.getUserId(), "出现了InvalidProtocolBufferException异常", e);
		} catch (Exception e) {
			GameLog.error(LogModule.GroupFightOnline, player.getUserId(), "出现了Exception异常", e);
		} finally {
			return result;
		}
	}
}