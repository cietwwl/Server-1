package com.playerdata.groupcompetition.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.PlayerMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.groupcompetition.holder.GCompMatchDataHolder;
import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.holder.GCompTeamMgr;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwproto.BattleCommon.eBattlePositionType;

/**
 * 
 * 帮派争霸，某场对垒的匹配任务
 * 
 * @author CHEN.P
 *
 */
class AgainstMatchingTask implements IGameTimerTask {
	
	final int againstId; // 对垒的id
	final Map<String, GroupMatchingData> groupMatchingDatas; // 对垒的帮派匹配数据
	final Queue<MatchingData> submitQueue; // 匹配队列
	final String nameOfTimerTask; // 时效任务的名字
	private boolean _on; // 是否进行匹配
	private boolean _randomMatchingOn = true; // 是否随机匹配（个人赛阶段不能随机匹配）
	private String idOfGroupA; // 帮派A的id
	private String idOfGroupB; // 帮派B的id
//	private GCEventsType eventsType; // 当前的赛事类型
	private GCompEventsStatus currentEventsStatus;
	private int memberCountOfCurrent;
	private long intervalMillis;
	private final Random random = new Random();
	
	AgainstMatchingTask(int pAgainstId, String pIdOfGroupA, String pIdOfGroupB) {
		groupMatchingDatas = new HashMap<String, GroupMatchingData>(2, 1.5f);
		submitQueue = new ConcurrentLinkedQueue<MatchingData>();
		groupMatchingDatas.put(pIdOfGroupA, new GroupMatchingData(pIdOfGroupA, pIdOfGroupB));
		groupMatchingDatas.put(pIdOfGroupB, new GroupMatchingData(pIdOfGroupB, pIdOfGroupA));
		againstId = pAgainstId;
		this.idOfGroupA = pIdOfGroupA;
		this.idOfGroupB = pIdOfGroupB;
		this.nameOfTimerTask = "帮派争霸匹配任务：" + againstId;
		this.intervalMillis = TimeUnit.SECONDS.toMillis(GCompCommonConfig.getMatchingIntervalSeconds());
//		this.eventsType = GroupCompetitionMgr.getInstance().getCurrentEventsType();
	}
	
	private void addToMatchingQueue(GroupMatchingData groupMatchingData, GCompTeam team) {
		MatchingData md = new MatchingData(team, groupMatchingData.getGroupId(), againstId);
		groupMatchingData.addMatchingData(md);
		synchronized (submitQueue) {
			submitQueue.add(md);
		}
	}
	
	void addMatching(String groupId, GCompTeam team) {
		GroupMatchingData groupMatchingData = groupMatchingDatas.get(groupId);
		if (!groupMatchingData.contains(team.getTeamId())) {
			this.addToMatchingQueue(groupMatchingData, team);
		} else {
			synchronized (submitQueue) {
				// 有可能在同步期间被人删除了
				MatchingData md = groupMatchingData.get(team);
				if (md != null) {
					// 如果没有被删除
					md.setCancel(false);
					if (md.getLv() != team.getLv()) {
						int lv = md.getLv();
						md.setLv(team.getLv());
						groupMatchingData.onMatchingDataLvUpdate(lv, md);
					}
					md.setDeadline(System.currentTimeMillis() + GCompUtil.getMatchingTimeoutMillis());
				} else {
					// 如果被删除了
					this.addToMatchingQueue(groupMatchingData, team);
				}
			}
		}
	}
	
	boolean cancelMatching(String groupId, GCompTeam team) {
		GroupMatchingData groupMatchindData = this.groupMatchingDatas.get(groupId);
		Queue<MatchingData> matchingDataQueue = groupMatchindData.getMatchingQueue(team.getLv());
		synchronized (matchingDataQueue) {
			for (MatchingData data : matchingDataQueue) {
				if (data.getTeamId().equals(team.getTeamId())) {
					synchronized (data) {
						if (!data.isMatched()) {
							data.setCancel(true);
							return true;
						} else {
							// 已经被匹配上了
							return false;
						}
					}
				}
			}
		}
		return false;
	}
	
	void beforePersonalEvents() {
//		GCompUtil.log("---------- 帮派争霸，个人匹配任务前的通知！ ----------");
		_randomMatchingOn = false;
		submitQueue.clear();
		Set<String> keySet = groupMatchingDatas.keySet();
		for(String key : keySet) {
			groupMatchingDatas.get(key).clear();
		}
	}
	
