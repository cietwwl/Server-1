package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropMissingRecordImpl implements IDropMissingRecord {

	private Map<String, List<Integer>> recordMap = new HashMap<String, List<Integer>>();
	
	@Override
	public boolean containsRecord(String heroId, int itemId) {
		List<Integer> list = recordMap.get(heroId);
		if (list == null) {
			return false;
		} else {
			return list.contains(itemId);
		}
	}

	@Override
	public void addToRecord(String heroId, int itemId) {
		List<Integer> list = recordMap.get(heroId);
		if(list == null) {
			list = new ArrayList<Integer>();
			recordMap.put(heroId, list);
		}
		list.add(itemId);
	}

}
