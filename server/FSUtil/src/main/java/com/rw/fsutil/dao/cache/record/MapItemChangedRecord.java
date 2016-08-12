package com.rw.fsutil.dao.cache.record;

import java.util.List;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.CacheFactory;
import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;

public class MapItemChangedRecord implements DataLoggerRecord {

	private static final String LINE_SEPARATOR;

	static {
		LINE_SEPARATOR = CacheFactory.LINE_SEPARATOR;
	}

	private final Object key;
	private final List<Object> addList;
	private final List<Object> removeList;
	private final List<Pair<Object, Object>> updateList;

	public MapItemChangedRecord(Object key, List<Object> addList, List<Object> removeList, List<Pair<Object, Object>> updateList) {
		this.key = key;
		this.addList = addList;
		this.removeList = removeList;
		this.updateList = updateList;
	}

	@Override
	public void write(CharArrayBuffer sb) {
		if (addList != null) {
			recored(addList, "[i]", sb);
		}
		if (removeList != null) {
			recored(removeList, "[d]", sb);
		}
		if (updateList != null) {
			sb.append(LINE_SEPARATOR).append("[u]");
			int size = updateList.size();
			for (int i = 0; i < size; i++) {
				sb.append(LINE_SEPARATOR);
				Pair<Object, Object> pair = updateList.get(i);
				Object info = pair.getT2();
				sb.append("[").append(pair.getT1()).append("]");
				sb.append(info);
			}
		}
	}

	private void recored(List<Object> list, String tips, CharArrayBuffer sb) {
		sb.append(LINE_SEPARATOR);
		sb.append(tips);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			sb.append(LINE_SEPARATOR);
			Object json = list.get(i);
			sb.append(json == null ? "null" : json);
		}
	}

	@Override
	public Object getKey() {
		return key;
	}
}
