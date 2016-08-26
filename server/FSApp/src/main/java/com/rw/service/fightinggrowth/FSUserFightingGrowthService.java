package com.rw.service.fightinggrowth;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;

public class FSUserFightingGrowthService implements FsService {

	private static final Map<Integer, IMsgProcesser> _processers = new HashMap<Integer, IMsgProcesser>();
	
	@Override
	public ByteString doTask(Request request, Player player) {
		return _processers.get(request.getHeader().getCommand().getNumber()).process(player, request);
	}

}
