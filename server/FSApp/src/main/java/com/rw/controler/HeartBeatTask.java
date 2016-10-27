package com.rw.controler;

import com.playerdata.Player;
import com.rw.netty.ServerHandler;
import com.rw.netty.UserChannelMgr;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.RequestProtos.Request;

public class HeartBeatTask implements PlayerTask {

	private final Long sessionId;
	private final Request request;

	public HeartBeatTask(Long sessionId, Request request) {
		super();
		this.sessionId = sessionId;
		this.request = request;
	}

	@Override
	public void run(Player player) {
		if (player == null) {
			return;
		}
		if (!ServerHandler.isConnecting(sessionId)) {
			return;
		}
		player.heartBeatCheck();
		UserChannelMgr.sendSyncResponse(request.getHeader(), null, sessionId);
	}

}
