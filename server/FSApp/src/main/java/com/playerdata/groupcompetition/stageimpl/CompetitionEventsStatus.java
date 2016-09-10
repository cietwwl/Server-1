package com.playerdata.groupcompetition.stageimpl;

public enum CompetitionEventsStatus {
	
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
		CompetitionEventsStatus[] all = CompetitionEventsStatus.values();
		for (int i = 0; i < all.length; i++) {
			int nextIndex = i + 1;
			if (nextIndex == all.length) {
				break;
			} else {
				all[i]._next = all[nextIndex];
			}
		}
	}
	
	private CompetitionEventsStatus _next;
	
	public boolean hasNext() {
		return _next != null;
	}
	
	public CompetitionEventsStatus getNex() {
		return _next;
	}
}
