package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.playerdata.groupcompetition.holder.data.GCompDetailInfo;
import com.playerdata.groupcompetition.holder.data.GCompGroupScore;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.holder.data.GCompPersonalScore;
import com.playerdata.groupcompetition.util.GCompUtil;

public class GCompDetailInfoMgr {

	private static final GCompDetailInfoMgr _instance = new GCompDetailInfoMgr();
	
	public static GCompDetailInfoMgr getInstance() {
		return _instance;
	}
	
	private GCompDetailInfoHolder _dataHolder;
	
	protected GCompDetailInfoMgr() {
		_dataHolder = new GCompDetailInfoHolder();
	}

	public void onEventsAgainstAssign(int matchId, String idOfGroupA, String idOfGroupB) {
		GCompDetailInfo detailInfo = GCompDetailInfo.createNew(matchId, idOfGroupA, idOfGroupB);
		_dataHolder.add(detailInfo);
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
		GCompGroupScore groupScore = detailInfo.getByGroupId(groupId);
		synchronized (groupScore) {
			groupScore.setScore(currentScore);
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
