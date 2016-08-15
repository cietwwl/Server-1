package com.rw.netty;


public class UserSession {

	private final String userId; // 玩家ID
	private final long sessionId; // session唯一ID

	public UserSession(String userId, long sessionId) {
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
