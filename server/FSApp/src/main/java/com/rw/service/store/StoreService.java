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

public class StoreService implements FsService<StoreRequest, eStoreRequestType> {
	private  StoreHandler handler  = StoreHandler.getInstance(); 
	
	@Override
	public ByteString doTask(StoreRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			eStoreRequestType reqType = request.getRequestType();
			switch (reqType) {
				case OpenStore:
					result = handler.OpenStore(player, request.getStoreType());
					break;
				case BuyCommodity:
					result = handler.BuyCommodity(player, request.getCommodity());
					break;
				case RefreshStore:
					result = handler.RefreshStore(player, request.getStoreType());
					break;
				case WakenRewardDraw:
					result = handler.wakenRewardDraw(player, request);
					break;
				case WakenExchange:
					result = handler.exchangeWakenItem(player, request);
					break;
				case RefreshExchangeItem:
					result = handler.refreshExchangeItem(player, request);
					break;
				case ViewStore:
					result = handler.viewStore(player, request);
					break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public StoreRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		StoreRequest req = StoreRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}
	@Override
	public eStoreRequestType getMsgType(StoreRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}

}
