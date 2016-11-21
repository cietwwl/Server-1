package com.rw.fsutil.dao.cache.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.rw.fsutil.dao.cache.trace.DataValueParserMap;

public class JsonValueWriterTest {

	public static void main(String[] args) {
		// HashMap<String, List> map = new HashMap<String, List>();
		// ArrayList<String> list = new ArrayList<String>();
		// list.add("1");
		// map.put("1", list);
		//
		// HashMap<String, List> map2 = new HashMap<String, List>();
		// ArrayList<String> list2 = new ArrayList<String>();
		// list2.add("1");
		// list2.add("2");
		// map2.put("1", list2);
		// System.out.println(new JsonValueWriter().compareSetDiff(null, "set",
		// map, map2));
		// System.out.println(map);
		// System.out.println(map2);

		// map
		// HashMap<String, Map> map = new HashMap<String, Map>();
		// HashMap<String, String> map1 = new HashMap<String, String>();
		// map1.put("1", "1");
		// map.put("a", map1);
		//
		// HashMap<String, Map> map_ = new HashMap<String, Map>();
		// HashMap<String, String> map2 = new HashMap<String, String>();
		// map2.put("1", "2");
		// map_.put("a", map2);
		// System.out.println(new JsonValueWriter().compareSetDiff(null, "set", map, map_));
		// System.out.println(map);
		// System.out.println(map_);

		DataValueParserMap.init(Collections.EMPTY_MAP);

		ArrayList l1 = new ArrayList();
		l1.add(1);

		ArrayList l2 = new ArrayList();
		l2.add(1);
		l2.add(2);

		HashMap map1 = new HashMap();
		map1.put(1, 1);
		map1.put(2, 2);

		HashMap map2 = new HashMap();
		// map2.put(1, 1);
		map2.put(2, 2);
		map2.put(3, 3);
		// list
		ArrayList list1 = new ArrayList();
		list1.add(1);
		list1.add(map1);
		list1.add(l1);

		ArrayList list2 = new ArrayList();
		list2.add(1);
		// list2.add(3);
		// list2.add(9);
		// list2.add(5);
		list2.add(map2);
		list2.add(l2);
		System.out.println(list1);
		System.out.println(list2);
		System.out.println(new JsonValueWriter().compareSetDiff(null, "set", list1, list2));
		System.out.println(list1);
		System.out.println(list2);
	}
}
