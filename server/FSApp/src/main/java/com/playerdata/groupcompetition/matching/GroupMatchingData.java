package com.playerdata.groupcompetition.matching;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.util.GCompUtil;

/**
 * 
 * 帮派争霸中某个帮派正在匹配的数据
 * 
 * @author CHEN.P
 *
 */
class GroupMatchingData {
	
//	private static final Boolean PRESENT = Boolean.TRUE;
	
	private final String groupId;
	private final String againstGroupId;
	private final List<String> matchingDataKeys;
	private final Map<Integer, Queue<MatchingData>> matchingData; // key=等级，value=该等级下的英雄
	private final Deque<RandomMatchingData> randomMatchingDatas;
//	private final Deque<RandomMatchingData> turnBackRandomMatchingDatas;
	private final AtomicBoolean someCancel = new AtomicBoolean(false);
	private final int maxLv = 60;
	private Map<String, RandomMatchingData> matchedUserIds; // 已经匹配上的userId
	
	GroupMatchingData(String groupId, String againstGroupId) {
		this.groupId = groupId;
		this.againstGroupId = againstGroupId;
		this.matchingDataKeys = new ArrayList<String>();
		this.matchingData = new HashMap<Integer, Queue<MatchingData>>();
		this.randomMatchingDatas = new LinkedList<RandomMatchingData>();
//		this.turnBackRandomMatchingDatas = new LinkedList<RandomMatchingData>();
		this.matchedUserIds = new LinkedHashMap<String, RandomMatchingData>();
	}
	
	synchronized void addMatchingData(MatchingData m) {
		this.matchingDataKeys.add(m.getTeamId());
		Queue<MatchingData> list = this.matchingData.get(m.getLv());
		if (list == null) {
			list = new LinkedList<MatchingData>();
			matchingData.put(m.getLv(), list);
		}
		list.add(m);
	}
	
	synchronized void onMatchingDataLvUpdate(int preLv, MatchingData m) {
		Queue<MatchingData> list = this.matchingData.get(preLv);
		list.remove(m);
		addMatchingData(m);
	}
	
	synchronized void removeMatchingData(MatchingData md) {
		this.matchingDataKeys.remove(md.getTeamId());
		this.matchingData.get(md.getLv()).remove(md);
	}
	
	synchronized boolean contains(String key) {
		return this.matchingDataKeys.contains(key);
	}
	
	synchronized MatchingData pollMatchingByLv(int lv) {
		Queue<MatchingData> list = matchingData.get(lv);
		if (list != null && list.size() > 0) {
			return pollMatchingData(list);
		}
		return null;
	}
	
	private MatchingData pollMatchingData(Queue<MatchingData> list) {
		MatchingData md;
		while ((md = list.poll()) != null) {
			if (!md.isCancel()) {
				return md;
			}
		}
		return null;
	}

	synchronized MatchingData pollBeginWithMaxLv() {
		if (matchingDataKeys.size() > 0) {
			Queue<MatchingData> list;
			for (int lv = maxLv; lv-- > 0;) {
				list = matchingData.get(lv);
				if (list != null && list.size() > 0) {
					MatchingData data = this.pollMatchingData(list);
					if (data != null) {
						return data;
					}
				}
			}
		}
		return null;
	}
	
	synchronized MatchingData get(GCompTeam team) {
		Queue<MatchingData> list = matchingData.get(team.getLv());
		if (list != null) {
			String teamId = team.getTeamId();
			for (MatchingData d : list) {
				if (d.getTeamId().equals(teamId)) {
					return d;
				}
			}
		}
		return null;
	}
	
	synchronized void clear() {
		matchingDataKeys.clear();
		matchingData.clear();
	}
	
	private boolean reliveRandomMatchingData(RandomMatchingData rmd, List<String> heroIds) {
		synchronized (rmd) {
			if (rmd.isRemoved()) {
				// 已经被移除了
				return false;
			}
			rmd.setHeroIds(heroIds);
			rmd.setCancel(false);
			rmd.setDeadline(System.currentTimeMillis() + GCompUtil.getMatchingTimeoutMillis());
			GCompUtil.log("--------- relive data : {} ----------", rmd);
			return true;
		}
	}
	
	void addRandomMatchingData(RandomMatchingData data) {
		RandomMatchingData rmd = matchedUserIds.get(data.getUserId());
		if (rmd != null) {
			if (reliveRandomMatchingData(rmd, data.getHeroIds())) {
				return;
			}
		} else {
			synchronized (this.randomMatchingDatas) {
				for (RandomMatchingData temp : randomMatchingDatas) {
					if (temp.getUserId().equals(data.getUserId())) {
						if (reliveRandomMatchingData(temp, data.getHeroIds())) {
							return;
						} else {
							break;
						}
					}
				}
			}
		}
		synchronized (randomMatchingDatas) {
			randomMatchingDatas.add(data);
		}
	}
	
