package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bm.group.GroupBM;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.groupcompetition.GCompCommonRankRewardCfgBaseDAO;
import com.rwbase.dao.groupcompetition.GCompPersonalContinueWinRankRewardDAO;
import com.rwbase.dao.groupcompetition.GCompPersonalKillRankRewardDAO;
import com.rwbase.dao.groupcompetition.GCompScoreRewardCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GCompCommonRankRewardCfg;

/**
 * 
 * 奖励中心
 * 
 * @author CHEN.P
 *
 */
public class GroupCompetitionRewardCenter {

	private static final GroupCompetitionRewardCenter _INSTANCE = new GroupCompetitionRewardCenter();
	
	protected GroupCompetitionRewardCenter() {
		
	}
	
	public static final GroupCompetitionRewardCenter getInstance() {
		return _INSTANCE;
	}
	
	// 发送奖励给帮派的所有成员
	private void sendRewardToAll(GCEventsType eventsType, int rank, String groupId, GCompCommonRankRewardCfg cfg) {
		Group group = GroupBM.get(groupId);
		List<? extends GroupMemberDataIF> allMembers = group.getGroupMemberMgr().getMemberSortList(null);
		String attachment = EmailUtils.createEmailAttachment(cfg.getRewardMap());
		String emailCfgId = cfg.getEmailCfgId();
		List<String> args = Arrays.asList(eventsType.chineseName, String.valueOf(rank));
		for (GroupMemberDataIF member : allMembers) {
			EmailUtils.sendEmail(member.getUserId(), emailCfgId, attachment, args);
		}
	}
	
	// 发送帮派积分排名奖励
	private void sendGroupScoreRankingReward(List<IGCGroup> groups, GCEventsType eventsType) {		
		int rank = 1;
		GCompScoreRewardCfgDAO scoreRewardCfgDAO = GCompScoreRewardCfgDAO.getInstance();
		for (IGCGroup group : groups) {
			GCompCommonRankRewardCfg cfg = scoreRewardCfgDAO.getByMatchTypeAndRank(eventsType, rank);
			sendRewardToAll(eventsType, rank, group.getGroupId(), cfg);
			rank++;
		}
	}
	
	// 处理个人奖励
	private void processPersonalReward(List<GCompMember> allMembers, Comparator<GCompMember> comparator, GCEventsType eventsType, GCompCommonRankRewardCfgBaseDAO dao) {
		Collections.sort(allMembers, comparator);
		int rank = 1;
		List<String> args = Arrays.asList(eventsType.chineseName, String.valueOf(rank));
		for (GCompMember member : allMembers) {
			GCompCommonRankRewardCfg cfg = dao.getByMatchTypeAndRank(eventsType, rank);
			String attachment = EmailUtils.createEmailAttachment(cfg.getRewardMap());
			String emailCfgId = cfg.getEmailCfgId();
			EmailUtils.sendEmail(member.getUserId(), emailCfgId, attachment, args);
		}
	}
	
	private void processPersonalWinTimesReward(List<GCompMember> allMembers, GCEventsType eventsType) {
		GCompPersonalKillRankRewardDAO dao = GCompPersonalKillRankRewardDAO.getInstance();
		this.processPersonalReward(allMembers, new GCompMemberWinTimesComparator(), eventsType, dao);
	}

	private void processPersonalContinueWinTimesReward(List<GCompMember> allMembers, GCEventsType eventsType) {
		GCompPersonalContinueWinRankRewardDAO dao = GCompPersonalContinueWinRankRewardDAO.getInstance();
		this.processPersonalReward(allMembers, new GCompMemberContinueWinTimesComparator(), eventsType, dao);
	}
	
	private void sendPersonalReward(List<IGCGroup> groups, GCEventsType eventsType) {
		List<GCompMember> allMembers = new ArrayList<GCompMember>(groups.size() * 50);
		for(IGCGroup group : groups) {
			GCompMemberMgr.getInstance().getCopyOfAllMembers(group.getGroupId(), allMembers);
		}
		processPersonalWinTimesReward(allMembers, eventsType);
		processPersonalContinueWinTimesReward(allMembers, eventsType);
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
		try {
		sendGroupScoreRankingReward(allGroups, eventsType);
		}catch (Exception e) {
			e.printStackTrace();
		}sendPersonalReward(allGroups, eventsType);
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
			return o1Score > o2Score ? -1 : (o1Score == o2Score ? 0 : -1);
		}

	}
	
	private static class GCompMemberScoreComparator implements Comparator<GCompMember> {

		@Override
		public int compare(GCompMember o1, GCompMember o2) {
			int o1Score = o1.getScore();
			int o2Score = o2.getScore();
			return o1Score > o2Score ? -1 : (o1Score == o2Score ? 0 : -1);
		}

	}
	
	private static class GCompMemberWinTimesComparator implements Comparator<GCompMember> {

		@Override
		public int compare(GCompMember o1, GCompMember o2) {
			int o1TotalWinTimes = o1.getTotalWinTimes();
			int o2TotalWinTimes = o2.getTotalWinTimes();
			return o1TotalWinTimes > o2TotalWinTimes ? -1 : (o1TotalWinTimes == o2TotalWinTimes ? 0 : -1);
		}

	}
	
	private static class GCompMemberContinueWinTimesComparator implements Comparator<GCompMember> {

		@Override
		public int compare(GCompMember o1, GCompMember o2) {
			int o1MaxContinueWins = o1.getMaxContinueWins();
			int o2MaxContinueWins = o2.getMaxContinueWins();
			return o1MaxContinueWins > o2MaxContinueWins ? -1 : (o1MaxContinueWins == o2MaxContinueWins ? 0 : -1);
		}

	}
}
