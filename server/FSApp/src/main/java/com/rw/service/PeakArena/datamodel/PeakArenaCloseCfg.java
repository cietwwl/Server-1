package com.rw.service.PeakArena.datamodel;

import java.util.Calendar;

import com.common.BaseConfig;
import com.common.ListParser;

public class PeakArenaCloseCfg extends BaseConfig {
	private int key; // 关键字段
	private String startTime; // 开始
	private String endTime; // 结束
	private int startHour;
	private int startMin;
	private int endHour;
	private int endMin;

	public int getKey() {
		return key;
	}

	public boolean isCloseTime() {
		Calendar cal = Calendar.getInstance();
		int curHour = cal.get(Calendar.HOUR_OF_DAY);
		int curMin = cal.get(Calendar.MINUTE);
		return (startHour < curHour && curHour < endHour) || (curHour == startHour && curMin >= startMin)
				|| (curHour == endHour && curMin <= endMin);
	}

	public String getCloseTimeTip() {
		return closeTimeTip;
	}

	private String closeTimeTip;

	@Override
	public void ExtraInitAfterLoad() {
		int[] tmp = ListParser.ParseIntList(startTime, ":", "巅峰竞技场", "PeakArenaCloseCfg.csv", "无效时间格式");
		startHour = tmp[0];
		startMin = tmp[1];
		checkTimeFormat(startHour, startMin);
		tmp = ListParser.ParseIntList(endTime, ":", "巅峰竞技场", "PeakArenaCloseCfg.csv", "无效时间格式");
		endHour = tmp[0];
		endMin = tmp[1];
		checkTimeFormat(endHour, endMin);
		closeTimeTip = String.format("每天%s~%s期间无法进行他巅峰竞技场对战", startTime, endTime);
	}

	private void checkTimeFormat(int hour, int min) {
		if (0 <= hour && hour < 23 && 0 <= min && min < 59) {
			return;
		}
		throw new RuntimeException("无效时间格式: " + hour + ":" + min);
	}
}
