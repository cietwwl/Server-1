package com.playerdata.groupcompetition.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.groupcompetition.holder.data.GCompGroupTotalScoreRecord;

public class GCompGroupScoreRankingDAO {

	
	private static final GCompGroupScoreRankingDAO _INSTANCE = new GCompGroupScoreRankingDAO();
	
	private final List<GCompGroupTotalScoreRecord> _allRecords = new ArrayList<GCompGroupTotalScoreRecord>();
	
	protected GCompGroupScoreRankingDAO() {}
	
	public static GCompGroupScoreRankingDAO getInstance() {
		return _INSTANCE;
	}
	
	public void removeAll() {
		_allRecords.clear();
	}
	
	public List<GCompGroupTotalScoreRecord> getAll() {
		return new ArrayList<GCompGroupTotalScoreRecord>(_allRecords);
	}
	
	public void addAll(List<GCompGroupTotalScoreRecord> list) {
		_allRecords.addAll(list);
	}
	
	public void add(GCompGroupTotalScoreRecord record) {
		_allRecords.add(record);
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
