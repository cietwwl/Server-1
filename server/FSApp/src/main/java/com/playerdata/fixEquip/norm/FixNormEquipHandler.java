package com.playerdata.fixEquip.norm;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipResult;
import com.rwproto.FixEquipProto.CommonReqMsg;
import com.rwproto.FixEquipProto.CommonRspMsg;

public class FixNormEquipHandler {
	
	private static FixNormEquipHandler instance = new FixNormEquipHandler();
	
	public static FixNormEquipHandler getInstance(){
		return instance;
	}

	public ByteString levelUp(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String ownerId = commonReq.getOwnerId();
		String cfgId = commonReq.getCfgId();
		
		FixEquipResult result = FixNormEquipMgr.getInstance().levelUp(player, ownerId, cfgId);
		
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
		
		FixEquipResult result = FixNormEquipMgr.getInstance().qualityUp(player, ownerId, cfgId);
		
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
		
		FixEquipResult result = FixNormEquipMgr.getInstance().starUp(player, ownerId, cfgId);
		
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
		
		FixEquipResult result = FixNormEquipMgr.getInstance().starDown(player, ownerId, cfgId);
		
		response.setIsSuccess(result.isSuccess());
		if(StringUtils.isNotBlank(result.getReason())){
			response.setTipMsg(result.getReason());
		}
		
		return response.build().toByteString();
	}



}
