package com.rwbase.common.timer.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.playerdata.Player;
import com.rwbase.common.timer.IPlayerOperable;

public class FSGameAllPlayerOperableDemo implements IPlayerOperable {

	private static final DateFormat _DATE_FORMATTER = new SimpleDateFormat("[MMdd HH:mm:ss.SSS]");
	
	@Override
	public boolean isInterestingOn(Player player) {
		return true;
	}

	@Override
	public void operate(Player player) {
		System.out.println(_DATE_FORMATTER.format(new Date(System.currentTimeMillis())) + " FSGameAllPlayerOperableDemo#operate, player:" + player.getUserId());
	}

}
