package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.playerdata.groupcompetition.dao.GCompGroupScoreRankingDAO;
import com.playerdata.groupcompetition.holder.data.GCompGroupScoreRecord;
import com.playerdata.groupcompetition.holder.data.GCompGroupTotalScoreRecord;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;

public class GCompGroupScoreRankingMgr {

	private static GCompGroupScoreRankingMgr _instance = new GCompGroupScoreRankingMgr();

	private GCompGroupScoreRankingDAO _dao;

	protected GCompGroupScoreRankingMgr() {
		_dao = GCompGroupScoreRankingDAO.getInstance();
	}

	public static GCompGroupScoreRankingMgr getInstance() {
		return _instance;
	}

	public void serverStartComplete() {
		_dao.loadData();
	}

	public void onNewSessionStart() {
		_dao.removeAll();
	}

	private List<GCompGroupTotalScoreRecord> createRecords(List<GCGroup> gcGroups) {
		List<GCompGroupTotalScoreRecord> recordGroups = new ArrayList<GCompGroupTotalScoreRecord>(gcGroups.size());
		Group group;
		GCGroup gcGroup;
		for (int i = 0, size = gcGroups.size(); i < size; i++) {
			gcGroup = gcGroups.get(i);
			if (gcGroup.getGroupId().length() == 0) {
				continue;
			}
			GCompFightingItem fightingRankItem = GCompFightingRankMgr.getFightingRankItem(gcGroup.getGroupId());
			GCompGroupTotalScoreRecord record = new GCompGroupTotalScoreRecord();
			group = GroupBM.getInstance().get(gcGroup.getGroupId());
			GroupBaseDataIF groupBaseData = group.getGroupBaseDataMgr().getGroupData();
			record.setCurrentRecord(GCompGroupScoreRecord.createNew(groupBaseData.getGroupId(), groupBaseData.getGroupName(), groupBaseData.getIconId()));
			record.getCurrentRecord().setScore(gcGroup.getGCompScore());
			record.setTotalScore(gcGroup.getGCompScore());
			record.setFighting(fightingRankItem == null ? 0 : fightingRankItem.getGroupFight());
			recordGroups.add(record);
		}
		return recordGroups;
	}

	private void addGroupInSeq(GCompAgainst against, List<GCGroup> list) {
		list.add(against.getWinGroup());
		list.add(against.getWinGroup() == against.getGroupA() ? against.getGroupB() : against.getGroupA());
	}

	public void onEventsEnd(GCEventsType type, List<GCompAgainst> againstList) {
		int beginRank = 0;
		int endRank = 0;
		List<GCGroup> gcGroups;
		boolean sort = true;
		switch (type) {
		case TOP_16:
		case TOP_8: {
			gcGroups = new ArrayList<GCGroup>(againstList.size());
			GCompAgainst temp;
			for (int i = 0, size = againstList.size(); i < size; i++) {
				temp = againstList.get(i);
				gcGroups.add(temp.getWinGroup() == temp.getGroupA() ? temp.getGroupB() : temp.getGroupA());
			}
			if (type == GCEventsType.TOP_16) {
				beginRank = 9;
				endRank = 16;
			} else {
				beginRank = 5;
				endRank = 8;
			}
			break;
		}
		default:
		case QUATER:
			return;
		case FINAL: {
			gcGroups = new ArrayList<GCGroup>(againstList.size());
			GCompAgainst finalAgaist = againstList.get(0);
			GCompAgainst secondAgainst;
			if (!finalAgaist.isChampionEvents()) {
				secondAgainst = finalAgaist;
				finalAgaist = againstList.get(1);
			} else {
				secondAgainst = againstList.get(1);
			}
			this.addGroupInSeq(finalAgaist, gcGroups);
			this.addGroupInSeq(secondAgainst, gcGroups);
			sort = false;
			beginRank = 1;
			endRank = 4;
			break;
		}
		}
		List<GCompGroupTotalScoreRecord> recordGroups = this.createRecords(gcGroups);
		if (sort) {
			Collections.sort(recordGroups);
		}
		for (int i = 0, size = recordGroups.size(); i < size; i++, beginRank++) {
			recordGroups.get(i).setRanking(beginRank);
		}
		if (beginRank <= endRank) {
			for (int i = beginRank; i <= endRank; i++) {
				GCompGroupTotalScoreRecord empty = GCompGroupTotalScoreRecord.createEmpty();
				empty.setRanking(i);
				recordGroups.add(empty);
			}
		}
		_dao.addAll(recordGroups);
	}

	public List<GCompGroupTotalScoreRecord> getAllRecord() {
		return _dao.getAll();
	}
}
