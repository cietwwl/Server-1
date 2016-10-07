package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.holder.data.GCompDetailInfo;
import com.playerdata.groupcompetition.holder.data.GCompGroupScoreRecord;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.holder.data.GCompPersonalScore;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCompUtil;

public class GCompDetailInfoMgr {

	private static final GCompDetailInfoMgr _instance = new GCompDetailInfoMgr();
	
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
	
	public void onEventsEnd() {
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
}
