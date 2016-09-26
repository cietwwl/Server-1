package com.playerdata.groupcompetition.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;

import javax.sound.sampled.TargetDataLine;

import com.playerdata.Hero;
import com.playerdata.PlayerMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompMatchDataHolder;
import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.holder.GCompTeamMgr;
import com.playerdata.groupcompetition.holder.GCompTeamMgr.CreateTeamMemberResultStatus;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.holder.data.GCompTeam.GCompTeamType;
import com.playerdata.groupcompetition.holder.data.GCompTeamMember;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.fsutil.common.Pair;
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
	private GCEventsType eventsType; // 当前的赛事类型
	private GCompEventsStatus currentEventsStatus;
	
	private static int _matchingItr = 20; // 匹配的间隔
	
	AgainstMatchingTask(int pAgainstId, String pIdOfGroupA, String pIdOfGroupB) {
		groupMatchingDatas = new HashMap<String, GroupMatchingData>(2, 1.5f);
		submitQueue = new ConcurrentLinkedQueue<MatchingData>();
		groupMatchingDatas.put(pIdOfGroupA, new GroupMatchingData(pIdOfGroupA, pIdOfGroupB));
		groupMatchingDatas.put(pIdOfGroupB, new GroupMatchingData(pIdOfGroupB, pIdOfGroupA));
		againstId = pAgainstId;
		this.idOfGroupA = pIdOfGroupA;
		this.idOfGroupB = pIdOfGroupB;
		this.nameOfTimerTask = "帮派争霸匹配任务：" + againstId;
		this.eventsType = GroupCompetitionMgr.getInstance().getCurrentEventsType();
	}
	
	void addMatching(String groupId, GCompTeam team) {
		GroupMatchingData groupMatchingData = groupMatchingDatas.get(groupId);
		if (!groupMatchingData.contains(team.getTeamId())) {
			MatchingData md = new MatchingData(team, groupId, againstId);
			groupMatchingData.addMatchingData(md);
			synchronized (submitQueue) {
				submitQueue.add(md);
			}
		} else {
			synchronized (submitQueue) {
				MatchingData md = groupMatchingData.get(team);
				md.setCancel(false);
			}
		}
	}
	
	void cancelMatching(String groupId, GCompTeam team) {
		GroupMatchingData groupMatchindData = this.groupMatchingDatas.get(groupId);
		Queue<MatchingData> matchingDataQueue = groupMatchindData.getMatchingQueue(team.getLv());
		synchronized (matchingDataQueue) {
			for(MatchingData data : matchingDataQueue) {
				if(data.getTeamId().equals(team.getTeamId())) {
					data.setCancel(true);
				}
			}
		}
	}
	
	void beforePersonalEvents() {
		GCompUtil.log("---------- 帮派争霸，个人匹配任务前的通知！ ----------");
		_randomMatchingOn = false;
		submitQueue.clear();
		Set<String> keySet = groupMatchingDatas.keySet();
		for(String key : keySet) {
			groupMatchingDatas.get(key).clear();
		}
	}
	
	void start() {
		GCompUtil.log("---------- 帮派争霸，匹配任务开始，负责帮派：{}", this.groupMatchingDatas.keySet().toString());
		if (this.idOfGroupA.length() == 0 || this.idOfGroupB.length() == 0) {
			GCompUtil.log("---------- 帮派争霸，检测到轮空的情况，不提交匹配任务！负责帮派：{}", this.groupMatchingDatas.keySet().toString());
			return;
		}
		this._on = true;
		FSGameTimerMgr.getInstance().submitSecondTask(this, _matchingItr);
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

	private GCompTeam createRandomTeam(List<RandomMatchingData> dataList) {
		RandomMatchingData r = dataList.get(0);
		Pair<CreateTeamMemberResultStatus, GCompTeamMember> createResult = GCompTeamMgr.getInstance().createTeamMember(PlayerMgr.getInstance().find(r.getUserId()), r.getHeroIds(), true);
		if (createResult.getT1() != CreateTeamMemberResultStatus.SUCCESS) {
			return null;
		}
		GCompTeamMember leader = createResult.getT2();
		if (dataList.size() > 1) {
			GCompTeamMember[] members = new GCompTeamMember[dataList.size() - 1];
			for (int i = 1, size = dataList.size(); i < size; i++) {
				r = dataList.get(i);
				createResult = GCompTeamMgr.getInstance().createTeamMember(PlayerMgr.getInstance().find(r.getUserId()), r.getHeroIds(), false);
				if (createResult.getT1() != CreateTeamMemberResultStatus.SUCCESS) {
					return null;
				}
				members[i - 1] = createResult.getT2();
			}
			return GCompTeam.createNewTeam(UUID.randomUUID().toString(), GCompTeamType.MULTIPLE_PLAYERS, leader, members);
		} else {
			return GCompTeam.createNewTeam(UUID.randomUUID().toString(), GCompTeamType.SINGLE_PLAYER, leader);
		}
	}
	
	private void onRandomMatch(List<RandomMatchingData> list1, List<RandomMatchingData> list2) {
		GCompTeam team1 = this.createRandomTeam(list1);
		GCompTeam team2 = this.createRandomTeam(list2);
		if (team1 != null && team2 != null) {
			this.submitToMatchDataHolder(team1, team2);
		}
	}
	
	private void getRandomMember(Queue<GCompMember> memberList, List<RandomMatchingData> targetList) {
		int size = memberList.size();
		int maxSize = GCompCommonConfig.getMaxMemberCountOfTeam();
		if (targetList.size() < maxSize) {
			while (size > 0) {
				GCompMember member = memberList.poll();
				size--;
				memberList.add(member);
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
	
	private void randomMatching(GroupMatchingData gmd1, GroupMatchingData gmd2) {
		GCompUtil.log("---------- 帮派争霸，随机匹配开始，负责帮派：{} ----------", this.groupMatchingDatas.keySet());
		gmd1.beforeRandomMatching();
		gmd2.beforeRandomMatching();
		int maxMemberCount = GCompCommonConfig.getMaxMemberCountOfTeam();
		if (gmd1.getRandomMatchingSize() < maxMemberCount || gmd2.getRandomMatchingSize() < maxMemberCount) {
			return;
		}
		int sizeOfGmd1 = gmd1.getRandomMatchingSize();
		int sizeOfGmd2 = gmd2.getRandomMatchingSize();
		List<RandomMatchingData> listOfGmd1;
		List<RandomMatchingData> listOfGmd2;
		Queue<GCompMember> allMemberOfGroupA = new LinkedList<GCompMember>();
		Queue<GCompMember> allMemberOfGroupB = new LinkedList<GCompMember>();
		GCompMemberMgr.getInstance().getCopyOfAllMembers(idOfGroupA, allMemberOfGroupA);
		GCompMemberMgr.getInstance().getCopyOfAllMembers(idOfGroupB, allMemberOfGroupB);
		while (sizeOfGmd1 > 0 || sizeOfGmd2 > 0) {
			if (sizeOfGmd1 > 0) {
				listOfGmd1 = gmd1.pollRandomMatchingData(sizeOfGmd1 > maxMemberCount ? maxMemberCount : sizeOfGmd1);
			} else {
				listOfGmd1 = new ArrayList<RandomMatchingData>();
			}
			if (sizeOfGmd2 > 0) {
				listOfGmd2 = gmd2.pollRandomMatchingData(sizeOfGmd2 > maxMemberCount ? maxMemberCount : sizeOfGmd2);
			} else {
				listOfGmd2 = new ArrayList<RandomMatchingData>();
			}
			if (listOfGmd1.size() < maxMemberCount) {
				this.getRandomMember(allMemberOfGroupA, listOfGmd1);
			}
			if (listOfGmd2.size() < maxMemberCount) {
				this.getRandomMember(allMemberOfGroupB, listOfGmd2);
			}
			onRandomMatch(listOfGmd1, listOfGmd2);
			sizeOfGmd1 = gmd1.getRandomMatchingSize();
			sizeOfGmd2 = gmd2.getRandomMatchingSize();
		}
		GCompUtil.log("---------- 帮派争霸，随机匹配结束，负责帮派：{} ----------", this.groupMatchingDatas.keySet());
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
			for (EmbattleHeroPosition h : posList) {
				heroIds.add(h.getId());
			}
		} else {
			List<Hero> heros = FSHeroMgr.getInstance().getMaxFightingHeros(PlayerMgr.getInstance().find(member.getUserId()));
			heroIds = new ArrayList<String>(heros.size());
			for (Hero h : heros) {
				heroIds.add(h.getId());
			}
		}
		return new RandomMatchingData(member.getUserId(), heroIds);
	}
	
	// 匹配机器人
	private boolean robotMatch(Queue<GCompMember> members, MatchingData matchingData) {
		int size = this.currentEventsStatus == GCompEventsStatus.TEAM_EVENTS ? GCompCommonConfig.getMaxMemberCountOfTeam() : 1;
		if (members.size() < size) {
			return false;
		}
		List<RandomMatchingData> list = new ArrayList<RandomMatchingData>(size);
		for (int i = 0; i < size; i++) {
			GCompMember member = members.poll();
			members.add(member); // 放到队尾
			list.add(this.createRandomMatchingData(member));
		}
		GCompTeam team1 = GCompTeamMgr.getInstance().getTeam(matchingData.getMatchId(), matchingData.getGroupId(), matchingData.getTeamId());
		GCompTeam team2 = createRandomTeam(list);
		this.submitToMatchDataHolder(team1, team2);
		return true;
	}
	
	private void processMatching() {
		GCompUtil.log("帮派争霸，常规匹配开始，帮派：{}", this.groupMatchingDatas.keySet());
//		if(this.groupMatchingDatas.keySet().contains("10011")) {
//			System.out.println();
//		}
		Queue<MatchingData> queue = this.submitQueue;
		if (queue.size() > 0) {
			GroupMatchingData gmd;
			GroupMatchingData againstGmd;
			MatchingData dataMatched;
			List<MatchingData> matched = new ArrayList<MatchingData>();
			int maxMatchingLvFloating = GCompCommonConfig.getMaxMatchingLvFloating();
			synchronized (queue) {
				Queue<GCompMember> allMembersOfGmd1 = new LinkedList<GCompMember>();
				Queue<GCompMember> allMembersOfGmd2 = new LinkedList<GCompMember>();
				GCompMemberMgr.getInstance().getCopyOfAllMembers(idOfGroupA, allMembersOfGmd1); // 所有的帮派A的成员
				GCompMemberMgr.getInstance().getCopyOfAllMembers(idOfGroupA, allMembersOfGmd2); // 所有的帮派B的成员
				for (MatchingData md : queue) {
					if (matched.contains(md)) {
						continue;
					} else if (md.isCancel()) {
						matched.add(md);
						continue;
					}
					gmd = groupMatchingDatas.get(md.getGroupId());
					againstGmd = groupMatchingDatas.get(gmd.getAgainstGroupId());
					dataMatched = null;
					for (int i = 0; i < maxMatchingLvFloating; i++) {
						dataMatched = againstGmd.pollMatchingByLv(md.getLv() + i);
						if (dataMatched != null) {
							break;
						}
					}
					if (dataMatched == null) {
						// 没有匹配到人，要匹配机器人，要从GCompMemberMgr拿3个人出来
						if(this.robotMatch(md.getGroupId().equals(idOfGroupA) ? allMembersOfGmd2 : allMembersOfGmd1, md)) {
							gmd.removeMatchingData(md);
						}
					} else {
						matched.add(dataMatched);
						matched.add(md);
						gmd.removeMatchingData(md);
						onMatch(md, dataMatched);
					}
				}
				if (matched.size() > 0) {
					queue.removeAll(matched);
				}
			}
		}
		GCompUtil.log("帮派争霸，常规匹配结束，帮派：{}", this.groupMatchingDatas.keySet());
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
				e.printStackTrace();
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
