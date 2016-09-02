package com.rw.service.FresherActivity;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.playerdata.readonly.FresherActivityMgrIF;
import com.rw.service.FsService;
import com.rwproto.FrshActProtos.FrshActRequest;
import com.rwproto.FrshActProtos.FrshActResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;

public class FresherActivityService implements FsService<FrshActRequest, Command>{

	private ByteString handlerAchieveActivityReward(Player player, int cfgId){
		FresherActivityMgrIF fresherActivityMgr = player.getFresherActivityMgrIF();
		String result = fresherActivityMgr.achieveFresherActivityReward(player, cfgId);
		FrshActResponse.Builder resp = FrshActResponse.newBuilder();
		resp.setCfgId(cfgId);
		
		if(result != null){
			resp.setResult(0);
			resp.setResultValue(result);
		}else{
			resp.setResult(1);
		}
		return resp.build().toByteString();
	}

	@Override
	public ByteString doTask(FrshActRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try{
			int cfgId = request.getCfgId();
			result = handlerAchieveActivityReward(player, cfgId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public FrshActRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		FrshActRequest req = FrshActRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public Command getMsgType(FrshActRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
