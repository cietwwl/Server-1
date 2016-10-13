package com.rw.handler.groupCompetition.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum GCEventsType {
	
	/**
	 * 16强
	 */
	TOP_16(1, "16强"),
	/**
	 * 8强
	 */
	TOP_8(2, "8强"),
	/**
	 * 4强
	 */
	QUATER(3, "4强"),
	/**
	 * 决赛
	 */
	FINAL(4, "决赛"),
	;
	
	private static final Map<Integer, GCEventsType> _all;
	static {
		GCEventsType[] all = GCEventsType.values();
		GCEventsType status;
		GCEventsType pre = null;
		int totalDays = all.length; // 每个阶段一日
		Map<Integer, GCEventsType> map = new HashMap<Integer, GCEventsType>();
		for (int i = 0; i < all.length; i++) {
			int nextIndex = i + 1;
			status = all[i];
			map.put(status.sign, status);
			if(pre != null) {
				status._pre = pre;
			}
			status.daysNeededToFinal = (--totalDays);
			if (nextIndex == all.length) {
				break;
			} else {
				status._next = all[nextIndex];
			}
			pre = status;
		}
		_all = Collections.unmodifiableMap(map);
	}
	
	/**
	 * 类型的数字标记
	 */
	public final int sign;
	public final String chineseName;
	private GCEventsType _next;
	private GCEventsType _pre;
	private int daysNeededToFinal; // 本阶段到总决赛需要多少天
	
	private GCEventsType(int pSign, String chineseName) {
		this.sign = pSign;
		this.chineseName = chineseName;
	}
	
	public boolean hasNext() {
		return _next != null;
	}
	
	public GCEventsType getNext() {
		return _next;
	}
	
	public GCEventsType getPre() {
		return _pre;
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
	
	public static GCEventsType getBySign(int pSign) {
		return _all.get(pSign);
	}
}
