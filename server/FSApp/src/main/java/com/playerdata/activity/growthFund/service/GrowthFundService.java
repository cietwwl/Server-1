package com.playerdata.activity.growthFund.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType;
import com.rwproto.GrowthFundServiceProto.GrowthFundRequest;
import com.rwproto.RequestProtos.Request;

public class GrowthFundService implements FsService<GrowthFundRequest, EGrowthFundRequestType> {

	@Override
	public ByteString doTask(GrowthFundRequest request, Player player) {
		switch (request.getReqType()) {
		case BUY_GROWTH_FUND:
			return GrowthFundHandler.getInstance().requestBuyGrowthFundGift(player);
		case GET_GROWTH_FUND_GIFT:
			return GrowthFundHandler.getInstance().requestGetGrowthFundGift(player, request);
		case GET_GROWTH_FUND_REWARD:
			return GrowthFundHandler.getInstance().requestGetGrowthFundReward(player, request);
		default:
			GameLog.error("GrowthFundService", player.getUserId(), "未知的请求类型：" + request.getReqType());
			return ByteString.EMPTY;
		}
	}

	@Override
	public GrowthFundRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		return GrowthFundRequest.PARSER.parseFrom(request.getBody().getSerializedContent());
	}

	@Override
	public EGrowthFundRequestType getMsgType(GrowthFundRequest request) {
		return request.getReqType();
	}

}
