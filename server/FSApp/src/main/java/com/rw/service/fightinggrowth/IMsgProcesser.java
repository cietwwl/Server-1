package com.rw.service.fightinggrowth;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rwproto.RequestProtos.Request;

public interface IMsgProcesser {

	/**
	 * 
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString process(Player player, Request request);
}
