package com.playerdata.fixEquip.exp;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipResult;
import com.rwproto.FixEquipProto.CommonReqMsg;
import com.rwproto.FixEquipProto.CommonRspMsg;

public class FixExpEquipHandler {
	
	private static FixExpEquipHandler instance = new FixExpEquipHandler();
	
	public static FixExpEquipHandler getInstance(){
		return instance;
	}

	public ByteString levelUp(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String ownerId = commonReq.getOwnerId();
		String itemId = commonReq.getEquipId();
		
		Hero targetHero = player.getHeroMgr().getHeroById(ownerId);
		FixEquipResult result = targetHero.getFixExpEquipMgr().levelUp(player, ownerId, itemId);
		
		response.setIsSuccess(result.isSuccess());
		if(StringUtils.isNotBlank(result.getReason())){
			response.setTipMsg(result.getReason());
		}
		
		return response.build().toByteString();
	}
	
	public ByteString qualityUp(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String ownerId = commonReq.getOwnerId();
		String itemId = commonReq.getEquipId();
		
		Hero targetHero = player.getHeroMgr().getHeroById(ownerId);
		FixEquipResult result = targetHero.getFixExpEquipMgr().qualityUp(player, ownerId, itemId);
		
		response.setIsSuccess(result.isSuccess());
		if(StringUtils.isNotBlank(result.getReason())){
			response.setTipMsg(result.getReason());
		}
		
		return response.build().toByteString();
	}
	
	public ByteString starUp(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String ownerId = commonReq.getOwnerId();
		String itemId = commonReq.getEquipId();
		
		Hero targetHero = player.getHeroMgr().getHeroById(ownerId);
		FixEquipResult result = targetHero.getFixExpEquipMgr().starUp(player, ownerId, itemId);
		
		response.setIsSuccess(result.isSuccess());
		if(StringUtils.isNotBlank(result.getReason())){
			response.setTipMsg(result.getReason());
		}
		
		return response.build().toByteString();
	}
	public ByteString starDown(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String ownerId = commonReq.getOwnerId();
		String itemId = commonReq.getEquipId();
		
		Hero targetHero = player.getHeroMgr().getHeroById(ownerId);
		FixEquipResult result = targetHero.getFixExpEquipMgr().starDown(player, ownerId, itemId);
		
		response.setIsSuccess(result.isSuccess());
		if(StringUtils.isNotBlank(result.getReason())){
			response.setTipMsg(result.getReason());
		}
		
		return response.build().toByteString();
	}



}
