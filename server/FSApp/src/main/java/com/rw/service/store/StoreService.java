package com.rw.service.store;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.HotPointServiceProtos.EHotPointRequestType;
import com.rwproto.HotPointServiceProtos.HotPointRequest;
import com.rwproto.RequestProtos.Request;
import com.rwproto.StoreProtos.StoreRequest;
import com.rwproto.StoreProtos.eStoreRequestType;

public class StoreService implements FsService {
	private  StoreHandler handler  = StoreHandler.getInstance(); 
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			StoreRequest req = StoreRequest.parseFrom(request.getBody().getSerializedContent());
			eStoreRequestType reqType = req.getRequestType();
			switch (reqType) {
				case OpenStore:
					result = handler.OpenStore(player, req.getStoreType());
					break;
				case BuyCommodity:
					result = handler.BuyCommodity(player, req.getCommodity());
					break;
				case RefreshStore:
					result = handler.RefreshStore(player, req.getStoreType());
					break;
				case WakenRewardDraw:
					result = handler.wakenRewardDraw(player, req);
					break;
				case WakenExchange:
					result = handler.exchangeWakenItem(player, req);
					break;
				case RefreshExchangeItem:
					
					break;
			}
		}catch(InvalidProtocolBufferException e){
			e.printStackTrace();
		}
		return result;
	}

}
