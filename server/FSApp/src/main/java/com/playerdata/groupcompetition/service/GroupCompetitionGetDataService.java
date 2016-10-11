package com.playerdata.groupcompetition.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GroupCompetitionProto.CommonGetDataReqMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.RequestProtos.Request;

public class GroupCompetitionGetDataService implements FsService<CommonGetDataReqMsg, GCRequestType> {

	@Override
	public ByteString doTask(CommonGetDataReqMsg request, Player player) {
		switch (request.getReqType()) {
		case GetSelectionData:
			return GroupCompetitionHandler.getInstance().getSelectionData(player);
		case GetMatchView:
			return GroupCompetitionHandler.getInstance().getMatchData(player);
		case LiveMsg:
			return GroupCompetitionHandler.getInstance().getFightRecordLive(player, request);
		case LeaveLivePage:
			return GroupCompetitionHandler.getInstance().leaveLivePage(player, request);
		case PlaybackMsg:
			return GroupCompetitionHandler.getInstance().getMatchDetailInfo(player, request);
		case GetKillRank:
			return GroupCompetitionHandler.getInstance().getKillRank(player, request);
		case GetScoreRank:
			return GroupCompetitionHandler.getInstance().getScoreRank(player, request);
		case GetWinRank:
			return GroupCompetitionHandler.getInstance().getWinRank(player, request);
		case GetGroupScoreRank:
			return GroupCompetitionHandler.getInstance().getGroupScoreRank(player);
		case GetNewestScore:
			return GroupCompetitionHandler.getInstance().getGroupNewestScore(player, request);
		case GetFightInfoInScene:
			return GroupCompetitionHandler.getInstance().getFightInfoInScene(player, request);
		default:
			return null;
		}
	}

	@Override
	public CommonGetDataReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		return CommonGetDataReqMsg.parseFrom(request.getBody().getSerializedContent());
	}

	@Override
	public GCRequestType getMsgType(CommonGetDataReqMsg request) {
		return request.getReqType();
	}

}
