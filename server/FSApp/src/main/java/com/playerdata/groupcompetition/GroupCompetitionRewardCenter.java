package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bm.group.GroupBM;
import com.bm.rank.groupCompetition.killRank.GCompKillItem;
import com.bm.rank.groupCompetition.killRank.GCompKillRankMgr;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreItem;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreRankMgr;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinItem;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinRankMgr;
import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.groupcompetition.GCompChampionRewardCfgDAO;
import com.rwbase.dao.groupcompetition.GCompCommonRankRewardCfgBaseDAO;
import com.rwbase.dao.groupcompetition.GCompGroupRewardCfgDAO;
import com.rwbase.dao.groupcompetition.GCompPersonalContinueWinRankRewardDAO;
import com.rwbase.dao.groupcompetition.GCompPersonalKillRankRewardDAO;
import com.rwbase.dao.groupcompetition.GCompScoreRewardCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GCompChampionRewardCfg;
import com.rwbase.dao.groupcompetition.pojo.GCompCommonRankRewardCfg;

/**
 * 
 * 奖励中心
 * 
 * @author CHEN.P
 *
 */
public class GroupCompetitionRewardCenter {

	private static GroupCompetitionRewardCenter _instance = new GroupCompetitionRewardCenter();

	protected GroupCompetitionRewardCenter() {

	}

	public static GroupCompetitionRewardCenter getInstance() {
		return _instance;
	}

	private <E extends GroupMemberDataIF> void sendMailToMembers(List<E> memberList, String emailCfgId, Map<Integer, Integer> rewardMap, List<String> args) {
		String attachment = EmailUtils.createEmailAttachment(rewardMap);
		for (GroupMemberDataIF member : memberList) {
			EmailUtils.sendEmail(member.getUserId(), emailCfgId, attachment, args);
		}
	}

	// 发送奖励给帮派的所有成员
	private void sendRewardToAll(String groupId, GCompCommonRankRewardCfg cfg, List<String> mailArgs) {
		Group group = GroupBM.getInstance().get(groupId);
		List<? extends GroupMemberDataIF> allMembers = group.getGroupMemberMgr().getMemberSortList(null);
		sendMailToMembers(allMembers, cfg.getEmailCfgId(), cfg.getRewardMap(), mailArgs);
	}

	// // 发送帮派积分排名奖励
	// private void sendGroupScoreRankingReward(List<IGCGroup> groups, GCEventsType eventsType) {
	// int rank = 1;
	// GCompScoreRewardCfgDAO scoreRewardCfgDAO = GCompScoreRewardCfgDAO.getInstance();
	// for (IGCGroup group : groups) {
	// List<String> args = Arrays.asList(eventsType.chineseName, String.valueOf(rank));
	// GCompCommonRankRewardCfg cfg = scoreRewardCfgDAO.getByMatchTypeAndRank(eventsType, rank);
	// if (cfg == null) {
	// GCompUtil.log("处理帮派奖励，没有找到合适的奖励，排名：{}，赛事类型是：{}", rank, eventsType);
	// continue;
	// }
	// sendRewardToAll(group.getGroupId(), cfg, args);
	// rank++;
	// }
	// }

	// 处理个人奖励
	private void processPersonalReward(List<String> allMembers, GCEventsType eventsType, GCompCommonRankRewardCfgBaseDAO dao) {
		// Collections.sort(allMembers, comparator);
		int rank = 1;
		for (String userId : allMembers) {
			List<String> args = Arrays.asList(eventsType.chineseName, String.valueOf(rank));
			GCompCommonRankRewardCfg cfg = dao.getByMatchTypeAndRank(eventsType, rank);
			if (cfg == null) {
				GCompUtil.log("处理个人奖励，没有找到合适的奖励，排名：{}，dao是：{}", rank, dao.getClass().getSimpleName());
				continue;
			}
			String attachment = EmailUtils.createEmailAttachment(cfg.getRewardMap());
			String emailCfgId = cfg.getEmailCfgId();
			EmailUtils.sendEmail(userId, emailCfgId, attachment, args);
			rank++;
		}
	}