	boolean cancelRandomMatchingData(String userId) {
		RandomMatchingData rmd = matchedUserIds.get(userId);
		if(rmd != null) {
			rmd.setCancel(true);
			return true;
		}
		synchronized(this.randomMatchingDatas) {
			for(RandomMatchingData temp : randomMatchingDatas) {
				if(temp.getUserId().equals(userId)) {
					temp.setCancel(true);
					someCancel.compareAndSet(false, true);
					return true;
				}
			}
		}
		return false;
	}
	
	void turnBackRandomMatchingData(RandomMatchingData rmd) {
//		synchronized (this.turnBackRandomMatchingDatas) {
//			this.turnBackRandomMatchingDatas.add(rmd);
//		}
	}
	
	void beforeRandomMatching() {
//		GCompUtil.log("---------- 帮派争霸，随机匹配任务前的通知！帮派id：{} ----------", this.groupId);
		if (someCancel.compareAndSet(true, false)) {
			synchronized (this.randomMatchingDatas) {
				RandomMatchingData rmd;
				for (Iterator<RandomMatchingData> itr = randomMatchingDatas.iterator(); itr.hasNext();) {
					rmd = itr.next();
					synchronized (rmd) {
						if (rmd.isCancel()) {
							rmd.setRemoved(true);
							itr.remove();
						}
					}
				}
			}
		}
//		if(this.turnBackRandomMatchingDatas.size() > 0) {
//			synchronized (this.turnBackRandomMatchingDatas) {
//				turnBackRandomMatchingDatas.descendingIterator(); // 后进先出
//				for (Iterator<RandomMatchingData> itr = turnBackRandomMatchingDatas.descendingIterator(); itr.hasNext();) {
//					randomMatchingDatas.addFirst(itr.next());
//					itr.remove();
//				}
//			}
//		}
		if (this.matchedUserIds.size() > 0) {
			RandomMatchingData rmd;
			Deque<RandomMatchingData> queue = new ArrayDeque<RandomMatchingData>();
			for (Iterator<String> keyItr = this.matchedUserIds.keySet().iterator(); keyItr.hasNext();) {
				rmd = matchedUserIds.get(keyItr.next());
				synchronized (rmd) {
					if (!rmd.isCancel()) {
						queue.add(rmd);
					} else {
						rmd.setRemoved(true);
						GCompUtil.log("随机匹配数据：{}，已经取消，不重新放入队列！", rmd);
					}
				}
			}
			if (queue.size() > 0) {
				synchronized (randomMatchingDatas) {
					for (Iterator<RandomMatchingData> itr = queue.descendingIterator(); itr.hasNext();) {
						randomMatchingDatas.addFirst(itr.next());
					}
				}
			}
			this.matchedUserIds.clear();
		}
	}
	
	int getRandomMatchingSize() {
		return randomMatchingDatas.size();
	}
	
	List<RandomMatchingData> pollRandomMatchingData(int count) {
		synchronized(this.randomMatchingDatas) {
//			if(this.randomMatchingDatas.size() < count) {
//				throw new RuntimeException("随机匹配数量不足：" + count);
//			}
			List<RandomMatchingData> list = new ArrayList<RandomMatchingData>(count);
			RandomMatchingData data;
			while (count > 0) {
				data = randomMatchingDatas.peek();
				if(data == null) {
					break;
				} else if(data.isCancel()) {
					continue;
				} 
				randomMatchingDatas.poll();
				list.add(data);
				matchedUserIds.put(data.getUserId(), data);
				count--;
			}
			return list;
		}
	}
	
	void afterMatched(List<RandomMatchingData> rmdList) {
		for (RandomMatchingData rmd : rmdList) {
			if(rmd.isRobot()) {
				continue;
			}
			matchedUserIds.remove(rmd.getUserId());
			GCompUtil.log("---------- 移除已经匹配上的数据：{} ----------", rmd);
		}
	}
	
	boolean isInRandomMatching(String userId) {
		RandomMatchingData rmd = matchedUserIds.get(userId);
		if(rmd != null) {
			if(rmd.isCancel()) {
				return false;
			}
			return true;
		}
		synchronized (this.randomMatchingDatas) {
			for (RandomMatchingData d : randomMatchingDatas) {
				if (d.getUserId().equals(userId) && !d.isCancel()) {
					return true;
				}
			}
			return false;
		}
	}
	
	Queue<MatchingData> getMatchingQueue(int lv) {
		return matchingData.get(lv);
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public String getAgainstGroupId() {
		return againstGroupId;
	}
}
