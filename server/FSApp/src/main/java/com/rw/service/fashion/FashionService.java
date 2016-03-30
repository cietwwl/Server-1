package com.rw.service.fashion;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.FashionServiceProtos.FashionRequest;
import com.rwproto.RequestProtos.Request;


public class FashionService implements FsService{

	private FashionHandle fashionHandler = FashionHandle.getInstance();
	
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		FashionRequest req;
		try {
			req = FashionRequest.parseFrom(request.getBody().getSerializedContent());
			switch (req.getEventType()) {
			case buy:
				result = fashionHandler.buyFash(player,req);
				break;
			case off:
				result = fashionHandler.offFash(player,req);
				break;
			case on:
				result = fashionHandler.onFash(player,req);
				break;

			case renew:
				result = fashionHandler.renewFashion(player,req);
				break;
			case getFashiondata:
				result = fashionHandler.getFashionData(player,req);
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return result;
	}

}