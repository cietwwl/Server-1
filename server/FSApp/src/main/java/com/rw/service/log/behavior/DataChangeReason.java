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
	private String mapId;

	public DataChangeReason(Player player, String eventTypeFirst, String eventTypeSecond, String currentViewId, String mapId) {
		super();
		this.player = player;
		this.eventTypeFirst = eventTypeFirst;
		this.eventTypeSecond = eventTypeSecond;
		this.currentViewId = currentViewId;
		this.mapId = mapId;
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

	public String getMapId() {
		return mapId;
	}
}
