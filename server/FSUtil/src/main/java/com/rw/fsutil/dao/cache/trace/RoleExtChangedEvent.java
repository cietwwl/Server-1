package com.rw.fsutil.dao.cache.trace;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.common.Pair;

public class RoleExtChangedEvent<V> implements DataChangedEvent<RoleExtChangedListener<V>> {

	private final List<Pair<Integer, V>> addList; // 新增元素的列表
	private final List<Pair<Integer, V>> removeList; // 删除元素的列表
	private final Map<Integer, Pair<V, V>> changedMap; // 可能发生改变的集合

	public RoleExtChangedEvent(List<Pair<Integer, V>> addList, List<Pair<Integer, V>> removeList, Map<Integer, Pair<V, V>> changedMap) {
		super();
		this.addList = addList;
		this.removeList = removeList;
		this.changedMap = changedMap;
	}

	@Override
	public void accept(RoleExtChangedListener<V> visitor) {
		visitor.notifyDataChanged(this);
	}

	public List<Pair<Integer, V>> getAddList() {
		return addList;
	}

	public List<Pair<Integer, V>> getRemoveList() {
		return removeList;
	}

	public Map<Integer, Pair<V, V>> getChangedMap() {
		return changedMap;
	}
}
