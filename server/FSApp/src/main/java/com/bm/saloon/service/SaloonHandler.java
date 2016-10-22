package com.bm.saloon.service;

import org.apache.commons.lang3.StringUtils;

import com.bm.saloon.ISaloonBm;
import com.bm.saloon.SaloonBmFactory;
import com.bm.saloon.SaloonResult;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.SaloonServiceProto.CommonReqMsg;
import com.rwproto.SaloonServiceProto.CommonRspMsg;
import com.rwproto.SaloonServiceProto.Position;
import com.rwproto.SaloonServiceProto.SaloonType;

public class SaloonHandler {
	
	private static SaloonHandler instance = new SaloonHandler();
	
	public static SaloonHandler getInstance(){
		return instance;
	}

	public ByteString enter(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setSuccess(false);
		
		SaloonType saloonType = commonReq.getSaloonType();
		Position position = commonReq.getPosition();
		ISaloonBm saloonBm = SaloonBmFactory.getInstance().get(saloonType);
		
		SaloonResult result = saloonBm.enter(player.getUserId(), position.getX(), position.getY());
		
		if(result.isSuccess()){
			result = saloonBm.synAllPlayerInfo(player);
			if(result.isSuccess()){
				response.setSuccess(true);
			}
		}
		
		if(StringUtils.isNotBlank(result.getReason())){
			response.setTipMsg(result.getReason());
		}
		
		return response.build().toByteString();
	}
	public ByteString updatePosition(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setSuccess(true);
		
		SaloonType saloonType = commonReq.getSaloonType();
		Position position = commonReq.getPosition();
		ISaloonBm saloonBm = SaloonBmFactory.getInstance().get(saloonType);
		
		SaloonResult result = saloonBm.updatePosition(player.getUserId(), position.getX(), position.getY());
		
		if(!result.isSuccess()){
			response.setSuccess(false);
			if(StringUtils.isNotBlank(result.getReason())){
				response.setTipMsg(result.getReason());
			}
		}
		
		return response.build().toByteString();
	}

	public ByteString leave(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setSuccess(true);
		
		SaloonType saloonType = commonReq.getSaloonType();
		ISaloonBm saloonBm = SaloonBmFactory.getInstance().get(saloonType);
		
		SaloonResult result = saloonBm.leave(player.getUserId());
		
		if(!result.isSuccess()){
			response.setSuccess(false);
			if(StringUtils.isNotBlank(result.getReason())){
				response.setTipMsg(result.getReason());
			}
		}
		
		return response.build().toByteString();
	
	}
	
	


}
