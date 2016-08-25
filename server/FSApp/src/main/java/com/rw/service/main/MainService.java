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

public class MainService implements FsService<MsgMainRequest, EMainServiceType> {

	private MainHandler mainHandler = MainHandler.getInstance();

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long time = 1462186979022l;
		String format = sdf.format(new Date(time));
		System.err.println(format);
	}

	@Override
	public ByteString doTask(MsgMainRequest request, Player pPlayer) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			EMainServiceType requestType = request.getRequestType();
			switch (requestType) {
			case GET_MAIN:
				result = mainHandler.index(request, pPlayer);
				break;
			case TO_CONTINUOUS_BUY_COIN:
				result = mainHandler.toContinuousBuyCoin(request, pPlayer);
				break;
			case CONTINUOUS_BUY_COIN:
				result = mainHandler.continuousBuyCoin(request, pPlayer);
				break;
			case BUY_COIN:
				result = mainHandler.buyCoin(request, pPlayer);
				break;
			case TO_BUY_POWER:
				result = mainHandler.toBuyPower(request, pPlayer);
				break;
			case BUY_POWER:
				result = mainHandler.buyPower(request, pPlayer);
				break;
			case GET_POWER_INFO:
				result = mainHandler.getPowerInfo(request, pPlayer);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public MsgMainRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgMainRequest mainRequest = MsgMainRequest.parseFrom(request.getBody().getSerializedContent());
		return mainRequest;
	}

	@Override
	public EMainServiceType getMsgType(MsgMainRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}

}
