package com.rw.controler;

import com.playerdata.Player;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.RequestProtos.RequestHeader;

public class PlayerLoginTask implements PlayerTask {

	private final Long sessionId;
	private final GameLoginRequest request;
	private final RequestHeader header;
	private final boolean savePlot;
	private final long submitTime;

	public PlayerLoginTask(Long sessionId, RequestHeader header, GameLoginRequest request, boolean savePlot) {
		this(sessionId, header, request, savePlot, System.currentTimeMillis());
	}

	public PlayerLoginTask(Long sessionId, RequestHeader header, GameLoginRequest request, boolean savePlot, long submitTime) {
		this.sessionId = sessionId;
		this.header = header;
		this.request = request;
		this.savePlot = savePlot;
		this.submitTime = submitTime;
	}

	@Override
	public void run(Player player) {
		PlayerLoginHandler.getHandler().run(player, sessionId, header, request, savePlot, submitTime);
	}

}
