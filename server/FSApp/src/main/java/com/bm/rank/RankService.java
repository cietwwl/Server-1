package com.bm.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class RankService{
	
	
	private final int maxCapacity;	
	
	private TreeSet<IRankEntry> treeSet;
	private final ReadLock treeReadLock;	
	private final WriteLock treeWriteLock;	
	
	
	private List<IRankEntry> entryList;
	private final ReadLock listReadLock;	
	private final WriteLock listWriteLock;
	
	
	private Map<String, IRankEntry> entryMap = new ConcurrentHashMap<String, IRankEntry>();
	
	private final RankDataMgr rankDataMgr = RankDataMgr.getInstance();
	
	private final IRankDao rankDao;
	
	public RankService(RankType type){
		
		rankDao = null;
		this.maxCapacity = type.getMaxCapacity();
		
		ReentrantReadWriteLock treeRwLock = new ReentrantReadWriteLock();
		this.treeReadLock = treeRwLock.readLock();
		this.treeWriteLock = treeRwLock.writeLock();

		ReentrantReadWriteLock listRwLock = new ReentrantReadWriteLock();
		this.listReadLock = listRwLock.readLock();
		this.listWriteLock = listRwLock.writeLock();
		
		initRank();
	}
	
	private void initRank(){
		List<IRankEntry> allFromDb = rankDao.getAllRankList();
		Map<String, IRankEntry> entryMapTmp = new ConcurrentHashMap<String, IRankEntry>();
		for (IRankEntry entryTmp : allFromDb) {
			entryMapTmp.put(entryTmp.rankId(), entryTmp);
		}
		TreeSet<IRankEntry>  treeSetTmp = new TreeSet<IRankEntry>(new Comparator<IRankEntry>() {

			@Override
			public int compare(IRankEntry source, IRankEntry target) {
				return -source.compareTo(target);
			}
		});
		treeSetTmp.addAll(allFromDb);
		treeWriteLock.lock();
		try {			
			treeSet = treeSetTmp;
			entryMap = entryMapTmp;			
		}finally{
			treeWriteLock.unlock();
		}
		fixOrderList();
	}


	public void addEntry(IRankEntry rankingEntry) {
		
		treeWriteLock.lock();
		try {			
			
			// 如果已经存在于队列，要先从队列中移除,避免重复。
			if(entryMap.containsKey(rankingEntry.rankId())){
				IRankEntry oldEntry = entryMap.get(rankingEntry.rankId());
				treeSet.remove(oldEntry);
				entryMap.remove(oldEntry);
				rankDataMgr.addTask(rankDao.getDeleteTask(oldEntry));
			}		
			
			// 当排行榜已满的时候，直接与最后一个比较
			if (treeSet.size() >= this.maxCapacity) {
				IRankEntry last = treeSet.last();
				if (rankingEntry.compareTo(last) <= 0) {
					return;
				}
			}			
			treeSet.add(rankingEntry);	
			rankDataMgr.addTask(rankDao.getInsertTask(rankingEntry));
			if (treeSet.size() > this.maxCapacity) {
				IRankEntry lastEntry = treeSet.pollLast();		
				rankDataMgr.addTask(rankDao.getDeleteTask(lastEntry));
				
			}			
		} finally {
			treeWriteLock.unlock();
		}	
		
		fixOrderList();		
	}

	private void fixOrderList(){	
		List<IRankEntry> entryTmpList  = null;
		treeReadLock.lock();		
		try {			
			entryTmpList = Collections.unmodifiableList(new ArrayList<IRankEntry>(treeSet)) ;
		} finally{
			treeReadLock.unlock();
		}
		
		listWriteLock.lock();		
		try {			
			entryList = entryTmpList;	
		} finally{
			listWriteLock.unlock();
		}
	}
	
	public List<IRankEntry> getRankList(int start, int range){
		List<IRankEntry>  orderList = new ArrayList<IRankEntry>();
		listReadLock.lock();
		try {						
			for (int rank = start; rank < start + range; rank++) {
				if(rank < entryList.size()){
					orderList.add(entryList.get(rank));
				}			
			}			
		} finally{
			listReadLock.unlock();
		}
		return orderList;
	}
	
	public IRankEntry getEntry(String rankId){
		return entryMap.get(rankId);
	}
	
	
	/*
	 * 获取给定 rankId 对应的排名
	 */
	public int getRankPosition(String rankId){
		IRankEntry entry = entryMap.get(rankId);
		int rank = -1;
		if(entry!=null){
			listReadLock.lock();
			try {			
				rank = getPosition(entry, entryList);	
			} finally{
				listReadLock.unlock();
			}
		}
		return rank;
	}
	
	private int getPosition(IRankEntry rankingEntry, List<IRankEntry> entryListP) {
		
		int size = entryListP.size();
		if (size == 0) {
			return -1;
		}
		int left = 0;
		int right = size;
		for (; left <= right;) {
			int middle = (left + right )/2;
			IRankEntry entry = entryListP.get(middle);
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
	
	
}