	void start() {
//		GCompUtil.log("---------- 帮派争霸，匹配任务开始，赛事id：{}，负责帮派：{}", this.againstId, this.groupMatchingDatas.keySet().toString());
		if (this.idOfGroupA.length() == 0 || this.idOfGroupB.length() == 0) {
//			GCompUtil.log("---------- 帮派争霸，检测到轮空的情况，不提交匹配任务！负责帮派：{}", this.groupMatchingDatas.keySet().toString());
			return;
		}
		this._on = true;
		this.memberCountOfCurrent = this.currentEventsStatus == GCompEventsStatus.TEAM_EVENTS ? GCompCommonConfig.getMaxMemberCountOfTeam() : 1;
		FSGameTimerMgr.getInstance().submitSecondTask(this, GCompCommonConfig.getMatchingIntervalSeconds());
	}
	
	void pause() {
		this._on = false;
	}
	
	void end() {
		this._on = false;
	}
	
	GroupMatchingData getGroupMatchingData(String groupId) {
		return this.groupMatchingDatas.get(groupId);
	}
	
	void setCurrentEventsStatus(GCompEventsStatus status) {
		this.currentEventsStatus = status;
	}
	
	private void submitToMatchDataHolder(GCompTeam team1, GCompTeam team2) {
		GCompUtil.log("---------- 匹配到 {} : {} ----------", team1, team2);
		team1.setMatching(false);
		team2.setMatching(false);
		if (team1.isPersonal()) {
			GCompMatchDataHolder.getHolder().addPersonalMatchData(team1, team2);
		} else {
			GCompMatchDataHolder.getHolder().addTeamMatchData(team1, team2);
		}
	}
	
	private void onMatch(MatchingData md1, MatchingData md2) {
		GCompTeam team1 = GCompTeamMgr.getInstance().getTeam(this.againstId, md1.getGroupId(), md1.getTeamId());
		GCompTeam team2 = GCompTeamMgr.getInstance().getTeam(this.againstId, md2.getGroupId(), md2.getTeamId());
		this.submitToMatchDataHolder(team1, team2);
	}
	
	private void onRandomMatch(List<RandomMatchingData> list1, List<RandomMatchingData> list2) {
		GCompTeam team1 = GCompTeamMgr.getInstance().createRandomTeam(list1);
		GCompTeam team2 =  GCompTeamMgr.getInstance().createRandomTeam(list2);
		if (team1 != null && team2 != null) {
			this.submitToMatchDataHolder(team1, team2);
		}
	}
	
	private void getRandomMember(LinkedList<GCompMember> memberList, List<RandomMatchingData> targetList) {
		int size = memberList.size();
		int maxSize = memberCountOfCurrent;
		if (targetList.size() < maxSize) {
			while (size > 0) {
				GCompMember member = memberList.remove(random.nextInt(size));
				size--;
				memberList.add(member); // 放回去
				boolean duplicate = false;
				for (int i = 0, tempSize = targetList.size(); i < tempSize; i++) {
					if (targetList.get(i).getUserId().equals(member.getUserId())) {
						duplicate = true;
						break;
					}
				}
				if (!duplicate) {
					targetList.add(this.createRandomMatchingData(member));
					if (targetList.size() == maxSize) {
						break;
					}
				}
			}
		}
	}
	
	private LinkedList<GCompMember> getAllMembersOfGroup(String groupId) {
		LinkedList<GCompMember> allMembersOfGroup = new LinkedList<GCompMember>();
		GCompMemberMgr.getInstance().getCopyOfAllMembers(groupId, allMembersOfGroup);
		return allMembersOfGroup;
	}
	
	private boolean isNotTimeout(long currentTimeMillis, long deadline) {
		return currentTimeMillis < deadline && (deadline - currentTimeMillis) > intervalMillis;
	}

	/**
	 * 
	 * @param groupMatchingData
	 * @param allMembersOfGroup
	 * @param maxMemberSize
	 * @param force 如果不够人数，是否也强制获取这个列表
	 * @return
	 */
	private List<RandomMatchingData> getRandomMatchingDataList(GroupMatchingData groupMatchingData, LinkedList<GCompMember> allMembersOfGroup, int maxMemberSize, boolean force) {
		// 从帮派的随机匹配列表中获取匹配数据
		// 如果有人已经等到匹配超时，会出现以下的情况：
		// 1、如果同一方的等待列表中超过3人，则组成一个队伍
		// 2、如果同一方的等待列表中不足3人，则会把超时和未超时的，加上一个机器人组成一个队伍
		List<RandomMatchingData> list;
		int size = groupMatchingData.getRandomMatchingSize();
		if (size > 0) {
			list = groupMatchingData.pollRandomMatchingData(size > maxMemberSize ? maxMemberSize : size);
		} else {
			list = new ArrayList<RandomMatchingData>();
		}
		int sizeOfList = list.size();
		if (sizeOfList > 0 && sizeOfList < memberCountOfCurrent) {
			// 只有一个人的时候
			RandomMatchingData data = list.get(0); // 只需要检查第一个有没有超时，因为只要有一个人超时，就必须给他组成一个队伍
			if (isNotTimeout(System.currentTimeMillis(), data.getDeadline()) && !force) {
				if (sizeOfList == 1) {
//					GCompUtil.log("未到时间，移除：{}", data.getUserId());
					groupMatchingData.turnBackRandomMatchingData(data);
					list.remove(data);
				} else {
					for (int i = 0; i < sizeOfList; i++) {
						groupMatchingData.turnBackRandomMatchingData(list.get(i));
					}
					list.clear();
				}
			}
		}
		if (list.size() > 0 && list.size() < maxMemberSize) {
			this.getRandomMember(allMembersOfGroup, list);
		}
		return list;
	}
	
