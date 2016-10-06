package com.rw.service.targetSell;

import com.bm.targetSell.TargetSellManager;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TargetSellProto.RequestType;
import com.rwproto.TargetSellProto.TargetSellReqMsg;

public class TargetSellService implements FsService<TargetSellReqMsg, RequestType>{

	@Override
	public ByteString doTask(TargetSellReqMsg request, Player player) {
		ByteString bstr = null;
		try {
			
			TargetSellManager manager = TargetSellManager.getInstance();
			RequestType msgType = getMsgType(request);
			switch (msgType) {
			case GET_ITEM:
				bstr = manager.roleGetItem(player,request.getItemGroupId());
				break;
			case CHARGE_ITEM:
				bstr = manager.roleChargeItem(player,request);
				break;
			default:
				GameLog.error("TargetSell","TargetSellService[doTask]","接收到一个Unknow的消息，无法处理", null);
				break;
			}
		} catch (Exception e) {
			GameLog.error("TargetSell","TargetSellService[doTask]","出现了Exception异常", e);
		}
		return bstr;
		
	}

	@Override
	public TargetSellReqMsg parseMsg(Request request)
			throws InvalidProtocolBufferException {
		return TargetSellReqMsg.parseFrom(request.getBody().getSerializedContent());
	}

	@Override
	public RequestType getMsgType(TargetSellReqMsg request) {
		return request.getReqType();
	}

}
