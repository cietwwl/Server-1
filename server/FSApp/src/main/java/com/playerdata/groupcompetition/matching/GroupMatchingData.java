package com.playerdata.groupcompetition.matching;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
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
	
	private final String groupId;
	private final String againstGroupId;
	private final List<String> matchingDataKeys;
	private final Map<Integer, Queue<MatchingData>> matchingData; // key=等级，value=该等级下的英雄
	private final Deque<RandomMatchingData> randomMatchingDatas;
	private final Deque<RandomMatchingData> turnBackRandomMatchingDatas;
	private final AtomicBoolean someCancel = new AtomicBoolean(false);
	private final int maxLv = 60;
	
	GroupMatchingData(String groupId, String againstGroupId) {
		this.groupId = groupId;
		this.againstGroupId = againstGroupId;
		this.matchingDataKeys = new ArrayList<String>();
		this.matchingData = new HashMap<Integer, Queue<MatchingData>>();
		this.randomMatchingDatas = new LinkedList<RandomMatchingData>();
		this.turnBackRandomMatchingDatas = new LinkedList<RandomMatchingData>();
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
		if (list != null) {
			return list.poll();
		}
		return null;
	}
	
	synchronized MatchingData pollBeginWithMaxLv() {
		if (matchingDataKeys.size() > 0) {
			Queue<MatchingData> list;
			for (int lv = maxLv; lv-- > 0;) {
				list = matchingData.get(lv);
				if (list != null && list.size() > 0) {
					return list.poll();
				}
			}
		}
		return null;
	}
	
	synchronized MatchingData get(GCompTeam team) {
		Queue<MatchingData> list = matchingData.get(team.getLv());
		if(list != null) {
			String teamId = team.getTeamId();
			for(MatchingData d : list) {
				if(d.getTeamId().equals(teamId)) {
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
	
	void addRandomMatchingData(RandomMatchingData data) {
		synchronized (this.randomMatchingDatas) {
			for (RandomMatchingData temp : randomMatchingDatas) {
				if (temp.getUserId().equals(data.getUserId())) {
					temp.setHeroIds(data.getHeroIds());
					temp.setCancel(false);
					temp.setDeadline(System.currentTimeMillis() + GCompUtil.getMatchingTimeoutMillis());
					return;
				}
			}
			randomMatchingDatas.add(data);
		}
	}
	
	boolean cancelRandomMatchingData(String userId) {
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
		synchronized (this.turnBackRandomMatchingDatas) {
			this.turnBackRandomMatchingDatas.add(rmd);
		}
	}
	
	void beforeRandomMatching() {
//		GCompUtil.log("---------- 帮派争霸，随机匹配任务前的通知！帮派id：{} ----------", this.groupId);
		if (someCancel.compareAndSet(true, false)) {
			synchronized (this.randomMatchingDatas) {
				for (Iterator<RandomMatchingData> itr = randomMatchingDatas.iterator(); itr.hasNext();) {
					if (itr.next().isCancel()) {
						itr.remove();
					}
				}
			}
		}
		if(this.turnBackRandomMatchingDatas.size() > 0) {
			synchronized (this.turnBackRandomMatchingDatas) {
				turnBackRandomMatchingDatas.descendingIterator(); // 后进先出
				for (Iterator<RandomMatchingData> itr = turnBackRandomMatchingDatas.descendingIterator(); itr.hasNext();) {
					randomMatchingDatas.addFirst(itr.next());
					itr.remove();
				}
			}
		}
	}
	
	int getRandomMatchingSize() {
		return randomMatchingDatas.size();
	}
	
	List<RandomMatchingData> pollRandomMatchingData(int count) {
		synchronized(this.randomMatchingDatas) {
			if(this.randomMatchingDatas.size() < count) {
				throw new RuntimeException("随机匹配数量不足：" + count);
			}
			List<RandomMatchingData> list = new ArrayList<RandomMatchingData>(count);
			while (count > 0) {
				list.add(randomMatchingDatas.poll());
				count--;
			}
			return list;
		}
	}
	
	boolean isInRandomMatching(String userId) {
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
