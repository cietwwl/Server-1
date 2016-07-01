package com.playerdata.groupFightOnline.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;


public class GFightOnlineService implements FsService {

	@Override
	public ByteString doTask(Request request, Player player) {
		
		GFightOnlineHandler handler = GFightOnlineHandler.getInstance();
		ByteString byteString = null;
		
		return byteString;
	}
}