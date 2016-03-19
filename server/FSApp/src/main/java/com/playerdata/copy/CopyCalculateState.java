package com.playerdata.copy;

import com.rwproto.CopyServiceProtos.MsgCopyResponse;

/**
 * 存储服务器结算状态
 * @author Jamaz
 *
 */
public class CopyCalculateState {

	private final int lastBattleId;
	private volatile MsgCopyResponse.Builder lastCopyResponse;

	public CopyCalculateState(int lastBattleId){
		this.lastBattleId = lastBattleId;
	}
	
	public int getLastBattleId() {
		return lastBattleId;
	}

	public MsgCopyResponse.Builder getLastCopyResponse() {
		return lastCopyResponse;
	}

	public void setLastCopyResponse(MsgCopyResponse.Builder lastCopyResponse) {
		this.lastCopyResponse = lastCopyResponse;
	}

}
