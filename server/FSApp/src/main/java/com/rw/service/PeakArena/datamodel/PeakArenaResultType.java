package com.rw.service.PeakArena.datamodel;

public enum PeakArenaResultType {

	LOSE(0),
	WIN(1),
	;
	public final int sign;
	
	private PeakArenaResultType(int pSign) {
		this.sign = pSign;
	}
}
