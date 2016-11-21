package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.holder.data.GCompDetailInfo;
import com.playerdata.groupcompetition.holder.data.GCompGroupScoreRecord;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.holder.data.GCompPersonalScore;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCompBattleResult;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

public class GCompDetailInfoMgr {

	private static GCompDetailInfoMgr _instance = new GCompDetailInfoMgr();
	
	public static GCompDetailInfoMgr getInstance() {
		return _instance;
	}
	
	private GCompDetailInfoHolder _dataHolder;
	
	protected GCompDetailInfoMgr() {
		_dataHolder = GCompDetailInfoHolder.getInstance();
	}
	
	public void onServerStartComplete() {
		_dataHolder.loadData();
	}
	
	public void onEventsStageStart() {
		_dataHolder.reset();
	}
	
	public void onEventsEnd(List<GCompAgainst> againsts) {
		for (int i = 0, size = againsts.size(); i < size; i++) {
			GCompAgainst against = againsts.get(i);
			GCompDetailInfo detailInfo = _dataHolder.get(against.getId());
			detailInfo.getByGroupId(against.getWinGroupId()).setResult(GCompBattleResult.Win);
			detailInfo.getByGroupId(against.getWinGroupId().equals(against.getGroupA().getGroupId()) ? against.getGroupB().getGroupId() : against.getGroupA().getGroupId())
					.setResult(GCompBattleResult.Lose);
		}
		_dataHolder.update();
	}
	
	public void onEventsStart(List<GCompAgainst> againsts) {
		for (int i = 0, size = againsts.size(); i < size; i++) {
			GCompAgainst against = againsts.get(i);
			GCompDetailInfo detailInfo = _dataHolder.get(against.getId());
			detailInfo.getByGroupId(against.getGroupA().getGroupId()).setResult(GCompBattleResult.Fighting);
			detailInfo.getByGroupId(against.getGroupB().getGroupId()).setResult(GCompBattleResult.Fighting);
		}
		_dataHolder.update();
	}

	public void onEventsAgainstAssign(List<GCompAgainst> againsts) {
		for (int i = 0, size = againsts.size(); i < size; i++) {
			GCompAgainst against = againsts.get(i);
			GCompDetailInfo detailInfo = GCompDetailInfo.createNew(against.getId(), against.getGroupA().getGroupId(), against.getGroupB().getGroupId());
			_dataHolder.add(detailInfo);
		}
		_dataHolder.update();
	}
	
	public boolean sendDetailInfo(int matchId, Player player) {
		return _dataHolder.syn(matchId, player);
	}
	
	private void updateMvp(GCompPersonalScore mvp, GCompMember member) {
		if (!member.getUserName().equals(mvp.getName())) {
			mvp.setName(member.getUserName());
			mvp.setHeadIcon(member.getHeadIcon());
		}
		if (mvp.getContinueWin() != member.getMaxContinueWins()) {
			mvp.setContinueWin(member.getMaxContinueWins());
		}
		mvp.setScore(member.getScore());
		mvp.setGroupScore(member.getGroupScore());
	}
	
	public void onScoreUpdate(int matchId, String groupId, int currentScore, GCompMember member) {
		GCompDetailInfo detailInfo = _dataHolder.get(matchId);
		GCompGroupScoreRecord groupScoreRecord = detailInfo.getByGroupId(groupId);
		synchronized (groupScoreRecord) {
			groupScoreRecord.setScore(currentScore);
		}
		if (member == null) {
			return;
		}
		GCompPersonalScore mvp = detailInfo.getMvp();
		if (mvp == null) {
			synchronized (detailInfo) {
				mvp = detailInfo.getMvp();
				if (mvp == null) {
					mvp = new GCompPersonalScore();
					updateMvp(mvp, member);
				}
				detailInfo.setMvp(mvp);
			}
		} else {
			if (member.getScore() > mvp.getScore()) {
				this.updateMvp(mvp, member);
			}
		}
		GCompUtil.log("更新GCompScore, matchId:{}, groupId:{}, 目前的detailInfo:{}", matchId, groupId, detailInfo);
	}

	public void updateDetailInfo(int matchId, String groupId, String groupName, String iconId) {
		GCompDetailInfo detailInfo = _dataHolder.get(matchId);
		if (detailInfo != null) {
			GCompGroupScoreRecord record = detailInfo.getByGroupId(groupId);
			if (record != null) {
				record.setGroupName(groupName);
				record.setGroupIcon(iconId);
			}
			_dataHolder.update();
		}
	}
	
	public List<IReadOnlyPair<String, Integer>> getNewestScore(int matchId) {
		GCompDetailInfo detailInfo = _dataHolder.get(matchId);
		List<IReadOnlyPair<String, Integer>> list;
		if (detailInfo != null) {
			list = new ArrayList<IReadOnlyPair<String, Integer>>(2);
			List<GCompGroupScoreRecord> scoreList = detailInfo.getGroupScores();
			for (GCompGroupScoreRecord record : scoreList) {
				if (record.getGroupId().length() == 0) {
					continue;
				} else {
					list.add(Pair.Create(record.getGroupId(), record.getScore()));
				}
			}
		} else {
			list = new ArrayList<IReadOnlyPair<String, Integer>>();
		}
		return list;
	}
}
