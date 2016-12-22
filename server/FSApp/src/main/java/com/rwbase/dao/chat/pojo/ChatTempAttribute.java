package com.rwbase.dao.chat.pojo;

/**
 * @Author HC
 * @date 2016年12月17日 上午11:03:11
 * @desc
 **/

public class ChatTempAttribute {
	private volatile long lastWorldSpeechTime;// 上次世界发言的时间
	private volatile long lastGroupSpeechTime;// 上一次帮派发言的时间
	private volatile long lastTeamSpeechTime;// 上一次私聊发言的时间
	private volatile String lastWorldMsg;// 上一次发言的消息
	private int msgRepeatedTimes;// 消息重复的次数

	// ============================================================GET区域
	public long getLastWorldSpeechTime() {
		return lastWorldSpeechTime;
	}

	public long getLastGroupSpeechTime() {
		return lastGroupSpeechTime;
	}

	public long getLastTeamSpeechTime() {
		return lastTeamSpeechTime;
	}

	public String getLastWorldMsg() {
		return lastWorldMsg;
	}

	public int getMsgRepeatedTimes() {
		return msgRepeatedTimes;
	}

	// ============================================================SET区域
	public void setLastWorldSpeechTime(long lastWorldSpeechTime) {
		this.lastWorldSpeechTime = lastWorldSpeechTime;
	}

	public void setLastGroupSpeechTime(long lastGroupSpeechTime) {
		this.lastGroupSpeechTime = lastGroupSpeechTime;
	}

	public void setLastTeamSpeechTime(long lastTeamSpeechTime) {
		this.lastTeamSpeechTime = lastTeamSpeechTime;
	}

	public void setLastWorldMsg(String lastWorldMsg) {
		this.lastWorldMsg = lastWorldMsg;
	}

	public void setMsgRepeatedTimes(int msgRepeatedTimes) {
		this.msgRepeatedTimes = msgRepeatedTimes;
	}
}