package com.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.ListRankingExtension;
import com.rw.fsutil.ranking.exception.RankingCapacityNotEougthException;
import com.rw.fsutil.ranking.exception.ReplaceTargetNotExistException;
import com.rw.fsutil.ranking.exception.ReplacerAlreadyExistException;

public class RobotListRankingImpl<ExtType> implements ListRanking<String, ExtType> {

	private List<ListRankingEntry<String, ExtType>> _sourceList; // 数据
	private Map<String, Integer> _rankingIndexMap; // key=userId，value=索引，从0开始
	private int _type;

	public RobotListRankingImpl(int type, List<ListRankingEntry<String, ExtType>> sourceListP) {
		_type = type;
		_sourceList = new ArrayList<ListRankingEntry<String, ExtType>>(sourceListP);
		_rankingIndexMap = new HashMap<String, Integer>(sourceListP.size(), 1.5f);
		for (int i = 0; i < _sourceList.size(); i++) {
			ListRankingEntry<String, ExtType> entry = _sourceList.get(i);
			_rankingIndexMap.put(entry.getKey(), i);
		}
	}
	
	@Override
	public ListRankingEntry<String, ExtType> getRankingEntry(int ranking) {
		if (_sourceList.size() > ranking) {
			return _sourceList.get(ranking - 1);
		}
		return null;
	}

	@Override
	public ListRankingEntry<String, ExtType> getRankingEntry(String key) {
		Integer index = _rankingIndexMap.get(key);
		if (index != null) {
			return _sourceList.get(index);
		}
		return null;
	}

	@Override
	public List<ListRankingEntry<String, ExtType>> getRankingEntries(List<Integer> rankingList) {
		List<ListRankingEntry<String, ExtType>> list = new ArrayList<ListRankingEntry<String, ExtType>>();
		for(int i = 0; i < rankingList.size(); i++) {
			ListRankingEntry<String, ExtType> entry = this.getRankingEntry(rankingList.get(i));
			list.add(entry);
		}
		return list;
	}

	@Override
	public List<? extends ListRankingEntry<String, ExtType>> getRankingEntries(int fromRanking, int toRanking) {
		List<ListRankingEntry<String, ExtType>> list = new ArrayList<ListRankingEntry<String, ExtType>>(toRanking - fromRanking + 1);
		int endIndex = toRanking + 1;
		for (int i = fromRanking; i < endIndex; i++) {
			ListRankingEntry<String, ExtType> entry = this.getRankingEntry(i);
			list.add(entry);
		}
		return list;
	}

	@Override
	public boolean swap(String lowerKey, String higherKey) {
		throw new UnsupportedOperationException("not supported yet...");
	}

	@Override
	public boolean isFull() {
		return true;
	}

	@Override
	public ListRankingEntry<String, ExtType> replace(String key, ExtType extension, String placeKey) throws ReplacerAlreadyExistException, ReplaceTargetNotExistException {
		throw new UnsupportedOperationException("not supported yet...");
	}

	@Override
	public ListRankingEntry<String, ExtType> addLast(String key, ExtType extension) throws RankingCapacityNotEougthException {
		throw new UnsupportedOperationException("not supported yet...");
	}

	@Override
	public ListRankingEntry<String, ExtType> remove(String key) {
		throw new UnsupportedOperationException("not supported yet...");
	}

	@Override
	public List<? extends ListRankingEntry<String, ExtType>> getEntrysCopy() {
		return new ArrayList<ListRankingEntry<String,ExtType>>(_sourceList);
	}

	@Override
	public ListRankingExtension<String, ExtType> getExtension() {
		return null;
	}

	@Override
	public int getRankingSize() {
		return _sourceList.size();
	}

	@Override
	public ListRankingEntry<String, ExtType> getFirstEntry() {
		return _sourceList.get(0);
	}

	@Override
	public ListRankingEntry<String, ExtType> getLastEntry() {
		return _sourceList.get(_sourceList.size() - 1);
	}

	@Override
	public int getMaxCapacity() {
		return _sourceList.size();
	}

	@Override
	public void subimitUpdatedTask(String key) {
		
	}

	@Override
	public int getType() {
		return _type;
	}

	@Override
	public boolean contains(String key) {
		return _rankingIndexMap.containsKey(key);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
