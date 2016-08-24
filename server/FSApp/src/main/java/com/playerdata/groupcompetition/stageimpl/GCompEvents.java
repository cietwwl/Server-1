package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.groupcompetition.data.IGCAgainst;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.holder.GCompDetailInfoMgr;
import com.playerdata.groupcompetition.holder.GCompFightingRecordMgr;
import com.playerdata.groupcompetition.holder.GCompMatchDataMgr;
import com.playerdata.groupcompetition.util.GCEventsStatus;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.fsutil.common.Pair;

/**
 * 
 * 帮派争霸的赛事
 * 
 * @author CHEN.P
 *
 */
public class GCompEvents {

	private GCEventsType _type;
	
	/**
	 * 
	 * 构建一个帮派争霸赛事
	 * 
	 * @param groupIds 涉及的帮派id
	 * @param eventsType 当前赛事的状态
	 */
	private GCompEvents(List<String> groupIds, List<Pair<Integer, Integer>> againsts, GCEventsType eventsType) {
		this.initEventsData(groupIds, againsts, eventsType);
	}
	
	private void initEventsData(List<String> groupIds, List<Pair<Integer, Integer>> againsts, GCEventsType eventsType) {
		// 初始化对阵关系
		if(againsts == null || againsts.isEmpty()) {
			againsts = new ArrayList<Pair<Integer,Integer>>();
			for (int i = 0, size = groupIds.size(); i < size; i++) {
				againsts.add(Pair.Create(i + 1, (++i) + 1));
			}
		}
		List<GCompAgainst> againstList = new ArrayList<GCompAgainst>(againsts.size());
		int beginPos = GCompUtil.computeBeginIndex(eventsType);
		Pair<Integer, Integer> pair;
		int index1;
		int index2;
		String groupId1;
		String groupId2;
		for (int i = 0, size = againsts.size(); i < size; i++) {
			pair = againsts.get(i);
			index1 = pair.getT1() - 1;
			index2 = pair.getT2() - 1;
			groupId1 = groupIds.get(index1);
			groupId2 = groupIds.get(index2);
			GCompAgainst against = new GCompAgainst(groupId1, groupId2, eventsType, beginPos);
			againstList.add(against);
			GCompDetailInfoMgr.getInstance().onEventsStart(against.getId(), groupId1, groupId2);
			GCompFightingRecordMgr.getInstance().initRecordList(against.getId());
		}
		GCompEventsData eventsData = new GCompEventsData();
		eventsData.setAgainsts(againstList);
		eventsData.setCurrentStatus(GCEventsStatus.NONE);
		eventsData.setEventsType(eventsType);
		GCompMatchDataMgr.getInstance().addEvents(eventsData, eventsType);
		_type = eventsType;
	}
	
	/**
	 * 
	 * 把赛事的状态切换到下个状态
	 * 
	 * @return true=切换成功，false=没有下个状态
	 */
	boolean switchToNextStatus() {
		GCompEventsData eventsData = GCompMatchDataMgr.getInstance().getEventsData(this._type);
		GCEventsStatus nextStatus = eventsData.getCurrentStatus().getNextStatus();
		if (nextStatus != null) {
			eventsData.setCurrentStatus(nextStatus);
			List<GCompAgainst> againsts = eventsData.getAgainsts();
			for (int i = 0, size = againsts.size(); i < size; i++) {
				againsts.get(i).setCurrentStatus(nextStatus);
			}
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
		this.switchToNextStatus();
	}
	
	/**
	 * 赛事结束
	 */
	public void onEnd() {
		GCompEventsData eventsData = GCompMatchDataMgr.getInstance().getEventsData(this._type);
		List<GCompAgainst> list = eventsData.getAgainsts();
		List<String> winGroupIds = new ArrayList<String>(list.size());
		for (int i = 0, size = list.size(); i < size; i++) {
			IGCAgainst against = list.get(i);
			IGCGroup groupA = against.getGroupA();
			IGCGroup groupB = against.getGroupB();
			winGroupIds.add(groupA.getGCompScore() > groupB.getGCompScore() ? groupA.getGroupId() : groupB.getGroupId());
		}
		eventsData.setWinGroupIds(winGroupIds);
	}
	
	/**
	 * 
	 * 获取胜利的公会id
	 * 
	 * @return
	 */
	public List<String> getWinGroups() {
		GCompEventsData eventsData = GCompMatchDataMgr.getInstance().getEventsData(this._type);
		return eventsData.getWinGroupIds();
	}
	
	/**
	 * 
	 * 获取当前的赛事状态
	 * 
	 * @return
	 */
	public GCEventsStatus getCurrentStatus() {
		GCompEventsData eventsData = GCompMatchDataMgr.getInstance().getEventsData(this._type);
		return eventsData.getCurrentStatus();
	}
	
	public static class Builder {

		private List<String> _groupIds; // 涉及的groupId
		private GCEventsType _status; // 赛事的状态
		private List<Pair<Integer, Integer>> _againstsInfo; // 对阵信息
		
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

		public List<Pair<Integer, Integer>> getAgainstsInfo() {
			return _againstsInfo;
		}

		public Builder setAgainstsInfo(List<Pair<Integer, Integer>> pAgainstsInfo) {
			this._againstsInfo = pAgainstsInfo;
			return this;
		}
		
		public GCompEvents build() {
			GCompEvents events = new GCompEvents(_groupIds, _againstsInfo, _status);
			return events;
		}
	}
}
