package com.rw.service.pve;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;

public class PveService implements FsService {
	
	@Override
	public ByteString doTask(Request request, Player player) {
		return PveHandler.getInstance().getPveInfo(player);
	}

}
