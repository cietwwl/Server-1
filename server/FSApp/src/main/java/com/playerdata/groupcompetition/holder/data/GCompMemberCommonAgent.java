package com.playerdata.groupcompetition.holder.data;

import com.bm.rank.groupCompetition.killRank.GCompKillRankMgr;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreRankMgr;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinRankMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.groupcompetition.GroupCompetitionBroadcastCenter;
import com.playerdata.groupcompetition.holder.GCompMemberHolder;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rwbase.dao.groupcompetition.ContinueWinsBroadcastCfgDAO;

class GCompMemberCommonAgent implements IGCompMemberAgent {

	@Override
	public void resetContinueWins(GCompMember member) {
		member.resetContinueWins();
	}

	@Override
	public void incWins(GCompMember member) {
		member.incWinTimes();
		Player player = PlayerMgr.getInstance().find(member.getUserId());
		GCompKillRankMgr.addOrUpdateKillRank(PlayerMgr.getInstance().find(member.getUserId()), member.getTotalWinTimes());
		if (member.getMaxContinueWins() == member.getContinueWins()) {
			GCompContinueWinRankMgr.addOrUpdateContinueWinRank(player, member.getContinueWins());
		}
	}

	@Override
	public void addScore(GCompMember member, int score) {
		member.updateScore(score);
		GCompScoreRankMgr.addOrUpdateScoreRank(PlayerMgr.getInstance().find(member.getUserId()), member.getScore());
	}
	
	@Override
	public void addGroupScore(GCompMember member, int score) {
		member.updateGroupScore(score);
	}

	@Override
	public int getContinueWins(GCompMember member) {
		return member.getContinueWins();
	}

	@Override
	public void checkBroadcast(GCompMember member, String groupName, int addGroupScoreCount) {
		String content = ContinueWinsBroadcastCfgDAO.getInstance().getBroadcastContent(member.getContinueWins());
		if (content != null) {
			content = GCompUtil.format(content, groupName, member.getUserName(), addGroupScoreCount);
			GroupCompetitionBroadcastCenter.getInstance().addBroadcastMsg(content);
		}
	}

	@Override
	public void updateToClient(GCompMember member) {
		GCompMemberHolder.getInstance().syn(PlayerMgr.getInstance().find(member.getUserId()), member);
	}

}
