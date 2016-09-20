package com.playerdata.groupcompetition.util;

public enum GCEventsType {
	
	/**
	 * 16强
	 */
	TOP_16(1),
	/**
	 * 8强
	 */
	TOP_8(2),
	/**
	 * 4强
	 */
	QUATER(3),
	/**
	 * 决赛
	 */
	FINAL(4),
	;
	
	static {
		GCEventsType[] all = GCEventsType.values();
		GCEventsType status;
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
	
	/**
	 * 类型的数字标记
	 */
	public final int sign;
	private GCEventsType _next;
	private int daysNeededToFinal; // 本阶段到总决赛需要多少天
	
	private GCEventsType(int pSign) {
		this.sign = pSign;
	}
	
	public boolean hasNext() {
		return _next != null;
	}
	
	public GCEventsType getNext() {
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
