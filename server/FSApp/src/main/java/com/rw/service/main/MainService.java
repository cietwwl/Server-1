package com.rw.service.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MainServiceProtos.EMainServiceType;
import com.rwproto.MainServiceProtos.MsgMainRequest;
import com.rwproto.RequestProtos.Request;

public class MainService implements FsService {

	// private static MainService instance = new MainService();
	private MainHandler mainHandler = MainHandler.getInstance();

	// private MainService(){}
	// public static MainService getInstance(){
	// return instance;
	// }
	public ByteString doTask(Request request, Player pPlayer) {
		ByteString result = null;
		try {
			MsgMainRequest mainRequest = MsgMainRequest.parseFrom(request.getBody().getSerializedContent());
			EMainServiceType requestType = mainRequest.getRequestType();
			switch (requestType) {
			case GET_MAIN:
				result = mainHandler.index(mainRequest, pPlayer);
				break;
			case TO_CONTINUOUS_BUY_COIN:
				result = mainHandler.toContinuousBuyCoin(mainRequest, pPlayer);
				break;
			case CONTINUOUS_BUY_COIN:
				result = mainHandler.continuousBuyCoin(mainRequest, pPlayer);
				break;
			case BUY_COIN:
				result = mainHandler.buyCoin(mainRequest, pPlayer);
				break;
			case TO_BUY_POWER:
				result = mainHandler.toBuyPower(mainRequest, pPlayer);
				break;
			case BUY_POWER:
				result = mainHandler.buyPower(mainRequest, pPlayer);
				break;
			case GET_POWER_INFO:
				result = mainHandler.getPowerInfo(mainRequest, pPlayer);
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long time = 1462186979022l;
		String format = sdf.format(new Date(time));
		System.err.println(format);
	}

}
