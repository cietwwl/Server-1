package com.rw.fsutil.dao.cache.trace;

import java.util.List;
import java.util.Map;
import com.rw.fsutil.common.Pair;

/**
 * MapItem改变事件
 * @author Jamaz
 *
 * @param <V>
 */
public class MapItemChangedEvent<V> implements DataChangedEvent<MapItemChangedListener<V>> {
				//List<V>
	private final List<Pair<String,V>> addList;		//新增元素的列表
	private final List<Pair<String,V>> removeList;	//删除元素的列表
	private final Map<String,Pair<V, V>> changedMap;	//可能发生改变的集合

	public MapItemChangedEvent(List<Pair<String,V>> addList, List<Pair<String,V>> removeList, Map<String,Pair<V, V>> changedList) {
		super();
		this.addList = addList;
		this.removeList = removeList;
		this.changedMap = changedList;
	}

	@Override
	public void accept(MapItemChangedListener<V> listener) {
		listener.notifyDataChanged(this);
	}

	public List<Pair<String,V>> getAddList() {
		return addList;
	}

	public List<Pair<String,V>> getRemoveList() {
		return removeList;
	}

	public Map<String,Pair<V, V>> getChangedMap() {
		return changedMap;
	}
}
