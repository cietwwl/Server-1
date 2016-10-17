package com.playerdata.groupcompetition.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.holder.GCompHistoryDataMgr;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class GCompUpdateFightingTask implements IGameTimerTask {
	
	private static int _itr = 15;
	
	public static void submit() {
		FSGameTimerMgr.getInstance().submitMinuteTask(new GCompUpdateFightingTask(), _itr);
	}
	
	private void checkAndAdd(GCGroup gcGroup, Map<String, Long> map, List<GCGroup> list) {
		String groupId = gcGroup.getGroupId();
		if(groupId.length() > 0) {
			if(!map.containsKey(groupId)) {
				map.put(groupId, 0l);
			}
			list.add(gcGroup);
		}
	}
	private void refreshGroupFighting() {
		List<GCompAgainst> list = GCompEventsDataMgr.getInstance().getAllAgainsts();
		list.addAll(GCompHistoryDataMgr.getInstance().getAllAgainsts());
		if (list.size() > 0) {
			Map<String, Long> map = new HashMap<String, Long>();
			List<GCGroup> gcGroupList = new ArrayList<GCGroup>();
			for (int i = 0, size = list.size(); i < size; i++) {
				GCompAgainst temp = list.get(i);
				checkAndAdd(temp.getGroupA(), map, gcGroupList);
				checkAndAdd(temp.getGroupB(), map, gcGroupList);
			}
			boolean isSelection = GroupCompetitionMgr.getInstance().getCurrentStageType() == GCompStageType.SELECTION;
			if (isSelection) {
				for (Iterator<String> keyItr = map.keySet().iterator(); keyItr.hasNext();) {
					String groupId = keyItr.next();
					GCompFightingItem fightingRankItem = GCompFightingRankMgr.getFightingRankItem(groupId);
					if (fightingRankItem != null) {
						map.put(groupId, fightingRankItem.getGroupFight());
					} else {
						map.put(groupId, GCompFightingRankMgr.getGroupFighting(groupId));
					}
				}
			} else {
				for (Iterator<String> keyItr = map.keySet().iterator(); keyItr.hasNext();) {
					String groupId = keyItr.next();
					map.put(groupId, GCompFightingRankMgr.getGroupFighting(groupId));
				}
			}
			GCGroup gcGroup;
			for(int i = 0, size = gcGroupList.size(); i < size; i++) {
				gcGroup = gcGroupList.get(i);
				long fighting = map.get(gcGroup.getGroupId());
				gcGroup.setFighting(fighting);
				
			}
		}
	}

	@Override
	public String getName() {
		return "帮派争霸更新战力";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		try {
			this.refreshGroupFighting();
		} catch (Exception e) {
			e.printStackTrace();
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
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}

}
