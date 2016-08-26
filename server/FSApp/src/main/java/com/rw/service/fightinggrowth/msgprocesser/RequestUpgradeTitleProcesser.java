package com.rw.service.fightinggrowth.msgprocesser;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.fightinggrowth.IMsgProcesser;
import com.rwproto.RequestProtos.Request;

public class RequestUpgradeTitleProcesser implements IMsgProcesser {

	@Override
	public ByteString process(Player player, Request request) {
		return ByteString.EMPTY;
	}

}
