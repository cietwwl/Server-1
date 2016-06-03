package com.rw.service.friend.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class MappedParser implements PlusParser {

	@Override
	public  PlusCalculator parse(String text) {
		String[] firstArray = split(text, ";");
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0, len = firstArray.length; i < len; i++) {
			String segment = firstArray[i];
			String[] segmentArray = split(segment, "=");
			Integer value = Integer.parseInt(segmentArray[1]);
			ArrayList<Integer> list = parse(segmentArray[0], "~");
			for (int j = list.size(); --j >= 0;) {
				map.put(list.get(i), value);
			}
		}
		return new IntMappingCalculator(map);
	}

	private ArrayList<Integer> parse(String text, String split) {
		if (!text.contains(split)) {
			ArrayList<Integer> list = new ArrayList<Integer>(1);
			list.add(Integer.parseInt(text));
			return list;
		}
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (String key : split(text, split)) {
			list.add(Integer.parseInt(key));
		}
		return list;
	}

	private String[] split(String text, String split) {
		StringTokenizer token = new StringTokenizer(text, split);
		String[] array = new String[token.countTokens()];
		int count = 0;
		while (token.hasMoreElements()) {
			array[count++] = token.nextToken();
		}
		return array;
	}

}
