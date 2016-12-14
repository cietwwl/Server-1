package com.rw.fsutil.dao.optimize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.rw.fsutil.common.LongPairValue;
import com.rw.fsutil.common.PairValue;
import com.rw.fsutil.dao.cache.CacheFactory;

public class CacheStackTraceRecord {

	private ConcurrentHashMap<String, Long> map;
	private final String path;
	private final String name;

	public CacheStackTraceRecord(String path, String name) {
		this.path = path;
		this.name = name;
	}

	public void record() {
		boolean first = false;
		ArrayList<LongPairValue<LongPairValue<String>>> results = new ArrayList<LongPairValue<LongPairValue<String>>>();
		ArrayList<LongPairValue<String>> evictList = new ArrayList<LongPairValue<String>>();
		ConcurrentHashMap<String, Long> map = this.map;
		if (map == null) {
			map = new ConcurrentHashMap<String, Long>(1024, 1.0f, 1);
			this.map = map;
			first = true;
		} else if (map.isEmpty()) {
			first = true;
		}
		List<PairValue<String, AtomicLong>> list = CacheFactory.getStackTrace();
		for (PairValue<String, AtomicLong> pair : list) {
			String key = pair.firstValue;
			Long lastValue = map.get(key);
			long current = pair.secondValue.get();
			long oldValue;
			if (lastValue != null) {
				oldValue = lastValue.longValue();
			} else {
				oldValue = 0;
			}
			if (oldValue == current) {
				continue;
			}
			map.put(key, current);
			long diff = current - oldValue;
			LongPairValue<String> value = new LongPairValue<String>(key, current);
			LongPairValue<LongPairValue<String>> result = new LongPairValue<LongPairValue<String>>(value, diff);
			results.add(result);
		}
		if (map.size() != list.size()) {
			HashSet<String> set = new HashSet<String>();
			for (PairValue<String, AtomicLong> p : list) {
				set.add(p.firstValue);
			}
			for (Map.Entry<String, Long> entry : map.entrySet()) {
				String key = entry.getKey();
				if (!set.contains(key)) {
					evictList.add(new LongPairValue<String>(key, entry.getValue()));
				}
			}
		}

		if (results.isEmpty() && evictList.isEmpty()) {
			return;
		}
		Collections.sort(results, new Comparator<LongPairValue<LongPairValue<String>>>() {

			@Override
			public int compare(LongPairValue<LongPairValue<String>> o1, LongPairValue<LongPairValue<String>> o2) {
				long r = o1.value - o2.value;
				return r < 0 ? 1 : -1;
			}
		});

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat Hourformatter = new SimpleDateFormat("HH:mm:ss");
		Date d = new Date();
		String postfix = formatter.format(d);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(this.path + "/" + this.name + "/" + postfix), true));
			writer.write("------------------------" + Hourformatter.format(d) + (first ? "(first)" : "") + "------------------------");
			writer.newLine();
			int count = 0;
			for (int i = 0, size = results.size(); i < size; i++) {
				LongPairValue<LongPairValue<String>> pair = results.get(i);
				LongPairValue<String> subPair = pair.t;
				StringBuilder sb = new StringBuilder();
				sb.append(subPair.t).append('=').append(pair.value).append('(').append(subPair.value).append(')');
				if (++count % 10 == 0) {
					sb.append(CacheFactory.LINE_SEPARATOR);
				} else {
					sb.append("; ");
				}
				writer.write(sb.toString());
			}
			if (!evictList.isEmpty()) {
				writer.write("-----------evicted-------------");
				count = 0;
				for (int i = 0, size = evictList.size(); i < size; i++) {
					LongPairValue<String> pair = evictList.get(i);
					StringBuilder sb = new StringBuilder();
					sb.append(pair.t).append('=').append(pair.value);
					if (++count % 10 == 0) {
						sb.append(CacheFactory.LINE_SEPARATOR);
					} else {
						sb.append("; ");
					}
				}
			}
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
}
