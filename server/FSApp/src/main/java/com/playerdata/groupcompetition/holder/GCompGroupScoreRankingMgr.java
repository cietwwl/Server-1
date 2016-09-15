package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.playerdata.groupcompetition.dao.GCompGroupScoreRankingDAO;
import com.playerdata.groupcompetition.holder.data.GCompGroupScoreRecord;
import com.playerdata.groupcompetition.holder.data.GCompGroupTotalScoreRecord;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;

public class GCompGroupScoreRankingMgr {

	private static final GCompGroupScoreRankingMgr _INSTANCE = new GCompGroupScoreRankingMgr();
	
	private GCompGroupScoreRankingDAO _dao;
	
	protected GCompGroupScoreRankingMgr() {
		_dao = GCompGroupScoreRankingDAO.getInstance();
	}
	
	public static GCompGroupScoreRankingMgr getInstance() {
		return _INSTANCE;
	}
	
	private void addToRanking(String groupId) {
		if (groupId != null && groupId.length() > 0) {
			GCompGroupTotalScoreRecord record;
			if ((record = _dao.getByGroupId(groupId)) == null) {
				GCompFightingItem fightingRankItem = GCompFightingRankMgr.getFightingRankItem(groupId);
				record = new GCompGroupTotalScoreRecord();
				Group group = GroupBM.get(groupId);
				GroupBaseDataIF groupBaseData = group.getGroupBaseDataMgr().getGroupData();
				record.setCurrentRecord(GCompGroupScoreRecord.createNew(groupId, groupBaseData.getGroupName(), groupBaseData.getIconId()));
				record.setFighting(fightingRankItem == null ? 0 : fightingRankItem.getGroupFight());
				_dao.add(record);
			}
		}
	}
	
	public void onNewSessionStart() {
		_dao.removeAll();
	}
	
	public void onEventsStart(List<String> relativeGroupIds) {
		List<GCompGroupTotalScoreRecord> list = _dao.getAll();
		if (list.size() > 0) {
			for (int i = 0, size = list.size(); i < size; i++) {
				list.get(i).getCurrentRecord().setScore(0);
			}
		}
		for (int i = 0, size = relativeGroupIds.size(); i < size; i++) {
			this.addToRanking(relativeGroupIds.get(i));
		}
	}
	
	public void onEvnetsEnd() {
		_dao.sort();
	}
	
	public void onScoreUpdate(String groupId, int currentScore) {
		GCompGroupTotalScoreRecord record = _dao.getByGroupId(groupId);
		if (record != null) {
			synchronized (record) {
				int now = record.getCurrentRecord().getScore();
				int nowTotal = record.getTotalScore() + (currentScore - now);
				record.setTotalScore(nowTotal);
				record.getCurrentRecord().setScore(currentScore);
			}
		}
	}
	
	public List<GCompGroupTotalScoreRecord> getAllRecord() {
		return _dao.getAll();
	}
}
