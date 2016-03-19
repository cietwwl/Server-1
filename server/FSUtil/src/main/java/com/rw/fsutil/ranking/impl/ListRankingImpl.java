package com.rw.fsutil.ranking.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.rw.fsutil.log.EngineLoggerImpl;
import com.rw.fsutil.ranking.DataUpdater;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.ListRankingExtension;
import com.rw.fsutil.ranking.exception.RankingCapacityNotEougthException;
import com.rw.fsutil.ranking.exception.ReplaceTargetNotExistException;
import com.rw.fsutil.ranking.exception.ReplacerAlreadyExistException;

/**
 * 列表排行榜的实现
 * 
 * @author Jamaz
 *
 * @param <K>
 * @param <E>
 */

public class ListRankingImpl<K, E> implements ListRanking<K, E> {

	private EngineLoggerImpl logger = new EngineLoggerImpl("ranking");
	private final int maxCapacity;
	private final int type;
	private volatile int size;
	private MomentSRankingEntryImpl<K, E>[] rankArray;
	private ConcurrentHashMap<K, MomentSRankingEntryImpl<K, E>> rankingMap;
	private final ReadLock readLock;
	private final WriteLock writeLcok;
	private final DataUpdater dataUpdater;
	private final ListRankingExtension<K, E> rankingExtension;

	public ListRankingImpl(int type, int maxCapacity, int period, ListRankingExtension<K, E> rankingExtension, ScheduledExecutorService executor) {
		this.rankingExtension = rankingExtension;
		this.type = type;
		this.maxCapacity = maxCapacity;
		ReentrantReadWriteLock rankMapRwLock = new ReentrantReadWriteLock();
		this.readLock = rankMapRwLock.readLock();
		this.writeLcok = rankMapRwLock.writeLock();
		this.dataUpdater = new DataUpdater(TimeUnit.MINUTES.toSeconds(period), executor) {

			@Override
			public boolean notifyDataUpdated() {
				 return updateToDB();
			}

		};
		initRank();
	}

	/** 排行榜的初始化 **/
	private void initRank() {
		List<ListRankingEntryData> dataList = RankingDataManager.getSRankingEntryData(type);
		ConcurrentHashMap<K, MomentSRankingEntryImpl<K, E>> ownerIdMapTmp = new ConcurrentHashMap<K, MomentSRankingEntryImpl<K, E>>();
		MomentSRankingEntryImpl<K, E>[] rankArrayTemp = new MomentSRankingEntryImpl[maxCapacity + 1];
		boolean submit = false;
		TreeMap<Integer, MomentSRankingEntryImpl<K, E>> treeMap = new TreeMap<Integer, MomentSRankingEntryImpl<K, E>>();
		// 重复主键的检查
		for (ListRankingEntryData entryData : dataList) {
			K key = rankingExtension.decodeKey(entryData.getKey());
			E ext = rankingExtension.decodeExtension(entryData.getExtension());
			int ranking = entryData.getRanking();
			MomentSRankingEntryImpl<K, E> momentEntry = new MomentSRankingEntryImpl<K, E>(ranking, key, ext);
			ownerIdMapTmp.put(key, momentEntry);
			rankArrayTemp[ranking] = momentEntry;
			MomentSRankingEntryImpl<K, E> old = treeMap.put(ranking, momentEntry);
			if (old != null) {
				logger.error("重复排名导致排行榜条目丢失：" + ranking + "," + entryData.getKey() + "," + entryData.getExtension());
			}
			submit = true;
		}

		// 对排名不连续的处理
		if (!treeMap.isEmpty() && treeMap.lastKey() != treeMap.size()) {
			logger.error("排行榜条目不连续：last = " + treeMap.size() + ",size = " + treeMap.size());
			// 对排名进行调整
			rankArrayTemp = new MomentSRankingEntryImpl[maxCapacity + 1];
			ownerIdMapTmp = new ConcurrentHashMap<K, MomentSRankingEntryImpl<K, E>>();
			int ranking = 0;
			for (MomentSRankingEntryImpl<K, E> entry : treeMap.values()) {
				MomentSRankingEntryImpl<K, E> newEntry = new MomentSRankingEntryImpl<K, E>(++ranking, entry.getKey(), entry.getExtension());
				rankArrayTemp[newEntry.getRanking()] = newEntry;
				ownerIdMapTmp.put(newEntry.getKey(), newEntry);
			}
			submit = true;
		}
		writeLcok.lock();
		try {
			rankArray = rankArrayTemp;
			rankingMap = ownerIdMapTmp;
			this.size = rankingMap.size();
		} finally {
			writeLcok.unlock();
		}
		if (submit) {
			// 通知异步更新
			dataUpdater.submitUpdateTask();
		}
	}

