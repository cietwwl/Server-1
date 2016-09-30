package com.playerdata.groupcompetition.stageimpl;

import java.util.ArrayList;
import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.holder.GCompHistoryDataMgr;
import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompTips;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * 帮派争霸海选阶段
 * 
 * @author CHEN.P
 *
 */
public class GCompSelectionStage implements IGCompStage {
	
	private long _stageEndTime; // 阶段结束的时间
	private String _stageCfgId; // 阶段的配置id
	
	public GCompSelectionStage(GroupCompetitionStageCfg cfg) {
		_stageCfgId = cfg.getCfgId();
	}
	
	private List<String> getTopCountGroupsFromRank() {
		// 从排行榜获取排名靠前的N个帮派数据
		List<GCompFightingItem> topGroups = GCompFightingRankMgr.getFightingRankList();
		List<String> groupIds = new ArrayList<String>(topGroups.size());
		int topCount = GCompCommonConfig.getTopCountGroups();
		Group group;
		String groupId;
		int minMembersCount = GCompCommonConfig.getMinMemberCountOfGroup();
		for (int i = 0, size = topGroups.size(); i < size; i++) {
			groupId = topGroups.get(i).getGroupId();
			group = GroupBM.get(groupId);
			if (group.getGroupMemberMgr().getGroupMemberSize() < minMembersCount) {
				GCompUtil.log("帮派：{}，人数少于：{}，不能入围！", group.getGroupBaseDataMgr().getGroupData().getGroupName(), minMembersCount);
				continue;
			}
			groupIds.add(groupId);
			topCount--;
			if (topCount == 0) {
				break;
			}
		}
		GCompUtil.log("----------入围帮派id : " + groupIds + "----------");
		return groupIds;
	}
	
	@Override
	public String getStageCfgId() {
		return _stageCfgId;
	}
	
	@Override
	public GCompStageType getStageType() {
		return GCompStageType.SELECTION;
	}
	
	@Override
	public void onStageStart(IGCompStage preStage) {
		this._stageEndTime = GCompUtil.calculateEndTimeOfStage(_stageCfgId);
		GCompUtil.sendMarquee(GCompTips.getTipsEnterSelectionStage());
	}

	@Override
	public void onStageEnd() {
		GCompHistoryDataMgr.getInstance().setSelectedGroupIds(getTopCountGroupsFromRank());
	}

	@Override
	public long getStageEndTime() {
		return _stageEndTime;
	}
	
}
