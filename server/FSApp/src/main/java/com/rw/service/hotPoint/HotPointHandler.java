package com.rw.service.hotPoint;

import com.google.protobuf.ByteString;
import com.playerdata.HotPointMgr;
import com.playerdata.Player;
import com.rwbase.dao.hotPoint.EHotPointType;
import com.rwproto.HotPointServiceProtos.HotPointRequest;
import com.rwproto.HotPointServiceProtos.HotPointResponse;

public class HotPointHandler {
	private static HotPointHandler instance = new HotPointHandler();	
	private HotPointHandler(){		
	}
	
	public static HotPointHandler getInstance(){
		return instance;
	}
	
	/**客户端请求设置数据*/
	public ByteString changeHotPoint(HotPointRequest request, Player pPlayer){
		HotPointMgr.changeHotPointState(pPlayer.getUserId(), EHotPointType.valueOf(request.getHotPointInfo().getType()), request.getHotPointInfo().getValue());
		
		HotPointResponse.Builder response = HotPointResponse.newBuilder();
		return response.build().toByteString();
	}
}
