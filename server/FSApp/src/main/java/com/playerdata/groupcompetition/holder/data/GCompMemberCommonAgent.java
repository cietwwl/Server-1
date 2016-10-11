package com.playerdata.groupcompetition.holder.data;

import java.util.Arrays;

import com.bm.rank.groupCompetition.killRank.GCompKillRankMgr;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreRankMgr;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinRankMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.groupcompetition.GroupCompetitionBroadcastCenter;
import com.playerdata.groupcompetition.holder.GCompMemberHolder;
import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.rank.ScoreInfoInPrepareMgr;
import com.rwbase.dao.groupcompetition.ContinueWinsBroadcastCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.UserGroupCompetitionScoreRecord;
import com.rwbase.gameworld.GameWorldFactory;

class GCompMemberCommonAgent implements IGCompMemberAgent {
	
	private ContinueWinsBroadcastCfgDAO _continueWinsBroadcastCftDAO;
	
	public GCompMemberCommonAgent() {
		_continueWinsBroadcastCftDAO = ContinueWinsBroadcastCfgDAO.getInstance();
	}

	@Override
	public void resetContinueWins(GCompMember member) {
		member.resetContinueWins();
//		UpdateWinTimesToRankingTask.submit(member);
	}

	@Override
	public void incWins(GCompMember member) {
		member.incWinTimes();
		UpdateWinTimesToRankingTask.submit(member);
	}

	@Override
	public void addScore(GCompMember member, int score) {
		member.updateScore(score);
		if (score > 0) {
			UpdateScoreToRankingTask.submit(member);
		}
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
		Integer pmdId = _continueWinsBroadcastCftDAO.getBroadcastId(member.getContinueWins());
		if (pmdId != null) {
			GroupCompetitionBroadcastCenter.getInstance().addBroadcastMsg(pmdId, Arrays.asList(groupName, member.getUserName(), String.valueOf(addGroupScoreCount)));
		}
	}

	@Override
	public void updateToClient(GCompMember member) {
		GCompMemberHolder.getInstance().syn(PlayerMgr.getInstance().find(member.getUserId()), member);
		UpdateUserGroupCompetitinRecordTask.submit(member);
	}

	private static class UpdateWinTimesToRankingTask implements Runnable {

		private String userId;
		private int totalWins;
		private int continueWins;
		private int maxContinueWins;
		
		static void submit(GCompMember member) {
			UpdateWinTimesToRankingTask task = new UpdateWinTimesToRankingTask();
			task.userId = member.getUserId();
			task.totalWins = member.getTotalWinTimes();
			task.continueWins = member.getContinueWins();
			task.maxContinueWins = member.getMaxContinueWins();
			GameWorldFactory.getGameWorld().asynExecute(task);
		}

		@Override
		public void run() {
			Player player = PlayerMgr.getInstance().find(userId);
			GCompKillRankMgr.addOrUpdateKillRank(PlayerMgr.getInstance().find(userId), totalWins);
			if (maxContinueWins == continueWins) {
				GCompContinueWinRankMgr.addOrUpdateContinueWinRank(player, maxContinueWins);
			}
			ScoreInfoInPrepareMgr.getInstance().updateNewScoreRecord(player);
		}

	}

	private static class UpdateScoreToRankingTask implements Runnable {

		private String userId;
		private int score;
		
		static void submit(GCompMember member) {
			UpdateScoreToRankingTask task = new UpdateScoreToRankingTask();
			task.userId = member.getUserId();
			task.score = member.getScore();
			GameWorldFactory.getGameWorld().asynExecute(task);
		}

		@Override
		public void run() {
			Player player = PlayerMgr.getInstance().find(userId);
			GCompScoreRankMgr.addOrUpdateScoreRank(player, score);
			ScoreInfoInPrepareMgr.getInstance().updateNewScoreRecord(player);
		}
	}
	
	private static class UpdateUserGroupCompetitinRecordTask implements Runnable {
		
		static void submit(GCompMember member) {
			UpdateUserGroupCompetitinRecordTask task = new UpdateUserGroupCompetitinRecordTask();
			task.member = member;
			GameWorldFactory.getGameWorld().asynExecute(task);
		}
		
		private GCompMember member;

		@Override
		public void run() {
			UserGroupCompetitionScoreRecord record = GCompMemberMgr.getInstance().getRecordOfCurrent(member.getUserId());
			if (member.getScore() != record.getScore()) {
				record.setScore(member.getScore());
			}
			if (member.getTotalWinTimes() != record.getTotalWinTimes()) {
				record.setTotalWinTimes(member.getTotalWinTimes());
			}
			if (member.getMaxContinueWins() != record.getMaxContinueWins()) {
				record.setMaxContinueWins(member.getMaxContinueWins());
			}
		}
	}
}
