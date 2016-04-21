package com.playerdata.charge.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.charge.ChargeResult;
import com.rwproto.ChargeServiceProto.ChargeServiceCommonReqMsg;
import com.rwproto.ChargeServiceProto.ChargeServiceCommonRspMsg;


public class ChargeHandler {
	private static ChargeHandler instance = new ChargeHandler();

	public static ChargeHandler getInstance() {
		return instance;
	}


	public ByteString charge(Player player, ChargeServiceCommonReqMsg request) {
		ChargeServiceCommonRspMsg.Builder response = ChargeServiceCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		
		String chargeItemId = request.getChargeItemId();
		
		ChargeResult chargeResult = ChargeMgr.getInstance().charge(player, chargeItemId);
		response.setIsSuccess(chargeResult.isSuccess());
		chargeResult.setTips(chargeResult.getTips());		
		
		return response.build().toByteString();
	}

	

}