	// 处理个人击杀排名奖励
	private void processPersonalWinTimesReward(GCEventsType eventsType) {
		List<GCompKillItem> rankList = GCompKillRankMgr.getKillRankList();
		if (rankList.size() > 0) {
			List<String> allUserIds = new ArrayList<String>(rankList.size());
			for (int i = 0, size = rankList.size(); i < size; i++) {
				allUserIds.add(rankList.get(i).getUserId());
			}
			GCompPersonalKillRankRewardDAO dao = GCompPersonalKillRankRewardDAO.getInstance();
			this.processPersonalReward(allUserIds, eventsType, dao);
		}
	}

	// 处理个人连胜排名奖励
	private void processPersonalContinueWinTimesReward(GCEventsType eventsType) {
		List<GCompContinueWinItem> continueWinRankList = GCompContinueWinRankMgr.getContinueWinRankList();
		if (continueWinRankList.size() > 0) {
			List<String> allUserIds = new ArrayList<String>(continueWinRankList.size());
			for (int i = 0, size = continueWinRankList.size(); i < size; i++) {
				allUserIds.add(continueWinRankList.get(i).getUserId());
			}
			GCompPersonalContinueWinRankRewardDAO dao = GCompPersonalContinueWinRankRewardDAO.getInstance();
			this.processPersonalReward(allUserIds, eventsType, dao);
		}
	}

	// 处理个人积分排名奖励
	private void processPersonalScore(GCEventsType eventsType) {
		List<GCompScoreItem> scoreRankList = GCompScoreRankMgr.getScoreRankList();
		if (scoreRankList.size() > 0) {
			List<String> allUserIds = new ArrayList<String>(scoreRankList.size());
			for (int i = 0, size = scoreRankList.size(); i < size; i++) {
				allUserIds.add(scoreRankList.get(i).getUserId());
			}
			GCompScoreRewardCfgDAO dao = GCompScoreRewardCfgDAO.getInstance();
			this.processPersonalReward(allUserIds, eventsType, dao);
		}
	}

	// 发放个人奖励
	private void sendPersonalReward(List<IGCGroup> groups, GCEventsType eventsType) {
		processPersonalWinTimesReward(eventsType); // 击杀奖励
		processPersonalContinueWinTimesReward(eventsType); // 连胜奖励
		processPersonalScore(eventsType);
	}

	private void addFinalGroups(Deque<IGCGroup> queue, IGCAgainst against) {
		IGCGroup loseGroup = against.getWinGroup() == against.getGroupA() ? against.getGroupB() : against.getGroupA();
		IGCGroup winGroup = against.getWinGroup();
		if (loseGroup.getGroupId().length() > 0) {
			queue.add(loseGroup);
		}
		if (winGroup.getGroupId().length() > 0) {
			queue.add(winGroup);
		}
	}

