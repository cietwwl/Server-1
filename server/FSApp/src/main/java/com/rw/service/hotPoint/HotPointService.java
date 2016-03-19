package com.rw.service.hotPoint;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.HotPointServiceProtos.EHotPointRequestType;
import com.rwproto.HotPointServiceProtos.HotPointRequest;
import com.rwproto.RequestProtos.Request;

public class HotPointService implements FsService{
	private HotPointHandler hotPointHandler = HotPointHandler.getInstance();
	
	public ByteString doTask(Request request, Player pPlayer) {
		ByteString result = null;
		try {
			HotPointRequest hotPointRequest = HotPointRequest.parseFrom(request.getBody().getSerializedContent());
			EHotPointRequestType hotPointType = hotPointRequest.getRequestType();
			switch (hotPointType) {
				case CHANGE_HOT_POINT:
					result = hotPointHandler.changeHotPoint(hotPointRequest, pPlayer);
					break;
			}
		}catch(InvalidProtocolBufferException e){
			e.printStackTrace();
		}
		return result;
	}
}
