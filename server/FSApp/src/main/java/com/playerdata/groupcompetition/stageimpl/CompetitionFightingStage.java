package com.playerdata.groupcompetition.stageimpl;

import java.util.Collections;
import java.util.List;

import com.playerdata.groupcompetition.data.CompetitionStage;
import com.rwbase.dao.group.pojo.Group;

/**
 * 
 * 帮会争霸：赛事阶段
 * 
 * @author CHEN.P
 *
 */
public class CompetitionFightingStage implements CompetitionStage {
	
	private List<Group> getTopCountGroupsFromRank() {
		// 从排行榜获取排名靠前的N个帮派数据
		return Collections.emptyList();
	}
	
	private void moveToState() {
		
	}

	@Override
	public void onStart(CompetitionStage preStage) {
		List<Group> topCountGroups = getTopCountGroupsFromRank();
	}

	@Override
	public void onEnd() {
		
	}

}
