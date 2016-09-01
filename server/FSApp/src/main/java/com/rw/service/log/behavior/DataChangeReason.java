package com.rw.service.log.behavior;

import com.playerdata.Player;

/**
 * 物品变动原因
 * @author lida
 *
 */
public class DataChangeReason {
	private Player player;
	private String eventTypeFirst;
	private String eventTypeSecond;
	
	private String currentViewId;

	public DataChangeReason(Player player, String eventTypeFirst, String eventTypeSecond, String currentViewId) {
		super();
		this.player = player;
		this.eventTypeFirst = eventTypeFirst;
		this.eventTypeSecond = eventTypeSecond;
		this.currentViewId = currentViewId;
	}

	public Player getPlayer() {
		return player;
	}

	public String getEventTypeFirst() {
		return eventTypeFirst;
	}

	public String getEventTypeSecond() {
		return eventTypeSecond;
	}

	public String getCurrentViewId() {
		return currentViewId;
	}
}