	private void randomMatching(GroupMatchingData groupMatchingDataA, GroupMatchingData groupMatchingDataB) {
//		GCompUtil.log("---------- 帮派争霸，随机匹配开始，负责帮派：{} ----------", this.groupMatchingDatas.keySet());
		
		groupMatchingDataA.beforeRandomMatching(); // 随机匹配前的通知
		groupMatchingDataB.beforeRandomMatching(); // 随机匹配前的通知
		
		if (groupMatchingDataA.getRandomMatchingSize() == 0 && groupMatchingDataB.getRandomMatchingSize() == 0) {
			// 两边都没有人在随机匹配
			return;
		}
		int maxMemberCount = GCompCommonConfig.getMaxMemberCountOfTeam();
		List<RandomMatchingData> listOfGroupMatchingDataA;
		List<RandomMatchingData> listOfGroupMatchingDataB;
		LinkedList<GCompMember> allMemberOfGroupA = this.getAllMembersOfGroup(idOfGroupA);
		LinkedList<GCompMember> allMemberOfGroupB = this.getAllMembersOfGroup(idOfGroupB);
		while (groupMatchingDataA.getRandomMatchingSize() > 0 || groupMatchingDataB.getRandomMatchingSize() > 0) {
			listOfGroupMatchingDataA = this.getRandomMatchingDataList(groupMatchingDataA, allMemberOfGroupA, maxMemberCount, false);
			listOfGroupMatchingDataB = this.getRandomMatchingDataList(groupMatchingDataB, allMemberOfGroupB, maxMemberCount, listOfGroupMatchingDataA.size() > 0);
			if (listOfGroupMatchingDataB.size() > 0 && listOfGroupMatchingDataA.isEmpty() && groupMatchingDataA.getRandomMatchingSize() > 0) {
				listOfGroupMatchingDataA = this.getRandomMatchingDataList(groupMatchingDataA, allMemberOfGroupA, maxMemberCount, true); // 强制获取对方的玩家
			}
			if (listOfGroupMatchingDataA.isEmpty() && listOfGroupMatchingDataB.isEmpty()) {
				break;
			}
			if (listOfGroupMatchingDataA.size() > 0 && listOfGroupMatchingDataB.isEmpty()) {
				this.getRandomMember(allMemberOfGroupB, listOfGroupMatchingDataB);
			} else if (listOfGroupMatchingDataB.size() > 0 && listOfGroupMatchingDataA.isEmpty()) {
				this.getRandomMember(allMemberOfGroupA, listOfGroupMatchingDataA);
			}
			onRandomMatch(listOfGroupMatchingDataA, listOfGroupMatchingDataB);
		}
		
//		GCompUtil.log("---------- 帮派争霸，随机匹配结束，负责帮派：{} ----------", this.groupMatchingDatas.keySet());
	}
	
	private void processRandomMatching() {
		// 随机匹配
		if (this._randomMatchingOn) {
			this.randomMatching(groupMatchingDatas.get(idOfGroupA), groupMatchingDatas.get(idOfGroupB));
		}
	}
	
	private RandomMatchingData createRandomMatchingData(GCompMember member) {
		EmbattlePositionInfo info = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(member.getUserId(), eBattlePositionType.GroupCompetition_VALUE, "");
		List<String> heroIds;
		if (info != null) {
			List<EmbattleHeroPosition> posList = info.getPos();
			heroIds = new ArrayList<String>(posList.size());
			for (int i = 0, size = posList.size(); i < size; i++) {
				heroIds.add(posList.get(i).getId());
			}
		} else {
			List<Hero> heros = FSHeroMgr.getInstance().getMaxFightingHeros(PlayerMgr.getInstance().find(member.getUserId()));
			heroIds = new ArrayList<String>(heros.size());
			for (int i = 0, size = heros.size(); i < size; i++) {
				heroIds.add(heros.get(i).getId());
			}
		}
		
		RandomMatchingData data = RandomMatchingData.createRobotData(member.getUserId(), heroIds);
		return data;
	}
	
