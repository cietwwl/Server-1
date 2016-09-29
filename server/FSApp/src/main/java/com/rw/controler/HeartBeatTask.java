package com.rw.controler;

import com.playerdata.Player;
import com.rw.netty.UserChannelMgr;
import com.rw.netty.UserSession;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.RequestProtos.Request;

public class HeartBeatTask implements PlayerTask {

	private final UserSession session;
	private final Request request;

	public HeartBeatTask(UserSession session, Request request) {
		super();
		this.session = session;
		this.request = request;
	}

	@Override
	public void run(Player player) {
		if (player == null) {
			return;
		}
		long sessionId = session.getSessionId();
		if (sessionId != UserChannelMgr.getCurrentSessionId(session.getUserId())) {
			return;
		}
		player.heartBeatCheck();
		UserChannelMgr.sendResponse(player.getUserId(), request.getHeader(), null, sessionId);
	}

}
