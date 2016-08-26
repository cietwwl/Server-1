package com.rw.service.ranking;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RankServiceProtos.ERankRequestType;
import com.rwproto.RankServiceProtos.MsgRankRequest;
import com.rwproto.RequestProtos.Request;

public class RankingService  implements FsService<MsgRankRequest, ERankRequestType>{
	private RankingHandler rankingHandler = RankingHandler.getInstance();

	@Override
	public ByteString doTask(MsgRankRequest request, Player pPlayer) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			ERankRequestType requestType = request.getRequestType();
			switch (requestType) {
				case RANK_LIST:
					result = rankingHandler.rankingList(request, pPlayer);
					break;
				case RANK_HERO_INFO:
					result = rankingHandler.rankingInfo(request, pPlayer);
					break;
				case RANK_MY_INFO:
					result = rankingHandler.rankingInfoSelf(request, pPlayer);
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
	public MsgRankRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgRankRequest rankRequest = MsgRankRequest.parseFrom(request.getBody().getSerializedContent());
		return rankRequest;
	}

	@Override
	public ERankRequestType getMsgType(MsgRankRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}
}
