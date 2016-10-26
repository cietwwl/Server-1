package com.rw.service.PeakArena.datamodel;

public enum PeakArenaActionType {

	/**
	 * 防守
	 */
	DEFEND(0),
	/**
	 * 挑战
	 */
	CHALLENGE(1),
	;
	public final int sign;
	
	private PeakArenaActionType(int pSign) {
		this.sign = pSign;
	}
}
