package com.rw.controler;

import com.playerdata.Player;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.SessionInfo;
import com.rw.netty.UserChannelMgr;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.RequestProtos.Request;

public class HeartBeatTask implements PlayerTask {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private final SessionInfo session;
	private final Request request;

	public HeartBeatTask(SessionInfo session, Request request) {
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
		nettyControler.sendResponse(player.getUserId(), request.getHeader(), null, sessionId);
	}

}
