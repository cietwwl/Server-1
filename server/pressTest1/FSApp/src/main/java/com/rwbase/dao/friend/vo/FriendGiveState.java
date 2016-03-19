package com.rwbase.dao.friend.vo;

import com.playerdata.readonly.FriendGiveStateIF;

public class FriendGiveState implements FriendGiveStateIF {
	private String userId;
	private boolean giveState = true;
	private boolean receiveState = false;
	
	public boolean isGiveState() {
		return giveState;
	}
	public void setGiveState(boolean giveState) {
		this.giveState = giveState;
	}
	public boolean isReceiveState() {
		return receiveState;
	}
	public void setReceiveState(boolean receiveState) {
		this.receiveState = receiveState;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
