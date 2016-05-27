package com.rw.fsutil.ranking.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.common.EnumerateListImpl;
import com.rw.fsutil.common.ReverseEnumerateList;
import com.rw.fsutil.common.SegmentEnumeration;
import com.rw.fsutil.common.SegmentList;
import com.rw.fsutil.common.SegmentListImpl;
import com.rw.fsutil.concurrent.FixTaskLatch;
import com.rw.fsutil.log.EngineLoggerImpl;
import com.rw.fsutil.ranking.DataUpdater;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntityOfRank;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingExtension;
import com.rw.fsutil.ranking.RankingFactory.RankingEntrySequence;

/**
 * 排行榜实现
 * 
 * @author Jamaz
 */
public class RankingImpl<C extends Comparable<C>, E> implements Ranking<C, E> {

	private static final EngineLoggerImpl logger = new EngineLoggerImpl("ranking");
	private final int type;
	private final int maxCapacity;
	private final String name;
	private final ConcurrentHashMap<String, RankingEntryImpl<C, E>> hashMap;
	private final ReadLock readLock;
	private final WriteLock writeLock;
	private RankingEntrySequence generator;
	private final FixTaskLatch orderListTask;
	private final RankingExtension<C, E> parser;
	private final DataUpdater dataUpdater;
	private volatile ArrayList<MomentEntry<C, E>> orderList;
	private TreeMap<RankingEntryImpl<C, E>, C> treeMap;

