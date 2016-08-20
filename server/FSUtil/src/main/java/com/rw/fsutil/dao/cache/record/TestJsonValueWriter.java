//package com.rw.fsutil.dao.cache.record;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class TestJsonValueWriter {
//
//	public static void main(String[] args) {
//		testCopy();
//	}
//
//	private static void testCopy() {
//		JsonValueWriter w = new JsonValueWriter();
//		HashMap<Integer, Object> map = new HashMap<Integer, Object>();
//		for (int i = 0; i < 10; i++) {
//			map.put(i, new Object());
//		}
////		System.out.println(w.copyObject(map));
//		Object copy = w.copyObject(map);
//		((Map)copy).put(1, "1");
//		System.out.println(w.compareMap(null, "test", (Map)copy, map));
//		System.out.println(copy);
//	}
//
//	private static void testMap() {
//		HashMap<Object, Object> map1 = new HashMap<Object, Object>();
//		map1.put(1, 1);
//		map1.put(2, 2);
//		map1.put(3, 3);
//		map1.put(5, null);
//		map1.put(6, null);
//		HashMap<Object, Object> map2 = new HashMap<Object, Object>();
//		map2.put(1, 1);
//		map2.put(2, 20);
//		map2.put(3, null);
//		map2.put(4, null);
//		map2.put(5, null);
//		map2.put(6, 6);
//		JsonValueWriter w = new JsonValueWriter();
//		System.out.println(w.compareMap(null, "test", map1, map2));
//		System.out.println(map1);
//	}
//
//	private static void testList() {
//		// ArrayList<Object> array1 = new ArrayList<Object>();
//		// array1.add("1");
//		// array1.add("2");
//		// array1.add("3");
//		// array1.add("4");
//		// array1.add("5");
//		// array1.add("6");
//		// array1.add(null);
//		// ArrayList<Object> array2 = new ArrayList<Object>();
//		// array2.add("1");
//		// array2.add("2");
//		// array2.add("3");
//		// array2.add(null);
//		// array2.add("4");
//		// array2.add("6");
//		// array2.add(null);
//		// array2.add(null);
//		// array2.add("9");
//		// JsonValueWriter w = new JsonValueWriter();
//		// System.out.println(w.compareList(null, "test", array1, array2));
//		// System.out.println(array1);
//	}
//}
