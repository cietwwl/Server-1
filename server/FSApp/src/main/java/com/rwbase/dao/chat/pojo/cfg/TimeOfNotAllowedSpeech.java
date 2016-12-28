package com.rwbase.dao.chat.pojo.cfg;

/**
 * @Author HC
 * @date 2016年12月17日 上午9:52:10
 * @desc 禁言时间
 **/

public class TimeOfNotAllowedSpeech {
	private int times;// 违规次数
	private int vipLevel;// vip等级
	private int time;// 禁言时间

	public int getTimes() {
		return times;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public int getTime() {
		return time;
	}
}