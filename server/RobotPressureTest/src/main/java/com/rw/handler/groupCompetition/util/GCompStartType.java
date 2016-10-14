package com.rw.handler.groupCompetition.util;

public enum GCompStartType {

	SERVER_TIME_OFFSET(1),
	NUTRAL_TIME_OFFSET(2),
	;
	public final int sign;
	private GCompStartType(int sign) {
		this.sign = sign;
	}
	
	public static GCompStartType getBySign(int sign) {
		return sign == SERVER_TIME_OFFSET.sign ? SERVER_TIME_OFFSET : NUTRAL_TIME_OFFSET;
	}
}
