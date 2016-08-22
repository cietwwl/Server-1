package com.playerdata.groupcompetition.util;

public enum GCEventsStatus {
	
	/**
	 * 16强
	 */
	ROUND_OF_16,
	/**
	 * 8强
	 */
	ROUND_OF_8,
	/**
	 * 4强
	 */
	QUATER,
	/**
	 * 决赛
	 */
	FINAL,
	;
	
	static {
		GCEventsStatus[] all = GCEventsStatus.values();
		GCEventsStatus status;
		int totalDays = all.length; // 每个阶段一日
		for (int i = 0; i < all.length; i++) {
			int nextIndex = i + 1;
			status = all[i];
			status.daysNeededToFinal = (--totalDays);
			if (nextIndex == all.length) {
				break;
			} else {
				status._next = all[nextIndex];
			}
		}
	}
	
	private GCEventsStatus _next;
	private int daysNeededToFinal; // 本阶段到总决赛需要多少天
	
	public boolean hasNext() {
		return _next != null;
	}
	
	public GCEventsStatus getNex() {
		return _next;
	}
	
	/**
	 * 
	 * 获取本阶段需要多少天才能到总决赛
	 * 
	 * @return
	 */
	public int getDaysNeededToFinal() {
		return daysNeededToFinal;
	}
}