	public RankingImpl(int type, int maxCapacity, String name, RankingExtension<C, E> parser, int period, RankingEntrySequence generator, ScheduledExecutorService executor) {
		if (maxCapacity < 1) {
			throw new ExceptionInInitializerError("maxCapacity < 1");
		}
		this.type = type;
		this.name = name;
		this.parser = parser;
		this.maxCapacity = maxCapacity;
		this.hashMap = new ConcurrentHashMap<String, RankingEntryImpl<C, E>>();
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		this.readLock = rwLock.readLock();
		this.writeLock = rwLock.writeLock();
		this.generator = generator;
		writeLock.lock();
		try {
			this.treeMap = new TreeMap<RankingEntryImpl<C, E>, C>();
		} finally {
			writeLock.unlock();
		}
		this.orderListTask = new FixTaskLatch(new Runnable() {

			@Override
			public void run() {
				// RankingImpl.this.orderList = new ArrayList<MomentEntry<C,
				// E>>();
				ArrayList<MomentEntry<C, E>> list = new ArrayList<MomentEntry<C, E>>();
				Set<RankingEntryImpl<C, E>> set = RankingImpl.this.treeMap.keySet();
				int count = 0;
				for (RankingEntryImpl<C, E> entry : set) {
					list.add(new MomentEntry<C, E>(entry, ++count));
				}
				RankingImpl.this.orderList = list;
			}
		});
		this.dataUpdater = new DataUpdater(TimeUnit.MINUTES.toSeconds(period), executor) {

			@Override
			public boolean notifyDataUpdated() {
				try {
					updateEntityDataToDB();
					return true;
				} catch (Throwable ex) {
					logger.error("更新排行榜异常：" + RankingImpl.this.type, ex);
					return false;
				}
			}
		};
		try {
			List<RankingEntryData> dataList = RankingDataManager.getRankingEntitys(type);
			int size = dataList.size();
			for (int i = 0; i < size; i++) {
				RankingEntryData entryData = dataList.get(i);
				C comparable = parser.decodeComparable(entryData.getCondition());
				RankingEntryImpl<C, E> entry = new RankingEntryImpl<C, E>(entryData.getKey(), entryData.getId(), comparable, parser.decodeExtendedAttribute(entryData.getExtension()));
				insertRanking(entry, comparable);
			}
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * 同步到数据库
	 * 
	 * @throws Exception
	 **/
	void updateEntityDataToDB() throws Throwable {
		ArrayList<RankingEntryData> list = new ArrayList<RankingEntryData>(this.hashMap.size());
		for (final RankingEntryImpl<C, E> entry : this.hashMap.values()) {
			list.add(new RankingEntryData(entry.getUniqueId(), type, entry.getKey(), parser.encodeComparable(entry.getComparable()), parser.encodeExtendedAttribute(entry.getExtendedAttribute())
					.toString()));
		}
		if (list.isEmpty()) {
			logger.info("排行榜数据没有数据仍然同步：" + RankingImpl.this.type);
		}
		RankingDataManager.refreshRanking(type, list);
	}

	@Override
	public void updateToDB() {
		this.dataUpdater.flush();
	}

	@Override
	public int getRanking(RankingEntry<C, E> rankingEntry) {
		if (rankingEntry == null) {
			throw new NullPointerException("rankingEntry is null");
		}
		return getRanking(rankingEntry.getKey());
	}

	private int getPosition(RankingEntryImpl<C, E> rankingEntry, ArrayList<MomentEntry<C, E>> list) {
		int size = list.size();
		if (size == 0) {
			return -1;
		}
		int left = 0;
		int right = size;
		for (; left <= right;) {
			int middle = left + right >> 1;
			RankingEntryImpl<C, E> entry = list.get(middle).entryImpl;
			int result = rankingEntry.compareTo(entry);
			if (result > 0) {
				left = middle + 1;
			} else if (result < 0) {
				right = middle - 1;
			} else {
				return middle + 1;
			}
		}
		return -1;
	}

	@Override
	public int getRanking(String key) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		ArrayList<MomentEntry<C, E>> list;
		RankingEntryImpl<C, E> rankingEntry;
		readLock.lock();
		try {
			rankingEntry = this.hashMap.get(key);
			if (rankingEntry == null) {
				return -1;
			}
			list = getOrderList_();
		} finally {
			readLock.unlock();
		}
		return getPosition(rankingEntry, list);
	}

	@Override
	public RankingEntry<C, E> getRankingEntry(int ranking) {
		if (ranking > this.maxCapacity) {
			throw new IllegalArgumentException("ranking too large");
		}
		ArrayList<MomentEntry<C, E>> list = getOrderList();
		int size = list.size();
		if (ranking > size) {
			return null;
		}
		return list.get(ranking - 1).entryImpl;
	}

	@Override
	public EnumerateList<MomentEntry<C, E>> getEntriesEnumeration(int fromRank, int toRank) {
		ArrayList<MomentEntry<C, E>> list = getOrderList();
		return new SegmentEnumeration<MomentEntry<C, E>>(list, fromRank - 1, toRank - 1);
	}

	private void clean() {
		this.orderListTask.reset();
		this.orderList = null;
	}

	@Override
	public RankingEntry<C, E> getRankingEntry(String entryKey) {
		return hashMap.get(entryKey);
	}

	@Override
	public void clearAndInsert(List<RankingEntityOfRank<C, E>> insertData) {
		int size = insertData.size();
		writeLock.lock();
		try {
			this.hashMap.clear();
			this.treeMap.clear();
			for (int i = 0; i < size; i++) {
				RankingEntityOfRank<C, E> entity = insertData.get(i);
				String key = entity.getKey();
				E extension = entity.getExtendedAttribute();
				C newComparable = entity.getComparable();
				RankingEntryImpl<C, E> oldEntry = this.hashMap.get(key);
				long id;
				if (oldEntry == null) {
					if (treeMap.size() >= this.maxCapacity) {
						RankingEntryImpl<C, E> last = treeMap.lastKey();
						// 比最后一个小直接返回
						if (newComparable.compareTo(last.getComparable()) <= 0) {
							continue;
						}
					}
					id = generator.assignId();
					// 排行榜没满或者比最后一个大，构造新的Entry
				} else {
					// 没有更新的时候直接返回原条目
					if (oldEntry.getComparable().compareTo(newComparable) == 0) {
						continue;
					}
					treeMap.remove(oldEntry);
					id = oldEntry.getUniqueId();
				}
				RankingEntryImpl<C, E> newEntry = new RankingEntryImpl<C, E>(key, id, newComparable, extension);
				insertRanking(newEntry, newComparable);
			}
		} finally {
			writeLock.unlock();
		}
		dataUpdater.submitUpdateTask();
	}

	@Override
	public <P> RankingEntry<C, E> addOrUpdateRankingEntry(String key, C newComparable, P customParam) {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		if (newComparable == null) {
			throw new IllegalArgumentException("comparable is null");
		}
		RankingEntryImpl<C, E> newEntry;
		RankingEntryImpl<C, E> evicted = null;
		writeLock.lock();
		try {
			RankingEntryImpl<C, E> oldEntry = this.hashMap.get(key);
			if (oldEntry == null) {
				if (treeMap.size() >= this.maxCapacity) {
					RankingEntryImpl<C, E> last = treeMap.lastKey();
					// 比最后一个小直接返回
					if (newComparable.compareTo(last.getComparable()) <= 0) {
						return null;
					}
				}
				// 排行榜没满或者比最后一个大，构造新的Entry
				E extension = parser.newEntryExtension(key, customParam);
				newEntry = new RankingEntryImpl<C, E>(key, generator.assignId(), newComparable, extension);
			} else {
				// 没有更新的时候直接返回原条目
				if (oldEntry.getComparable().compareTo(newComparable) == 0) {
					return oldEntry;
				}
				treeMap.remove(oldEntry);
				//TODO 新entry就该用新的ExtendedAttribute
				//E extension = parser.newEntryExtension(key, customParam);
				//newEntry = new RankingEntryImpl<C, E>(key, oldEntry.getUniqueId(), newComparable, extension);
				newEntry = new RankingEntryImpl<C, E>(key, oldEntry.getUniqueId(), newComparable, oldEntry.getExtendedAttribute());
			}
			evicted = insertRanking(newEntry, newComparable);
		} finally {
			writeLock.unlock();
		}
		dataUpdater.submitUpdateTask();
		if (evicted != null) {
			this.parser.notifyEntryEvicted(evicted);
		}
		return newEntry;
	}

	private RankingEntryImpl<C, E> insertRanking(RankingEntryImpl<C, E> newEntry, C newComparable) {
		RankingEntryImpl<C, E> evicted = null;
		this.treeMap.put(newEntry, newComparable);
		this.hashMap.put(newEntry.getKey(), newEntry);
		if (treeMap.size() > this.maxCapacity) {
			evicted = treeMap.lastKey();
			this.treeMap.remove(evicted);
			this.hashMap.remove(evicted.getKey());
		}
		clean();
		return evicted;
	}

	@Override
	public RankingEntry<C, E> updateRankingEntry(RankingEntry<C, E> entry, C newComparable) {
		if (entry == null) {
			throw new IllegalArgumentException("entry is null");
		}
		System.err.println("#updateRankingEntry() " + entry.getKey());
		if (newComparable == null) {
			throw new IllegalArgumentException("comparable is null");
		}
		// 排序的值没有改变则直接返回原Entry
		if (entry.getComparable().compareTo(newComparable) == 0) {
			return entry;
		}

		RankingEntryImpl<C, E> oldEntryImpl = (RankingEntryImpl<C, E>) entry;
		RankingEntryImpl<C, E> newEntryImpl;
		RankingEntryImpl<C, E> evicted = null;
		String key = oldEntryImpl.getKey();
		writeLock.lock();
		try {
			C old = this.treeMap.remove(oldEntryImpl);
			// 当排行榜不存在此条目时，先检查能不能重新加入排行榜
			if (old == null) {
				//删除失败时，检查是否需要进入安全删除模式
				RankingEntryImpl<C, E> oldEntry = this.hashMap.get(key);
				if (oldEntry != null) {
					safeRemove(oldEntry, key);
				}
				// 当排行榜已满的时候，直接与最后一个比较
				if (treeMap.size() >= this.maxCapacity) {
					RankingEntryImpl<C, E> last = treeMap.lastKey();
					// 比最后一个小直接返回false
					if (newComparable.compareTo(last.getComparable()) <= 0) {
						return null;
					}
				}
			}
			newEntryImpl = new RankingEntryImpl<C, E>(key, oldEntryImpl.getUniqueId(), newComparable, oldEntryImpl.getExtendedAttribute());
			evicted = insertRanking(newEntryImpl, newComparable);
		} finally {
			writeLock.unlock();
		}
		dataUpdater.submitUpdateTask();
		if (evicted != null) {
			this.parser.notifyEntryEvicted(evicted);
		}
		return newEntryImpl;
	}

	private void safeRemove(RankingEntryImpl<C, E> oldEntry,String key){
		//可以用==先进行比较
		int removePhase = 0;
		C old = this.treeMap.remove(oldEntry);
		if (old == null) {
			for (Iterator<RankingEntryImpl<C, E>> it = treeMap.keySet().iterator(); it.hasNext();) {
				RankingEntryImpl<C, E> e = it.next();
				if (e.getKey().equals(key)) {
					it.remove();
					removePhase = 2;
					break;
				}
			}
		} else {
			removePhase = 1;
		}
		logger.error("updateRankingEntry移除失败：" + oldEntry.getKey() + "," + oldEntry.getComparable() + ",removePhase:" + removePhase);
	}
	
	@Override
	public RankingEntry<C, E> removeRankingEntry(String entryKey) {
		RankingEntryImpl<C, E> entry = null;
		writeLock.lock();
		try {
			entry = this.hashMap.remove(entryKey);
			if (entry == null) {
				return null;
			}
			if (this.treeMap.remove(entry) == null) {
				logger.error("严重错误@从treeMap中删除失败：" + entryKey);
			}
			clean();
		} finally {
			writeLock.unlock();
		}
		dataUpdater.submitUpdateTask();
		return entry;
	}

	@Override
	public void subimitUpdatedTask(RankingEntry<C, E> entry) {
		// TODO 更新到数据库
		dataUpdater.submitUpdateTask();
	}

	@Override
	public List<? extends MomentRankingEntry<C, E>> getReadOnlyRankingEntries() {
		return Collections.unmodifiableList(getOrderList());
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public EnumerateList<? extends MomentRankingEntry<C, E>> getEntriesEnumeration() {
		ArrayList<MomentEntry<C, E>> list = getOrderList();
		return new EnumerateListImpl<MomentEntry<C, E>>(list.iterator(), list.size());
	}

	@Override
	public EnumerateList<? extends MomentRankingEntry<C, E>> getEntriesReverseEnumeration() {
		ArrayList<MomentEntry<C, E>> list = getOrderList();
		return new ReverseEnumerateList<MomentEntry<C, E>>(list, list.size());
	}

	private ArrayList<MomentEntry<C, E>> getOrderList() {
		ArrayList<MomentEntry<C, E>> list = this.orderList;
		if (list != null) {
			return list;
		}
		readLock.lock();
		try {
			return getOrderList_();
		} finally {
			readLock.unlock();
		}
	}

	private ArrayList<MomentEntry<C, E>> getOrderList_() {
		if (this.orderList == null) {
			this.orderListTask.acquire();
		}
		return this.orderList;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getMaxCapacity() {
		return this.maxCapacity;
	}

	@Override
	public int size() {
		// return this.hashMap.size();
		readLock.lock();
		try {
			return this.treeMap.size();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public RankingEntry<C, E> lastEntry() {
		readLock.lock();
		try {
			if (this.treeMap.size() > 0) {
				return this.treeMap.lastKey();
			} else {
				return null;
			}
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public RankingEntry<C, E> firstEntry() {
		readLock.lock();
		try {
			if (this.treeMap.size() > 0) {
				return this.treeMap.firstKey();
			} else {
				return null;
			}
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public RankingExtension<C, E> getExtension() {
		return this.parser;
	}

	@Override
	public void clear() {
		writeLock.lock();
		try {
			this.treeMap.clear();
			this.hashMap.clear();
			clean();
		} finally {
			writeLock.unlock();
		}
		dataUpdater.submitUpdateTask();
	}

	@Override
	public String toString() {
		return "ranking[type = " + type + "]";
	}

	private static SegmentList DUMMY_SEGMENT_LIST = new SegmentList() {

		@Override
		public Object get(int index) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
		}

		@Override
		public int getRefStartIndex() {
			// TODO Auto-generated method stub
			return -1;
		}

		@Override
		public int getRefEndIndex() {
			// TODO Auto-generated method stub
			return -1;
		}

		@Override
		public int getRefSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getMaxSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public List getSemgentCopy(int start, int end) {
			return Collections.EMPTY_LIST;
		}
	};

	@Override
	public SegmentList<? extends MomentRankingEntry<C, E>> getSegmentList(C fromCondition, C toCondition) {
		int r = fromCondition.compareTo(toCondition);
		if (r == 0) {
			throw new IllegalArgumentException("minValue is equal to maxValue");
		}
		// 对于排行榜来说，from是大数，交换位置
		if (r < 0) {
			C temp = fromCondition;
			fromCondition = toCondition;
			toCondition = temp;
		}

		RankingEntryImpl<C, E> fromEntryDummpy = new RankingEntryImpl<C, E>(null, 0, fromCondition, null);
		RankingEntryImpl<C, E> toEntryDummpy = new RankingEntryImpl<C, E>(null, Long.MAX_VALUE, toCondition, null);
		RankingEntryImpl<C, E> first;
		RankingEntryImpl<C, E> last;
		ArrayList<MomentEntry<C, E>> list;
		readLock.lock();
		try {
			// 最小值比排行榜最大值大，返回空的迭代器
			// if
			// (toCondition.compareTo(this.treeMap.firstKey().getComparable()) >
			// 0) {
			// return DUMMY_ENUMBERATE_LIST;
			// }
			// // 最大值比排行榜最小值小，返回空的迭代器
			// if
			// (fromCondition.compareTo(this.treeMap.lastKey().getComparable())
			// < 0) {
			// return DUMMY_ENUMBERATE_LIST;
			// }
			list = getOrderList_();
			int size = list.size();
			if (size == 0) {
				return DUMMY_SEGMENT_LIST;
			}
			// 最小值比排行榜最大值大，返回空的迭代器
			if (toCondition.compareTo(list.get(0).entryImpl.getComparable()) > 0) {
				return DUMMY_SEGMENT_LIST;
			}
			// 最大值比排行榜最小值小，返回空的迭代器
			if (fromCondition.compareTo(list.get(list.size() - 1).entryImpl.getComparable()) < 0) {
				return DUMMY_SEGMENT_LIST;
			}

			first = this.treeMap.ceilingKey(fromEntryDummpy);
			last = this.treeMap.floorKey(toEntryDummpy);
		} finally {
			readLock.unlock();
		}
		int fromIndex = getPosition(first, list) - 1;
		int toIndex = getPosition(last, list) - 1;
		if(fromIndex > toIndex) {
			return DUMMY_SEGMENT_LIST; 
		}
		return new SegmentListImpl<MomentEntry<C, E>>(list, fromIndex, toIndex);
	}

	static class MomentEntry<C extends Comparable<C>, E> implements MomentRankingEntry<C, E> {

		private final RankingEntryImpl<C, E> entryImpl;
		private final int ranking;

		public MomentEntry(RankingEntryImpl<C, E> entryImpl, int ranking) {
			this.entryImpl = entryImpl;
			this.ranking = ranking;
		}

		@Override
		public int getRanking() {
			return ranking;
		}

		@Override
		public RankingEntry<C, E> getEntry() {
			return entryImpl;
		}

		@Override
		public C getComparable() {
			return entryImpl.getComparable();
		}

		@Override
		public String getKey() {
			return entryImpl.getKey();
		}

		@Override
		public E getExtendedAttribute() {
			return entryImpl.getExtendedAttribute();
		}

	}

	@Override
	public boolean isFull() {
		readLock.lock();
		try {
			return treeMap.size() >= maxCapacity;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public RankingEntry<C, E> getFirstEntry() {
		readLock.lock();
		try {
			if (treeMap.isEmpty()) {
				return null;
			}
			return treeMap.firstKey();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int higherRanking(C condition) {
		if (condition == null) {
			return -1;
		}

		RankingEntryImpl<C, E> entryDummpy = new RankingEntryImpl<C, E>(null, 0, condition, null);
		RankingEntryImpl<C, E> entry;
		ArrayList<MomentEntry<C, E>> list;
		readLock.lock();
		try {
			list = getOrderList_();
			int size = list.size();
			if (size == 0) {
				return -1;
			}

			// 比较条件比最大值还大，就说明没有可以大过它的排行
			if (condition.compareTo(list.get(0).entryImpl.getComparable()) > 0) {
				return -1;
			}

			entry = this.treeMap.floorKey(entryDummpy);
			if (entry == null) {
				return -1;
			}
		} finally {
			readLock.unlock();
		}
		return getPosition(entry, list);
	}

	@Override
	public int lowerRanking(C condition) {
		if (condition == null) {
			return -1;
		}

		RankingEntryImpl<C, E> entryDummpy = new RankingEntryImpl<C, E>(null, 0, condition, null);
		RankingEntryImpl<C, E> entry;
		ArrayList<MomentEntry<C, E>> list;
		readLock.lock();
		try {
			list = getOrderList_();
			int size = list.size();
			if (size == 0) {
				return -1;
			}

			// 比较条件比最低值还小，说明也找不到比它更小的值
			if (condition.compareTo(list.get(list.size() - 1).entryImpl.getComparable()) < 0) {
				return -1;
			}

			entry = this.treeMap.ceilingKey(entryDummpy);
			if (entry == null) {
				return -1;
			}
		} finally {
			readLock.unlock();
		}
		return getPosition(entry, list);
	}
}