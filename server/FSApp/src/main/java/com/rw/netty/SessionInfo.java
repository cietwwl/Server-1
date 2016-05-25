package com.rw.netty;

public class SessionInfo {

	private final String userId; // 玩家ID
	private final long sessionId; // session唯一ID

	public SessionInfo(String userId, long sessionId) {
		this.userId = userId;
		this.sessionId = sessionId;
	}

	public String getUserId() {
		return userId;
	}

	public long getSessionId() {
		return sessionId;
	}

	@Override
	public String toString() {
		return "[" + sessionId + "," + userId + "]";
	}

}
