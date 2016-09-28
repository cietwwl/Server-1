package com.playerdata.groupcompetition.holder.data;

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
	}

	@Override
	public void addScore(GCompMember member, int score) {
		member.updateScore(score);
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
