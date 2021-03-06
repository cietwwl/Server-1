package com.playerdata.charge.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ChargeServiceProto.ChargeServiceCommonReqMsg;
import com.rwproto.ChargeServiceProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class ChargeService  implements FsService<ChargeServiceCommonReqMsg, RequestType>{

	@Override
	public ByteString doTask(ChargeServiceCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType){
				case Charge:
				result = ChargeHandler.getInstance().charge(player, request);
				break;
				case FirstChargeReward:
					result = ChargeHandler.getInstance().getRewardForFirstPay(player, request);
					break;
				case BuyVipGift:
					result = ChargeHandler.getInstance().buyVipGift(player, request);
					break;
				case TimeCard:
					result = ChargeHandler.getInstance().buyMonthCard(player, request);
					break;
				case GetFriendMonthCardInfo:
					result = ChargeHandler.getInstance().getFriendMonthCardInfo(player, request);
					break;
				case SendFriendMonthCard:
					result = ChargeHandler.getInstance().sendFriendMonthCard(player, request);
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
	public ChargeServiceCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		ChargeServiceCommonReqMsg chargetReq = ChargeServiceCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return chargetReq;
	}

	@Override
	public RequestType getMsgType(ChargeServiceCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}
