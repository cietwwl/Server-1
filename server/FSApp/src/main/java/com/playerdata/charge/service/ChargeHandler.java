package com.playerdata.charge.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.timeCardType.data.FriendMonthCardInfoHolder;
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
		
		ChargeResult chargeResult = ChargeMgr.getInstance().buyMonthCardByGm(player, chargeItemId);
		response.setIsSuccess(chargeResult.isSuccess());
		response.setTipMsg(chargeResult.getTips());		
			
		return response.build().toByteString();
	}
	
	/**
	 * 赠送好友一张月卡
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString sendFriendMonthCard(Player player, ChargeServiceCommonReqMsg request){
		ChargeServiceCommonRspMsg.Builder response = ChargeServiceCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		String chargeItemId = request.getChargeItemId();//月卡类型
		boolean result = FriendMonthCardInfoHolder.getInstance().canSendMonthCard(request.getFriendId(), chargeItemId);
		if(result){
			response.setIsSuccess(true);
		}else{
			response.setIsSuccess(false);
		}
		response.setTipMsg("对方该类型的月卡剩余期限超过上限值，不能再接受赠送");
		return response.build().toByteString();
	}
	
	/**
	 * 获取好友的月卡状态，用于赠送的时候，前端按钮颜色的显示
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString getFriendMonthCardInfo(Player player, ChargeServiceCommonReqMsg request){
		ChargeServiceCommonRspMsg.Builder response = ChargeServiceCommonRspMsg.newBuilder();	
		response.setReqType(request.getReqType());
		response.setIsSuccess(true);;
		FriendMonthCardInfoHolder.getInstance().synAllFriendData(player);
		return response.build().toByteString();
	}
}
