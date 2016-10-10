package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.playerdata.groupcompetition.GroupCompetitionBroadcastCenter;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.GroupCompetitionRewardCenter;
import com.playerdata.groupcompetition.battle.EventsStatusForBattleCenter;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.holder.GCompDetailInfoMgr;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.holder.GCompFightingRecordMgr;
import com.playerdata.groupcompetition.holder.GCompGroupScoreRankingMgr;
import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.holder.GCompOnlineMemberMgr;
import com.playerdata.groupcompetition.holder.GCompTeamMgr;
import com.playerdata.groupcompetition.matching.GroupCompetitionMatchingCenter;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
import com.playerdata.groupcompetition.quiz.GCompQuizMgr;
import com.playerdata.groupcompetition.rank.GCompRankMgr;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompTips;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

/**
 * 
 * 帮派争霸的赛事
 * 
 * @author CHEN.P
 *
 */
public class GCompEvents {

	private GCEventsType _type; // 赛事类型

	// private boolean _firstOfThisSession; // 是否本届第一个类型的比赛

	/**
	 * 
	 * 构建一个帮派争霸赛事
	 * 
	 * @param groupIds 涉及的帮派id
	 * @param eventsType 当前赛事的状态
	 */
	private GCompEvents() {

	}

	private String getSafely(int index, List<String> list) {
		if (index < list.size()) {
			return list.get(index);
		} else {
			return "";
		}
	}

	private List<IReadOnlyPair<Integer, Integer>> checkAgainstAssignment(List<IReadOnlyPair<Integer, Integer>> againsts, int sizeOfGroup) {
		// 检查againsts是否有内容
		if (againsts == null || againsts.isEmpty()) {
			againsts = new ArrayList<IReadOnlyPair<Integer, Integer>>();
			for (int i = 0; i < sizeOfGroup; i++) {
				againsts.add(Pair.Create(i + 1, (++i) + 1));
			}
		}
		return againsts;
	}

	private void generateAgainstInfo(List<String> groupIds, List<IReadOnlyPair<Integer, Integer>> againsts, GCEventsType eventsType, boolean old) {
		if (!old) {
			againsts = this.checkAgainstAssignment(againsts, groupIds.size()); // 检查对阵关系的安排
			List<GCompAgainst> againstList = new ArrayList<GCompAgainst>(againsts.size());
			int beginPos = GCompUtil.computeBeginIndex(eventsType); // 计算开始索引
			IReadOnlyPair<Integer, Integer> pair;
			String groupId1, groupId2; // 临时变量
			for (int i = 0, size = againsts.size(); i < size; i++, beginPos++) {
				pair = againsts.get(i); // 对阵安排
				// 有可能会轮空；对阵信息是从1开始，而list的索引是从0开始，所以要-1
				groupId1 = this.getSafely(pair.getT1() - 1, groupIds);
				groupId2 = this.getSafely(pair.getT2() - 1, groupIds);
				GCompAgainst against = new GCompAgainst(groupId1, groupId2, eventsType, beginPos);
				againstList.add(against);
			}
			if (eventsType == GCEventsType.FINAL) {
				// 决赛的第一场是冠军争夺
				againstList.get(0).setChampionEvents(true);
			}
			GCompEventsData eventsData = new GCompEventsData();
			eventsData.setAgainsts(againstList);
			eventsData.setCurrentStatus(GCompEventsStatus.NONE);
			eventsData.setEventsType(eventsType);
			eventsData.setRelativeGroupIds(groupIds);
			GCompEventsDataMgr.getInstance().addEvents(eventsData, eventsType);
			GCompQuizMgr.getInstance().groupCompEventsStart(eventsType); // 竞猜模块
		}
	}

	private void initEventsData(List<String> groupIds, List<IReadOnlyPair<Integer, Integer>> againsts, GCEventsType eventsType, boolean old) {
		// 初始化对阵关系
		_type = eventsType;
		this.generateAgainstInfo(groupIds, againsts, eventsType, old);
	}

