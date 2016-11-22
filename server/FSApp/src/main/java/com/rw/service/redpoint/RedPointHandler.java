package com.rw.service.redpoint;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityRedPointManager;
import com.rwproto.RedPointServiceProtos.RedPointServiceRequest;
import com.rwproto.RedPointServiceProtos.RedPointServiceResponse;

public class RedPointHandler {
	private static RedPointHandler instance = new RedPointHandler();
	
	public static RedPointHandler getInstance(){
		return instance;
	}

	public ByteString reFreshRedPoint(Player player, RedPointServiceRequest commnreq) {
		RedPointServiceResponse.Builder response = RedPointServiceResponse.newBuilder();
		response.setRespType(commnreq.getReqType());
		int id = commnreq.getId();
		String extraInfo = commnreq.getExtraInfo();
		boolean issucce = reFreshRedPoint(player,id,extraInfo);
		response.setIsSuccess(issucce);
		return response.build().toByteString();
	} 
	

	public boolean reFreshRedPoint(Player player, int id, String extraInfo) {
		boolean issucce = false;
		RedPointType eNum = RedPointManager.getRedPointManager().getRedPointType(id);
		switch (eNum) {
		case HOME_WINDOW_ACTIVITY:
			issucce = ActivityRedPointManager.getInstance().init(player, extraInfo);
			break;
		case FORTUNE_CAT:
			issucce = ActivityRedPointManager.getInstance().init(player, extraInfo);
			break;
		case LIMIT_HERO:
			issucce = ActivityRedPointManager.getInstance().init(player, extraInfo);
			break;
		case EVIL_BAO_ARRIVE:
			issucce = ActivityRedPointManager.getInstance().init(player, extraInfo);
			break;
		default:
			break;
		}
		return issucce;
	}
}
