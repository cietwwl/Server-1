package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rw.fsutil.common.Pair;

public class CompetitionEvents {

	/**
	 * 
	 * 构建一个帮派争霸赛事
	 * 
	 * @param groupIds 涉及的帮派id
	 * @param status 当前赛事的状态
	 */
	private CompetitionEvents(List<String> groupIds, CompetitionEventsStatus status) {

	}
	
	/**
	 * 赛事开始
	 */
	public void start() {
		
	}
	
	/**
	 * 赛事结束
	 */
	public void onEnd() {
		
	}
	
	/**
	 * 
	 * 获取胜利的公会id
	 * 
	 * @return
	 */
	public List<String> getWinGroups() {
		return Collections.emptyList();
	}
	
	public static class Builder {

		private List<String> _groupIds; // 涉及的groupId
		private CompetitionEventsStatus _status; // 赛事的状态
		private List<Pair<Integer, Integer>> _againstsInfo; // 对阵信息
		
		public Builder(List<String> groupIds, CompetitionEventsStatus status) {
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

		public CompetitionEventsStatus getStatus() {
			return _status;
		}

		public Builder setStatus(CompetitionEventsStatus pstatus) {
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
		
		public CompetitionEvents build() {
			CompetitionEvents events = new CompetitionEvents(_groupIds, _status);
			return events;
		}
	}
}
