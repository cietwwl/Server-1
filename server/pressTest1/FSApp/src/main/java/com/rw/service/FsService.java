package com.rw.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwbase.dao.user.UserGameData;
import com.rwproto.RequestProtos.Request;


public interface FsService {
	
	public ByteString doTask(Request request, Player player);
	
}
