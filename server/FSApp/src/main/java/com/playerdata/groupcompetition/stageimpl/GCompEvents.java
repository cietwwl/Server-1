package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.holder.GCOnlineMemberMgr;
import com.playerdata.groupcompetition.holder.GCompTeamMgr;
import com.playerdata.groupcompetition.holder.GCompDetailInfoMgr;
import com.playerdata.groupcompetition.holder.GCompFightingRecordMgr;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
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
	
	/**
	 * 
	 * 构建一个帮派争霸赛事
	 * 
	 * @param groupIds 涉及的帮派id
	 * @param eventsType 当前赛事的状态
	 */
	private GCompEvents(List<String> groupIds, List<IReadOnlyPair<Integer, Integer>> againsts, GCEventsType eventsType) {
		this.initEventsData(groupIds, againsts, eventsType);
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
	
	private void initEventsData(List<String> groupIds, List<IReadOnlyPair<Integer, Integer>> againsts, GCEventsType eventsType) {
		// 初始化对阵关系
		_type = eventsType;
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
			GCompDetailInfoMgr.getInstance().onEventsAgainstAssign(against.getId(), groupId1, groupId2);
			GCompFightingRecordMgr.getInstance().initRecordList(against.getId());
		}
		GCompEventsData eventsData = new GCompEventsData();
		eventsData.setAgainsts(againstList);
		eventsData.setCurrentStatus(GCompEventsStatus.NONE);
		eventsData.setEventsType(eventsType);
		eventsData.setRelativeGroupIds(groupIds);
		GCompEventsDataMgr.getInstance().addEvents(eventsData, eventsType);
	}
	
	private void fireEventsStart() {
		GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(_type);
		GCompTeamMgr.getInstance().onEventsStart(_type, eventsData.getAgainsts()); // 通知队伍数据管理
		GCOnlineMemberMgr.getInstance().onEventsStart(_type, eventsData.getRelativeGroupIds()); // 通知在线数据管理
		GCompUtil.sendMarquee(GCompTips.getTipsEnterEventsType(_type.chineseName)); // 跑马灯
	}
	
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
	 * 赛事开始
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
				if(groupA.getGCompScore() >= groupB.getGCompScore()) {
					winGroupId = groupA.getGroupId();
					loseGroupId = groupB.getGroupId();
				} else {
					winGroupId = groupB.getGroupId();
					loseGroupId = groupB.getGroupId();
				}
			}
			winGroupIds.add(winGroupId);
			loseGroupIds.add(loseGroupId);
			against.setWinner(winGroupId);
		}
		eventsData.setWinGroupIds(winGroupIds);
		eventsData.setLostGroupIds(loseGroupIds);
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

		public Builder setStatus(GCEventsType pstatus) {
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
		
		public GCompEvents build() {
			GCompEvents events = new GCompEvents(_groupIds, _againstsInfo, _status);
			return events;
		}
	}
}
