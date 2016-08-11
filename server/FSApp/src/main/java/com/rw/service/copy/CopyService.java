package com.rw.service.copy;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.RequestProtos.Request;

public class CopyService implements FsService{

	private CopyHandler copyHandler = CopyHandler.getInstance();
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MsgCopyRequest copyRequest = MsgCopyRequest.parseFrom(request.getBody().getSerializedContent());
			ERequestType copyServiceType = copyRequest.getRequestType();
			switch (copyServiceType)
			{
			case BUY_LEVEL:
				result = copyHandler.buyLevel(copyRequest, player);
				break;
			case BATTLE_CLEARING:
				result = copyHandler.battleClear(player, copyRequest);
				break;
			case BATTLE_ITEMS_BACK:
				result = copyHandler.battleItemsBack(player, copyRequest);
				break;
			case SWEEP_LEVEL_TICKET:
			case SWEEP_LEVEL_DIAMOND:
				result = copyHandler.sweep(player, copyRequest);
				break;
			case GET_GIFT:
				result = copyHandler.getMapGift(player, copyRequest);
				break;
			case Map_Animation:
				result = copyHandler.updateMapAnimation(player, copyRequest);
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
