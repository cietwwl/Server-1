package com.rw.service.copy;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.RequestProtos.Request;

public class CopyService implements FsService<MsgCopyRequest, ERequestType>{

	private CopyHandler copyHandler = CopyHandler.getInstance();
	
	@Override
	public ByteString doTask(MsgCopyRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			ERequestType copyServiceType = request.getRequestType();
			switch (copyServiceType)
			{
			case BUY_LEVEL:
				result = copyHandler.buyLevel(request, player);
				break;
			case BATTLE_CLEARING:
				result = copyHandler.battleClear(player, request);
				break;
			case BATTLE_ITEMS_BACK:
				result = copyHandler.battleItemsBack(player, request);
				break;
			case SWEEP_LEVEL_TICKET:
			case SWEEP_LEVEL_DIAMOND:
				result = copyHandler.sweep(player, request);
				break;
			case GET_GIFT:
				result = copyHandler.getMapGift(player, request);
				break;
			case Map_Animation:
				result = copyHandler.updateMapAnimation(player, request);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public MsgCopyRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgCopyRequest copyRequest = MsgCopyRequest.parseFrom(request.getBody().getSerializedContent());
		return copyRequest;
	}
	@Override
	public ERequestType getMsgType(MsgCopyRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}

}
