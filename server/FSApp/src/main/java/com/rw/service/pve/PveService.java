package com.rw.service.pve;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;

public class PveService implements FsService<Request, Command> {
	
	@Override
	public ByteString doTask(Request request, Player player) {
		return PveHandler.getInstance().getPveInfo(player);
	}

	@Override
	public Request parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		return request;
	}

	@Override
	public Command getMsgType(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

}
