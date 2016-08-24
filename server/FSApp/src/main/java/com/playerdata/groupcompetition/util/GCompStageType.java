package com.playerdata.groupcompetition.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum GCompStageType {

	SELECTION(1),
	EVENTS(2),
	REST(3),
	EMPTY(4),
	;
	private static final Map<Integer, GCompStageType> _all;
	
	static {
		GCompStageType[] array = values();
		Map<Integer, GCompStageType> map = new HashMap<Integer, GCompStageType>(array.length + 1, 1.0f);
		for(int i = 0, length = array.length; i < length; i++) {
			GCompStageType temp = array[i];
			map.put(temp.sign, temp);
		}
		_all = Collections.unmodifiableMap(map);
	}
	
	public final int sign;

	private GCompStageType(int sign) {
		this.sign = sign;
	}
	
	public static final GCompStageType getBySign(int sign) {
		return _all.get(sign);
	}
}
