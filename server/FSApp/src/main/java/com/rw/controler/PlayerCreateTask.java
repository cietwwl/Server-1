package com.rw.controler;

import com.rw.fsutil.cacheDao.IdentityIdGenerator;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.RequestProtos.RequestHeader;

public class PlayerCreateTask implements Runnable {

	private final GameLoginRequest request;
	private final RequestHeader header;
	private final Long sessionId;
	private final IdentityIdGenerator generator;
	private final long submitTime;

	public PlayerCreateTask(GameLoginRequest request, RequestHeader header, Long sessionId, IdentityIdGenerator generator) {
		super();
		this.request = request;
		this.header = header;
		this.sessionId = sessionId;
		this.generator = generator;
		this.submitTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		PlayerCreateHandler.getInstance().run(request, header, sessionId, generator, submitTime);
	}

}