	// 处理最后的排名奖励
	private void processFinalRewards() {
		GCEventsType type = GroupCompetitionMgr.getInstance().getFisrtTypeOfCurrent();
		Deque<IGCGroup> queue = new LinkedList<IGCGroup>();
		Comparator<IGCGroup> cmp = new GCompScoreComparator();
		GCompEventsData data;
		IGCGroup loseGroup;
		while (type != GCEventsType.QUATER) { // 半决赛的四队都会在决赛重新分配
			data = GCompEventsDataMgr.getInstance().getEventsData(type);
			List<GCompAgainst> againstList = data.getAgainsts();
			List<IGCGroup> groups = new ArrayList<IGCGroup>();
			for (GCompAgainst against : againstList) {
				if (against.getWinGroup() == against.getGroupA()) {
					loseGroup = against.getGroupB();
				} else {
					loseGroup = against.getGroupA();
				}
				if (loseGroup.getGroupId().length() > 0) {
					groups.add(loseGroup);
				}
			}
			Collections.sort(groups, cmp);
			queue.addAll(groups);
			type = type.getNext();
		}
		GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(GCEventsType.FINAL);
		addFinalGroups(queue, eventsData.getAgainsts().get(1));
		addFinalGroups(queue, eventsData.getAgainsts().get(0));
		int rank = 1;
		GCompGroupRewardCfgDAO dao = GCompGroupRewardCfgDAO.getInstance();
		List<String> mailArgs;
		for (Iterator<IGCGroup> itr = queue.descendingIterator(); itr.hasNext();) {
			mailArgs = Arrays.asList(String.valueOf(rank));
			GCompCommonRankRewardCfg cfg = dao.getByRank(rank);
			this.sendRewardToAll(itr.next().getGroupId(), cfg, mailArgs);
			rank++;
		}
		this.processChampionReward(queue.getLast());
	}

	private void processChampionReward(IGCGroup championGroup) {
		GCompChampionRewardCfgDAO dao = GCompChampionRewardCfgDAO.getInstance();
		Group group = GroupBM.getInstance().get(championGroup.getGroupId());
		Map<Integer, List<GroupMemberDataIF>> members = group.getGroupMemberMgr().getAllMemberByPost();
		List<String> args = Collections.emptyList();
		for (Iterator<Integer> keyItr = members.keySet().iterator(); keyItr.hasNext();) {
			Integer pos = keyItr.next();
			List<GroupMemberDataIF> memberList = members.get(pos);
			if (members != null && members.size() > 0) {
				GCompChampionRewardCfg cfg = dao.getByPos(pos);
				sendMailToMembers(memberList, cfg.getEmailCfgId(), cfg.getRewardMap(), args);
			}
		}
	}

	/**
	 * 
	 * 通知帮派争霸赛事结束
	 * 
	 * @param eventsType
	 * @param againstsList
	 */
	public void notifyEventsFinished(GCEventsType eventsType, List<GCompAgainst> againstsList) {
		List<IGCGroup> allGroups = GCompUtil.getAllGroups(againstsList, new GCompScoreComparator());
		// sendGroupScoreRankingReward(allGroups, eventsType);
		sendPersonalReward(allGroups, eventsType); // 决赛之前只发送个人奖励
		if (eventsType == GCEventsType.FINAL) {
			this.processFinalRewards();
		}
	}

	/**
	 * 
	 * 积分排序器
	 * 
	 * @author CHEN.P
	 *
	 */
	private static class GCompScoreComparator implements Comparator<IGCGroup> {

		@Override
		public int compare(IGCGroup o1, IGCGroup o2) {
			int o1Score = o1.getGCompScore();
			int o2Score = o2.getGCompScore();
			return o1Score > o2Score ? -1 : (o1Score == o2Score ? 0 : 1);
		}

	}

	// private static class GCompMemberWinTimesComparator implements Comparator<GCompMember> {
	//
	// @Override
	// public int compare(GCompMember o1, GCompMember o2) {
	// int o1TotalWinTimes = o1.getTotalWinTimes();
	// int o2TotalWinTimes = o2.getTotalWinTimes();
	// return o1TotalWinTimes > o2TotalWinTimes ? -1 : (o1TotalWinTimes == o2TotalWinTimes ? 0 : 1);
	// }
	//
	// }
	//
	// private static class GCompMemberContinueWinTimesComparator implements Comparator<GCompMember> {
	//
	// @Override
	// public int compare(GCompMember o1, GCompMember o2) {
	// int o1MaxContinueWins = o1.getMaxContinueWins();
	// int o2MaxContinueWins = o2.getMaxContinueWins();
	// return o1MaxContinueWins > o2MaxContinueWins ? -1 : (o1MaxContinueWins == o2MaxContinueWins ? 0 : 1);
	// }
	//
	// }
}
