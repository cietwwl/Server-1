package com.rw.service.player;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse;
import com.rwproto.RequestProtos.Request;

public class PlayerService implements FsService{

	@Override
	public ByteString doTask(Request request, Player player) {
		PlayerLogoutResponse.Builder builder = PlayerLogoutResponse.newBuilder();
		return builder.build().toByteString();
	}

}
