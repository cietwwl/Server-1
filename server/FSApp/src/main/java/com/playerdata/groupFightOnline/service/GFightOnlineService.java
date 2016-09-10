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

public class GFightOnlineService implements FsService<GroupFightOnlineReqMsg, GFRequestType> {
	
	private GFightOnlineHandler gfHandler = GFightOnlineHandler.getInstance();

	@Override
	public ByteString doTask(GroupFightOnlineReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			GFRequestType gfType = request.getReqType();
			
			int resourceID = request.getResourceID();
			
			String clientDataVersion = request.getClientVersion();
			switch (gfType) {
			case GET_RESOURCE_INFO:
				result = gfHandler.getResourceInfo(player, request);
				break;
			case GROUP_BIDDING:
				result = gfHandler.groupBidding(player, request);
				break;
			case PERSONAL_BIDDING:
				result = gfHandler.personalBidding(player, request);
				break;
			case MODIFY_SELF_DEFENDER:
				result = gfHandler.modifySelfDefender(player, request);
				break;
			case GET_ENIMY_DEFENDER:
				result = gfHandler.getEnimyDefender(player, request);
				break;
			case CHANGE_ENIMY_DEFENDER:
				result = gfHandler.changeEnimyDefender(player, request);
				break;
			case START_FIGHT:
				result = gfHandler.startFight(player, request);
				break;
			case INFORM_FIGHT_RESULT:
				result = gfHandler.informFightResult(player, request);
				break;
			case GET_GROUP_BID_RANK:
				result = gfHandler.getGroupBidRank(player, request);
				break;
			case GET_KILL_RANK:
				result = gfHandler.getKillRank(player, request);
				break;
			case GET_HURT_RANK:
				result = gfHandler.getHurtRank(player, request);
				break;
			case GET_ALL_RANK_IN_GROUP:
				result = gfHandler.getAllRankInGroup(player, request);
				break;
			case GET_DEFENDER_TEAMS:
				result = gfHandler.getDefenderTeams(player, request);
				break;
			case VIEW_DEFENDER_TEAM:
				result = gfHandler.viewDefenderTeam(player, request);
				break;
			case GET_FIGHT_RECORD:
				result = gfHandler.getFightRecord(player, request);
				break;
			case GET_FIGHT_OVER_REWARD:
				result = gfHandler.getFightOverReward(player, request);
				break;
			case SYN_GROUP_DATA:
				result = gfHandler.synGroupData(player, request);
				break;
			default:
				GameLog.error(LogModule.GroupFightOnline, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
			GFightDataVersionMgr.synByVersion(player, resourceID, clientDataVersion);
			
		} catch (Exception e) {
			GameLog.error(LogModule.GroupFightOnline, player.getUserId(), "出现了Exception异常", e);
		} 
		return result;
	}

	@Override
	public GroupFightOnlineReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		GroupFightOnlineReqMsg msgGFRequest = GroupFightOnlineReqMsg.parseFrom(request.getBody().getSerializedContent());
		return msgGFRequest;
	}

	@Override
	public GFRequestType getMsgType(GroupFightOnlineReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}