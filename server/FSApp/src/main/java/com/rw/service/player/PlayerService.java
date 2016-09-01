package com.rw.service.player;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MsgDef.Command;
import com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse;
import com.rwproto.RequestProtos.Request;

public class PlayerService implements FsService<Request, Command>{

	@Override
	public ByteString doTask(Request request, Player player) {
		PlayerLogoutResponse.Builder builder = PlayerLogoutResponse.newBuilder();
		return builder.build().toByteString();
	}

	@Override
	public Request parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Command getMsgType(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

}
