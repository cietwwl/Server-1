package com.rwbase.dao.chat.pojo;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @Author HC
 * @date 2016年12月17日 上午10:45:40
 * @desc 角色的违规记录数据
 **/

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ChatIllegalData {
	@Id
	@JsonProperty("0")
	private String userId;// 角色Id

	@JsonProperty("1")
	private int triggerTimes;// 触发禁言的次数
	@JsonProperty("2")
	private int illegalTimes;// 违规的次数

	@JsonProperty("3")
	private long lastIllegalTime;// 上次违规的时间
	@JsonProperty("4")
	private long lastNotAllowedSpeechTime;// 上次禁言的时间，NW--NotAllowed

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setTriggerTimes(int triggerTimes) {
		this.triggerTimes = triggerTimes;
	}

	public void setIllegalTimes(int illegalTimes) {
		this.illegalTimes = illegalTimes;
	}

	public void setLastIllegalTime(long lastIllegalTime) {
		this.lastIllegalTime = lastIllegalTime;
	}

	public void setLastNotAllowedSpeechTime(long lastNotAllowedSpeechTime) {
		this.lastNotAllowedSpeechTime = lastNotAllowedSpeechTime;
	}

	public String getUserId() {
		return userId;
	}

	public int getTriggerTimes() {
		return triggerTimes;
	}

	public int getIllegalTimes() {
		return illegalTimes;
	}

	public long getLastIllegalTime() {
		return lastIllegalTime;
	}

	public long getLastNotAllowedSpeechTime() {
		return lastNotAllowedSpeechTime;
	}
}