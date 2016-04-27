package com.playerdata.charge.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.charge.ChargeResult;
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
	public ByteString getReward(Player player, ChargeServiceCommonReqMsg request) {
		ChargeServiceCommonRspMsg.Builder response = ChargeServiceCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		
		
		ChargeResult chargeResult   = ChargeMgr.getInstance().gerReward(player);
		response.setIsSuccess(chargeResult.isSuccess());
		chargeResult.setTips(chargeResult.getTips());	
		
		return response.build().toByteString();
	}
	

	public ByteString buyVipGift(Player player, ChargeServiceCommonReqMsg request) {
		ChargeServiceCommonRspMsg.Builder response = ChargeServiceCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		
		int vipLevel = player.getVip();
		VipGiftCfg vipGiftCfg = VipGiftCfgDao.getInstance().getByVip(vipLevel);
		if(vipGiftCfg!=null && !player.getVipMgr().isVipGiftTaken(vipLevel)){
			//先设置已领取，防止下面操作出错的时候重复领取
			player.getVipMgr().setVipGiftTaken(vipLevel);			
			
			String chargeItemId = vipGiftCfg.getChargeCfgId();			
			ChargeResult chargeResult = ChargeMgr.getInstance().chargeAndTakeGift(player, chargeItemId);
			
			response.setIsSuccess(chargeResult.isSuccess());
			response.setTipMsg(chargeResult.getTips());		
		}else{
			response.setIsSuccess(false);
			response.setTipMsg("");				
			
		}
		
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
