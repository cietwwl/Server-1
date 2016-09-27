package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bm.group.GroupBM;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
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
	
	private void sendRewardToAll(String groupId, GCompCommonRankRewardCfg cfg) {
		Group group = GroupBM.get(groupId);
		List<? extends GroupMemberDataIF> allMembers = group.getGroupMemberMgr().getMemberSortList(null);
		String attachment = EmailUtils.createEmailAttachment(cfg.getRewardMap());
		for(GroupMemberDataIF member : allMembers) {
			
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
		List<IGCGroup> allGroups = new ArrayList<IGCGroup>(againstsList.size() * 2);
		for (GCompAgainst against : againstsList) {
			if (against.getGroupA().getGroupId().length() > 0) {
				allGroups.add(against.getGroupA());
			}
			if (against.getGroupB().getGroupId().length() > 0) {
				allGroups.add(against.getGroupB());
			}
		}
		Collections.sort(allGroups, new GCompScoreComparator());
		int rank = 1;
		GCompScoreRewardCfgDAO scoreRewardCfgDAO = GCompScoreRewardCfgDAO.getInstance();
		for (IGCGroup group : allGroups) {
			GCompCommonRankRewardCfg cfg = scoreRewardCfgDAO.getByMatchTypeAndRank(eventsType, rank);
			sendRewardToAll(group.getGroupId(), cfg);
			rank++;
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
			return o1.getGCompScore() > o2.getGCompScore() ? -1 : (o1.getGCompScore() == o2.getGCompScore() ? 0 : -1);
		}

	}
}
