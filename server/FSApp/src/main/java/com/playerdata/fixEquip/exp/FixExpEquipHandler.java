package com.playerdata.fixEquip.exp;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
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
		String cfgId = commonReq.getCfgId();
		
		FixEquipResult result = FixExpEquipMgr.getInstance().levelUp(player, ownerId, cfgId);
		
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
		String cfgId = commonReq.getCfgId();
		
		FixEquipResult result = FixExpEquipMgr.getInstance().qualityUp(player, ownerId, cfgId);
		
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
		String cfgId = commonReq.getCfgId();
		
		FixEquipResult result = FixExpEquipMgr.getInstance().starUp(player, ownerId, cfgId);
		
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
		String cfgId = commonReq.getCfgId();
		
		FixEquipResult result = FixExpEquipMgr.getInstance().starDown(player, ownerId, cfgId);
		
		response.setIsSuccess(result.isSuccess());
		if(StringUtils.isNotBlank(result.getReason())){
			response.setTipMsg(result.getReason());
		}
		
		return response.build().toByteString();
	}



}
