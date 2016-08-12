package com.rwbase.dao.task.pojo;

import java.util.Calendar;

import com.playerdata.Player;
import com.rwbase.dao.task.DailyStartCondition;

/**
 * 检查时间的条件
 * 
 * @author Jamaz
 *
 */
public class DailyTimeCondition implements DailyStartCondition, DailyFinishCondition {

	private final int startHour;
	private final int startMinute;
	private final int endHour;
	private final int endMinute;

	public DailyTimeCondition(String text){
		// 把原来的代码拷过来
		String[] timeArrayFinish = text.split("_");
		this.startHour = Integer.parseInt(timeArrayFinish[0].split(":")[0]);
		this.startMinute = Integer.parseInt(timeArrayFinish[0].split(":")[1]);
		this.endHour = Integer.parseInt(timeArrayFinish[1].split(":")[0]);
		this.endMinute = Integer.parseInt(timeArrayFinish[1].split(":")[1]);

	}

	@Override
	public boolean isMatchCondition(Player player, DailyActivityData data) {
		return isMatchCondition(player);
	}

	@Override
	public boolean isMatchCondition(Player player) {
		// 当前时间
		// 把原来的代码拷过来
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		if (((startHour < hour) || (startHour == hour && startMinute <= minute)) && ((endHour > hour) || (endHour == hour && endMinute > minute))) {
			return true;
		} else {
			return false;
		}
	}

}
