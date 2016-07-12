package com.rwbase.common.timer.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.rwbase.common.timer.FSGamePlayerOperable;

public class FSGamePlayerOperationHeavyWeightMinuteDemo implements FSGamePlayerOperable {

	private static final DateFormat _DATE_FORMATTER = new SimpleDateFormat("[MMdd HH:mm:ss.SSS]");
	
	@Override
	public boolean isInterestingOn(Player player) {
		return true;
	}

	@Override
	public void operate(Player player) {
		try {
			System.out.println(_DATE_FORMATTER.format(new Date(System.currentTimeMillis())) + "process player:" + player.getUserId() + ", " + player.getUserName() + "， 开始");
			TimeUnit.MILLISECONDS.sleep(500);
			System.out.println(_DATE_FORMATTER.format(new Date(System.currentTimeMillis())) + "process player:" + player.getUserId() + ", " + player.getUserName() + "， 结束");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