	// 匹配机器人
	private boolean robotMatch(LinkedList<GCompMember> members, MatchingData matchingData) {
		if (members.size() < memberCountOfCurrent) {
			return false;
		}
		List<RandomMatchingData> list = new ArrayList<RandomMatchingData>(memberCountOfCurrent);
		if (memberCountOfCurrent == 1) {
			GCompMember member = members.remove(random.nextInt(members.size()));
			members.add(member); // 放到队尾
			list.add(this.createRandomMatchingData(member));
		} else {
			int size = members.size();
			for (int i = 0; i < memberCountOfCurrent; i++) {
				GCompMember member = members.remove(random.nextInt(size));
				members.add(member); // 放到队尾
				list.add(this.createRandomMatchingData(member));
				size--;
			}
		}
		GCompTeam team1 = GCompTeamMgr.getInstance().getTeam(matchingData.getMatchId(), matchingData.getGroupId(), matchingData.getTeamId());
		GCompTeam team2 = GCompTeamMgr.getInstance().createRandomTeam(list);
		this.submitToMatchDataHolder(team1, team2);
		return true;
	}
	
	private void processMatching() {
//		GCompUtil.log("帮派争霸，常规匹配开始，帮派：{}", this.groupMatchingDatas.keySet());
//		if(this.groupMatchingDatas.keySet().contains("10011")) {
//			System.out.println();
//		}
		Queue<MatchingData> queue = this.submitQueue;
		if (queue.size() > 0) {
			GroupMatchingData myGroupMatchingData;
			GroupMatchingData againstGroupMatchingData;
			MatchingData dataMatched;
			List<MatchingData> matched = new ArrayList<MatchingData>(); // 已经匹配到的数据
			int maxMatchingLvFloating = GCompCommonConfig.getMaxMatchingLvFloating();
			synchronized (queue) {
				long currentMillis = System.currentTimeMillis();
				LinkedList<GCompMember> allMembersOfGroupA = this.getAllMembersOfGroup(idOfGroupA); // 所有的帮派A的成员
				LinkedList<GCompMember> allMembersOfGroupB = this.getAllMembersOfGroup(idOfGroupB); // 所有的帮派B的成员
				for (MatchingData md : queue) {
					synchronized (md) {
						myGroupMatchingData = groupMatchingDatas.get(md.getGroupId());
						if (matched.contains(md)) {
							continue;
						} else if (md.isCancel()) {
							matched.add(md);
							myGroupMatchingData.removeMatchingData(md);
							continue;
						}
						againstGroupMatchingData = groupMatchingDatas.get(myGroupMatchingData.getAgainstGroupId());
						dataMatched = null;
						for (int i = 0; i < maxMatchingLvFloating; i++) {
							dataMatched = againstGroupMatchingData.pollMatchingByLv(md.getLv() + i);
							if (dataMatched != null) {
								synchronized (dataMatched) {
									if (dataMatched.isCancel()) {
										// 取消了
										continue;
									} else {
										// 设置匹配上的标识
										dataMatched.setMatched(true);
										break;
									}
								}
							}
						}
						if (dataMatched == null) {
							if (isNotTimeout(currentMillis, md.getDeadline())) {
								// 没有超时，等待下一轮
								continue;
							}
							dataMatched = againstGroupMatchingData.pollBeginWithMaxLv();
						}
						// 再次确认有没有匹配到人
						if (dataMatched == null) {
							// 没有匹配到人，要匹配机器人，要从GCompMemberMgr拿3个人出来
							if (this.robotMatch(md.getGroupId().equals(idOfGroupA) ? allMembersOfGroupB : allMembersOfGroupA, md)) {
								matched.add(md);
								myGroupMatchingData.removeMatchingData(md);
								md.setMatched(true);
							}
						} else {
							md.setMatched(true); // 匹配上了
							matched.add(dataMatched);
							matched.add(md);
							myGroupMatchingData.removeMatchingData(md);
							onMatch(md, dataMatched);
						}
					}
				}
				if (matched.size() > 0) {
					queue.removeAll(matched);
				}
			}
		}
//		GCompUtil.log("帮派争霸，常规匹配结束，帮派：{}", this.groupMatchingDatas.keySet());
	}

	@Override
	public String getName() {
		return nameOfTimerTask;
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		if (_on) {
			try {
				this.processMatching();
				this.processRandomMatching();
			} catch (Exception e) {
				GameLog.error("帮派争霸匹配", "匹配扫描", "异常情况！负责帮派：" + this.idOfGroupA + ", " + this.idOfGroupB + ", 赛事id：" + this.againstId, e);
			}
		}
		return "SUCCESS";
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
		
	}

	@Override
	public void rejected(RejectedExecutionException e) {
		
	}

	@Override
	public boolean isContinue() {
		return _on;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}
}
