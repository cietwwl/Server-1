package com.playerdata.groupcompetition.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.groupcompetition.holder.data.GCompGroupTotalScoreRecord;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GCompGroupScoreRankingDAO {

	
	private static GCompGroupScoreRankingDAO _instance = new GCompGroupScoreRankingDAO();
	
	private final List<GCompGroupTotalScoreRecord> _allRecords = new ArrayList<GCompGroupTotalScoreRecord>();
	
	protected GCompGroupScoreRankingDAO() {}
	
	public static GCompGroupScoreRankingDAO getInstance() {
		return _instance;
	}
	
	public void loadData() {
		String value = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_COMPETITION_SCORE_RANKING);
		if(value != null && value.length() > 0) {
			List<GCompGroupTotalScoreRecord> list = JsonUtil.readList(value, GCompGroupTotalScoreRecord.class);
			_allRecords.addAll(list);
		}
	}
	
	public void removeAll() {
		_allRecords.clear();
		update();
	}
	
	public void update() {
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_COMPETITION_SCORE_RANKING, JsonUtil.writeValue(_allRecords));
	}
	
	public List<GCompGroupTotalScoreRecord> getAll() {
		return new ArrayList<GCompGroupTotalScoreRecord>(_allRecords);
	}
	
	public void addAll(List<GCompGroupTotalScoreRecord> list) {
		_allRecords.addAll(list);
		update();
	}
	
	public void add(GCompGroupTotalScoreRecord record) {
		_allRecords.add(record);
		update();
	}
	
	public GCompGroupTotalScoreRecord getByGroupId(String groupId) {
		for (int i = 0, size = _allRecords.size(); i < size; i++) {
			GCompGroupTotalScoreRecord temp = _allRecords.get(i);
			if (temp.getCurrentRecord().getGroupId().equals(groupId)) {
				return temp;
			}
		}
		return null;
	}
	
	public void sort() {
		Collections.sort(_allRecords);
	}
}