	// 通知赛事开始
	private void fireEventsStart() {
		GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(_type);
		GroupCompetitionMgr.getInstance().updateCurrenEventstData(_type, eventsData.getRelativeGroupIds());
		GCompDetailInfoMgr.getInstance().onEventsAgainstAssign(eventsData.getAgainsts());
		GCompFightingRecordMgr.getInstance().initRecordList(eventsData.getAgainsts());
		GCompTeamMgr.getInstance().onEventsStart(_type, eventsData.getAgainsts()); // 通知队伍数据管理
		GCompOnlineMemberMgr.getInstance().onEventsStart(_type, eventsData.getRelativeGroupIds()); // 通知在线数据管理
		GCompMemberMgr.getInstance().notifyEventsStart(_type, eventsData.getRelativeGroupIds()); // 通知成员管理器
		// GCompQuizMgr.getInstance().groupCompEventsStart(); // 竞猜模块
		GroupCompetitionMatchingCenter.getInstance().onEventsStart(eventsData.getAgainsts());
		GCompUtil.sendMarquee(GCompTips.getTipsEnterEventsType(_type.chineseName)); // 跑马灯
		GroupCompetitionBroadcastCenter.getInstance().onEventsStart();
		GCompDetailInfoMgr.getInstance().onEventsStart(eventsData.getAgainsts());
	}

	// 通知赛事结束
	private void fireEventsEnd() {
		GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(this._type);
		List<GCompAgainst> againsts = eventsData.getAgainsts();
		for (GCompAgainst against : againsts) {
			GCompQuizMgr.getInstance().groupCompEventsEnd(against.getId(), against.getWinGroupId());
		}
		GCompRankMgr.getInstance().stageEnd(_type);
		GroupCompetitionMgr.getInstance().notifyEventsEnd(_type, againsts);
		GCompOnlineMemberMgr.getInstance().onEventsEnd(_type, againsts);
		GCompFightingRecordMgr.getInstance().endLiveRecord();
		GroupCompetitionRewardCenter.getInstance().notifyEventsFinished(_type, againsts);
		GroupCompetitionBroadcastCenter.getInstance().onEventsEnd();
		GCompGroupScoreRankingMgr.getInstance().onEventsEnd(_type, againsts);
		GCompDetailInfoMgr.getInstance().onEventsEnd(eventsData.getAgainsts());
		GCompMemberMgr.getInstance().notifyEventsEnd();
	}

	// 通知具体赛事的具体节点变化
	private void fireEventsStatusChange(GCompEventsStatus status) {
		GroupCompetitionMgr.getInstance().updateEventsStatus(status);
		switch (status) {
		case PREPARE:
			PrepareAreaMgr.getInstance().prepareStart(GCompEventsDataMgr.getInstance().getEventsData(_type).getRelativeGroupIds());
			break;
		case FINISH:
			PrepareAreaMgr.getInstance().prepareEnd();
			break;
		default:
			break;
		}
		GroupCompetitionMatchingCenter.getInstance().onEventsStatusChange(status);
		EventsStatusForBattleCenter.getInstance().onEventsStatusChange(status);
	}

	/**
	 * 
	 * 把赛事的状态切换到下个状态
	 * 
	 * @return true=切换成功，false=没有下个状态
	 */
	boolean switchToNextStatus() {
		GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(this._type);
		GCompEventsStatus nextStatus = eventsData.getCurrentStatus().getNextStatus();
		if (nextStatus != null) {
			eventsData.setCurrentStatus(nextStatus);
			List<GCompAgainst> againsts = eventsData.getAgainsts();
			for (int i = 0, size = againsts.size(); i < size; i++) {
				againsts.get(i).setCurrentStatus(nextStatus);
			}
			fireEventsStatusChange(nextStatus);
			return true;
		} else {
			// 没有下个状态
			return false;
		}
	}

	/**
	 * 赛事开始 1、通知其他模块赛事开始 2、切换到准备状态
	 */
	public void start() {
		this.fireEventsStart();
		this.switchToNextStatus();
	}

