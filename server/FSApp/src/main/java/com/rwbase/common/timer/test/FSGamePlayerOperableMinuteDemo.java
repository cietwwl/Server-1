package com.rwbase.common.timer.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.common.timer.IPlayerGatherer;
import com.rwbase.common.timer.IPlayerOperable;

public class FSGamePlayerOperableMinuteDemo implements IPlayerOperable, IPlayerGatherer {
	
	private static final DateFormat _DATE_FORMATTER = new SimpleDateFormat("[MMdd HH:mm:ss.SSS]");

	@Override
	public List<Player> gatherPlayers() {
		return PlayerMgr.getInstance().getOnlinePlayers();
	}

	@Override
	public boolean isInterestingOn(Player player) {
		return true;
	}

	@Override
	public void operate(Player player) {
		System.out.println(_DATE_FORMATTER.format(new Date(System.currentTimeMillis())) + " FSGamePlayerOperableMinuteDemo#operate, player:" + player.getUserId() + ", " + player.getUserName());
	}

}
