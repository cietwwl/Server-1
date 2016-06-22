package com.rw.service.friend.datamodel;

import java.util.HashMap;
import java.util.Map;

public class IntMappingCalculator implements PlusCalculator {

	private HashMap<Integer, Integer> map;

	public IntMappingCalculator(Map<Integer, Integer> map) {
		this.map = new HashMap<Integer, Integer>(map);
	}

	@Override
	public int calcutePlus(Integer key) {
		Integer value = map.get(key);
		return value == null ? 0 : value;
	}
	
	@Override
	public String toString(){
		return map.toString();
	}

}
