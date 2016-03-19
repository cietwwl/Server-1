package com.rw.service.ranking;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RankServiceProtos.ERankRequestType;
import com.rwproto.RankServiceProtos.MsgRankRequest;
import com.rwproto.RequestProtos.Request;

public class RankingService  implements FsService{
	private RankingHandler rankingHandler = RankingHandler.getInstance();
	
	public ByteString doTask(Request request, Player pPlayer) {
		ByteString result = null;
		try {
			MsgRankRequest rankRequest = MsgRankRequest.parseFrom(request.getBody().getSerializedContent());
			ERankRequestType requestType = rankRequest.getRequestType();
			switch (requestType) {
				case RANK_LIST:
					result = rankingHandler.rankingList(rankRequest, pPlayer);
					break;
				case RANK_HERO_INFO:
					result = rankingHandler.rankingInfo(rankRequest, pPlayer);
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