	/**
	 * 赛事结束
	 */
	public void onEnd() {
		GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(this._type);
		List<GCompAgainst> list = eventsData.getAgainsts();
		List<String> winGroupIds = new ArrayList<String>(list.size());
		List<String> loseGroupIds = new ArrayList<String>(list.size());
		for (int i = 0, size = list.size(); i < size; i++) {
			GCompAgainst against = list.get(i);
			IGCGroup groupA = against.getGroupA();
			IGCGroup groupB = against.getGroupB();
			String winGroupId;
			String loseGroupId;
			if (StringUtils.isEmpty(groupA.getGroupId())) {
				// 帮派B轮空
				winGroupId = groupB.getGroupId();
				loseGroupId = groupA.getGroupId();
			} else if (StringUtils.isEmpty(groupB.getGroupId())) {
				// 帮派A轮空
				winGroupId = groupA.getGroupId();
				loseGroupId = groupB.getGroupId();
			} else {
				if (groupA.getGCompScore() >= groupB.getGCompScore()) {
					winGroupId = groupA.getGroupId();
					loseGroupId = groupB.getGroupId();
				} else {
					winGroupId = groupB.getGroupId();
					loseGroupId = groupA.getGroupId();
				}
			}
			winGroupIds.add(winGroupId);
			loseGroupIds.add(loseGroupId);
			against.setWinGroupId(winGroupId);
		}
		eventsData.setWinGroupIds(winGroupIds);
		eventsData.setLostGroupIds(loseGroupIds);
		// if (_type.getNext() == GCEventsType.FINAL) {
		// int beginPos = GCompUtil.computeBeginIndex(GCEventsType.FINAL); // 计算开始索引
		// List<GCompAgainst> next = new ArrayList<GCompAgainst>();
		// next.add(new GCompAgainst(winGroupIds.get(0), winGroupIds.get(1), GCEventsType.FINAL, beginPos));
		// next.add(new GCompAgainst(loseGroupIds.get(0), loseGroupIds.get(1), GCEventsType.FINAL, beginPos + 1));
		// GCompEventsDataMgr.getInstance().setNextMatches(next);
		// }
		GCompEventsDataMgr.getInstance().save();
		if (_type.getNext() != null) { // 提早生成下一组赛事
			List<IReadOnlyPair<Integer, Integer>> againstList = Collections.emptyList();
			List<String> groupIds = new ArrayList<String>(winGroupIds);
			if (_type.getNext() == GCEventsType.FINAL) {
				groupIds.addAll(loseGroupIds);
			}
			this.generateAgainstInfo(groupIds, againstList, _type.getNext(), false);
		}
		this.fireEventsEnd();
	}

	/**
	 * 
	 * 获取胜利的公会id
	 * 
	 * @return
	 */
	public List<String> getWinGroups() {
		GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(this._type);
		return eventsData.getWinGroupIds();
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getLoseGroups() {
		GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(this._type);
		return eventsData.getLoseGroupIds();
	}

	/**
	 * 
	 * 获取当前的赛事状态
	 * 
	 * @return
	 */
	public GCompEventsStatus getCurrentStatus() {
		GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(this._type);
		return eventsData.getCurrentStatus();
	}

	public static class Builder {

		private List<String> _groupIds; // 涉及的groupId
		private GCEventsType _status; // 赛事的状态
		private List<IReadOnlyPair<Integer, Integer>> _againstsInfo; // 对阵信息
		private boolean _old = false;

		// private boolean _firstOfThisSession = false;

		public Builder() {

		}

		public Builder(List<String> groupIds, GCEventsType status) {
			this._groupIds = new ArrayList<String>(groupIds);
			this._status = status;
		}

		public List<String> getGroupIds() {
			return _groupIds;
		}

		public Builder setGroupIds(List<String> pGroupIds) {
			this._groupIds = new ArrayList<String>(pGroupIds);
			return this;
		}

		public GCEventsType getStatus() {
			return _status;
		}

		public Builder setEventsType(GCEventsType pstatus) {
			this._status = pstatus;
			return this;
		}

		public List<IReadOnlyPair<Integer, Integer>> getAgainstsInfo() {
			return _againstsInfo;
		}

		public Builder setAgainstsInfo(List<? extends IReadOnlyPair<Integer, Integer>> pAgainstsInfo) {
			this._againstsInfo = new ArrayList<IReadOnlyPair<Integer, Integer>>(pAgainstsInfo);
			return this;
		}

		public Builder setOld(boolean value) {
			this._old = value;
			return this;
		}

		// public Builder setFirstOfThisSession(boolean value) {
		// this._firstOfThisSession = value;
		// return this;
		// }

		public GCompEvents build() {
			GCompEvents events = new GCompEvents();
			// events._firstOfThisSession = this._firstOfThisSession;
			events.initEventsData(_groupIds, _againstsInfo, _status, _old);
			return events;
		}
	}
}
