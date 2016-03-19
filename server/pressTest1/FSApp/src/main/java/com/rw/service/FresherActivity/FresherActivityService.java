package com.rw.service.FresherActivity;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.playerdata.readonly.FresherActivityMgrIF;
import com.rw.service.FsService;
import com.rwproto.FrshActProtos.FrshActRequest;
import com.rwproto.FrshActProtos.FrshActResponse;
import com.rwproto.RequestProtos.Request;

public class FresherActivityService implements FsService{

	@Override
	public ByteString doTask(Request request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try{
			FrshActRequest req = FrshActRequest.parseFrom(request.getBody().getSerializedContent());
			int cfgId = req.getCfgId();
			result = handlerAchieveActivityReward(player, cfgId);
		}catch(InvalidProtocolBufferException e){
			e.printStackTrace();
		}
		return result;
	}

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
}
