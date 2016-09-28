package com.playerdata.groupcompetition.holder.data;

class GCompMemberRobotAgent implements IGCompMemberAgent {

	@Override
	public void resetContinueWins(GCompMember member) {
		member.resetRobotContinueWins();
	}

	@Override
	public void incWins(GCompMember member) {
		member.incRobotContinueWins();
	}

	@Override
	public void addScore(GCompMember member, int score) {
		// 机器人不需要加积分
	}

	@Override
	public int getContinueWins(GCompMember member) {
		return member.getRobotContinueWins();
	}

	@Override
	public void checkBroadcast(GCompMember member, String groupName, int addGroupScoreCount) {
		
	}

	@Override
	public void updateToClient(GCompMember member) {
		
	}

}