	/* 同步所有排行榜条目到数据库 */
	public boolean updateToDB() {
		MomentSRankingEntryImpl<K, E>[] rankArray = getArrayCopy();
		int length = rankArray.length;
		ArrayList<ListRankingEntryData> list = new ArrayList<ListRankingEntryData>(length);
		for (int i = 0; i < length; i++) {
			MomentSRankingEntryImpl<K, E> entry = rankArray[i];
			try {
				ListRankingEntryData data = new ListRankingEntryData(rankingExtension.encodeKey(entry.getKey()), type, entry.getRanking(), rankingExtension.encodeExtension(entry.getExtension()));
				list.add(data);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		try {
			RankingDataManager.refreshSRanking(type, list);
			return true;
		} catch (Throwable t) {
			logger.error("保存排行榜条目到数据库时发生异常：" + type, t);
			return false;
		}
	}

	private MomentSRankingEntryImpl<K, E>[] getArrayCopy() {
		readLock.lock();
		try {
			MomentSRankingEntryImpl<K, E>[] rankArray_ = new MomentSRankingEntryImpl[size];
			System.arraycopy(rankArray, 1, rankArray_, 0, size);
			return rankArray_;
		} finally {
			readLock.unlock();
		}
	}

	public int getRankingSize() {
		return size;
	}

	public MomentSRankingEntryImpl<K, E> addLast(K key, E extension) throws RankingCapacityNotEougthException {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		if (extension == null) {
			throw new IllegalArgumentException("extension is null");
		}
		MomentSRankingEntryImpl<K, E> old = rankingMap.get(key);
		if (old != null) {
			return old;
		}
		MomentSRankingEntryImpl<K, E> entryImpl;
		writeLcok.lock();
		try {
			if (size >= maxCapacity) {
				throw new RankingCapacityNotEougthException("ranking capacity is full:current size = " + size + ",maxCapacity = " + maxCapacity);
			}
			int newSize = size + 1;
			entryImpl = new MomentSRankingEntryImpl<K, E>(newSize, key, extension);
			old = rankingMap.putIfAbsent(key, entryImpl);
			if (old != null) {
				return old;
			}
			rankArray[newSize] = entryImpl;
			size = newSize;
		} finally {
			writeLcok.unlock();
		}
		dataUpdater.submitUpdateTask();
		return entryImpl;
	}

	@Override
	public ListRankingEntry<K, E> replace(K key, E extension, K placeKey) throws ReplacerAlreadyExistException, ReplaceTargetNotExistException {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		if (extension == null) {
			throw new IllegalArgumentException("extension is null");
		}
		MomentSRankingEntryImpl<K, E> placerEntry;
		writeLcok.lock();
		try {
			if (this.rankingMap.containsKey(key)) {
				throw new ReplacerAlreadyExistException("排行榜已经存在指定条目：" + key);
			}
			MomentSRankingEntryImpl<K, E> twoMomentSRankingEntry = this.rankingMap.remove(placeKey);
			if (twoMomentSRankingEntry == null) {
				throw new ReplaceTargetNotExistException("排行榜不存在指定条目：" + placeKey);
			}
			int ranking = twoMomentSRankingEntry.getRanking();
			placerEntry = new MomentSRankingEntryImpl<K, E>(ranking, key, extension);
			rankArray[ranking] = placerEntry;
			this.rankingMap.put(key, placerEntry);
		} finally {
			writeLcok.unlock();
		}
		dataUpdater.submitUpdateTask();
		return placerEntry;
	}

	public boolean swap(K oneKey, K twoKey) {
		// 先检查一次,containsKey操作不需要获取锁
		if (!this.rankingMap.containsKey(oneKey)) {
			return false;
		}
		if (!this.rankingMap.containsKey(twoKey)) {
			return false;
		}
		writeLcok.lock();
		try {
			// 两个排行榜条目的位置交换
			MomentSRankingEntryImpl<K, E> oneMomentSRankingEntry = this.rankingMap.get(oneKey);
			MomentSRankingEntryImpl<K, E> twoMomentSRankingEntry = this.rankingMap.get(twoKey);
			if (oneMomentSRankingEntry == null || twoMomentSRankingEntry == null) {
				return false;
			}
			int oneRanking = oneMomentSRankingEntry.getRanking();
			int twoRanking = twoMomentSRankingEntry.getRanking();
			if (oneRanking <= twoRanking) {
				return false;
			}

			MomentSRankingEntryImpl<K, E> newOneEntry = new MomentSRankingEntryImpl<K, E>(twoRanking, oneKey, oneMomentSRankingEntry.getExtension());
			MomentSRankingEntryImpl<K, E> newTwoEntry = new MomentSRankingEntryImpl<K, E>(oneRanking, twoKey, twoMomentSRankingEntry.getExtension());

			rankArray[oneRanking] = newTwoEntry;
			rankArray[twoRanking] = newOneEntry;

			this.rankingMap.put(oneKey, newOneEntry);
			this.rankingMap.put(twoKey, newTwoEntry);
		} finally {
			writeLcok.unlock();
		}
		dataUpdater.submitUpdateTask();
		return true;
	}

	@Override
	public ListRankingEntry<K, E> getRankingEntry(int ranking) {
		if (ranking <= 0) {
			throw new IllegalArgumentException("ranking must more then 0:ranking = " + ranking);
		}
		if (ranking > maxCapacity) {
			throw new IllegalArgumentException("ranking > maxCapcacity:ranking = " + ranking + ",maxCapacity = " + maxCapacity);
		}
		readLock.lock();
		try {
			return rankArray[ranking];
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public ListRankingEntry<K, E> getRankingEntry(K key) {
		return this.rankingMap.get(key);
	}

	@Override
	public List<ListRankingEntry<K, E>> getRankingEntrys(List<Integer> rankingList) {
		int size = rankingList.size();
		ArrayList<ListRankingEntry<K, E>> list = new ArrayList<ListRankingEntry<K, E>>(size);
		readLock.lock();
		try {
			for (int i = 0; i < size; i++) {
				int ranking = rankingList.get(i);
				if (ranking > this.size) {
					continue;
				}
				ListRankingEntry<K, E> entry = rankArray[ranking];
				if (entry != null) {
					list.add(entry);
				}
			}
		} finally {
			readLock.unlock();
		}
		return list;
	}

	@Override
	public ListRankingExtension<K, E> getExtension() {
		return this.rankingExtension;
	}

	@Override
	public List<? extends ListRankingEntry<K, E>> getEntrysCopy() {
		return Arrays.asList(getArrayCopy());
	}

	@Override
	public ListRankingEntry<K, E> getFirstEntry() {
		readLock.lock();
		try {
			return rankArray[1];
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public ListRankingEntry<K, E> getLastEntry() {
		readLock.lock();
		try {
			return rankArray[size];
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int getMaxCapacity() {
		return maxCapacity;
	}

	/**
	 * 提交指定条目的更新
	 * 
	 * @param key
	 */
	public void subimitUpdatedTask(K key) {
		dataUpdater.submitUpdateTask();
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public boolean isFull() {
		return size >= maxCapacity;
	}

	@Override
	public boolean contains(K key) {
		return rankingMap.containsKey(key);
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public ListRankingEntry<K, E> remove(K key) {
		// 当前版本实现删除的性能比较差，需要O(n)的移动
		// 我们游戏需要删除的次数很少，以后再进去优化
		ListRankingEntry<K, E> entry;
		writeLcok.lock();
		try {
			entry = rankingMap.remove(key);
			if (entry == null) {
				return null;
			}
			int rank = entry.getRanking();
			int currentSize = size;
			for (int i = rank + 1; i <= currentSize; i++) {
				MomentSRankingEntryImpl<K, E> oldEntry = rankArray[i];
				int newIndex = i - 1;
				K k = oldEntry.getKey();
				MomentSRankingEntryImpl<K, E> entryImp = new MomentSRankingEntryImpl<K, E>(newIndex, k, oldEntry.getExtension());
				rankArray[newIndex] = entryImp;
				rankingMap.put(k, entryImp);
			}
			rankArray[currentSize] = null;
			size--;
		} finally {
			writeLcok.unlock();
		}
		dataUpdater.submitUpdateTask();
		return entry;
	}

}
