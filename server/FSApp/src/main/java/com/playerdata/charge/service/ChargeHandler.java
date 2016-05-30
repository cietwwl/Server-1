package com.playerdata.charge.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfgDAO;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.charge.ChargeResult;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.cfg.VipGiftCfg;
import com.playerdata.charge.cfg.VipGiftCfgDao;
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
		response.setTipMsg(chargeResult.getTips());		
		
		return response.build().toByteString();
	}
	public ByteString getRewardForFirstPay(Player player, ChargeServiceCommonReqMsg request) {
		ChargeServiceCommonRspMsg.Builder response = ChargeServiceCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		
		
		ChargeResult chargeResult   = ChargeMgr.getInstance().gerRewardForFirstPay(player);
		response.setIsSuccess(chargeResult.isSuccess());
		chargeResult.setTips(chargeResult.getTips());	
		
		return response.build().toByteString();
	}
	

	public ByteString buyVipGift(Player player, ChargeServiceCommonReqMsg request) {
		ChargeServiceCommonRspMsg.Builder response = ChargeServiceCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		String vipGiftId = request.getChargeItemId();				
		ChargeResult chargeResult = ChargeMgr.getInstance().buyAndTakeVipGift(player, vipGiftId);
		response.setIsSuccess(chargeResult.isSuccess());
		response.setTipMsg(chargeResult.getTips());		

		
		return response.build().toByteString();
	}
	
	public ByteString buyMonthCard(Player player, ChargeServiceCommonReqMsg request){
		ChargeServiceCommonRspMsg.Builder response = ChargeServiceCommonRspMsg.newBuilder();
		
		response.setReqType(request.getReqType());
		String chargeItemId = request.getChargeItemId();//月卡类型
		
	
		
		ChargeResult chargeResult = ChargeMgr.getInstance().buyMonthCard(player, chargeItemId);
		response.setIsSuccess(chargeResult.isSuccess());
		response.setTipMsg(chargeResult.getTips());		
		
		
		return response.build().toByteString();
	}
	

}
