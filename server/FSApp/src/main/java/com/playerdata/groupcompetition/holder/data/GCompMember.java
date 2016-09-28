package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.GroupCompetitionBroadcastCenter;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rwbase.dao.groupcompetition.ContinueWinsBroadcastCfgDAO;

@SynClass
public class GCompMember implements Comparable<GCompMember>{

	@IgnoreSynField
	private String userId; // 玩家id
	private int lv; // 玩家等级
	@IgnoreSynField
	private int score; // 积分
	private int totalWinTimes; // 胜利的次数
	private int continueWins; // 连胜次数
	private int maxContinueWins; // 最大连胜次数
	@IgnoreSynField
	private int robotContinueWins; // 作为机器人的连胜次数
	@IgnoreSynField
	private String userName; //
	@IgnoreSynField
	private static IGCompMemberAgent _robotAgent = new GCompMemberRobotAgent();
	@IgnoreSynField
	private static IGCompMemberAgent _commonAgent = new GCompMemberCommonAgent();
	
	public GCompMember(String userId, String userName, int lv) {
		this.userId = userId;
		this.lv = lv;
		this.userName = userName;
	}
	
	public static IGCompMemberAgent getAgent(boolean isRobot) {
		if(isRobot) {
			return _robotAgent;
		} else {
			return _commonAgent;
		}
	}
	
	void updateScore(int offset) {
		this.score += offset;
	}

	int getContinueWins() {
		return continueWins;
	}
	void incWinTimes() {
		this.totalWinTimes++;
		this.continueWins++;
		if (this.maxContinueWins < this.continueWins) {
			this.maxContinueWins = this.continueWins;
		}
	}
	
	void resetContinueWins() {
		this.continueWins = 0;
	}
	
	void incRobotContinueWins() {
		this.robotContinueWins++;
	}
	
	void resetRobotContinueWins() {
		this.robotContinueWins = 0;
	}
	
	int getRobotContinueWins() {
		return this.robotContinueWins;
	}
	
	public int getLv() {
		return lv;
	}

	public String getUserId() {
		return userId;
	}

	public int getScore() {
		return score;
	}
	
	public int getTotalWinTimes() {
		return totalWinTimes;
	}

	public int getMaxContinueWins() {
		return maxContinueWins;
	}	

	@Override
	public int compareTo(GCompMember o) {
		return this.lv > o.lv ? -1 : 1;
	}
	
	public static interface IGCompMemberAgent {
		
		/**
		 * 
		 * 重置连胜
		 * 
		 * @param member
		 */
		public void resetContinueWins(GCompMember member);
		
		/**
		 * 
		 * 增加胜利次数
		 * 
		 * @param member
		 */
		public void incWins(GCompMember member);
		
		/**
		 * 
		 * 增加积分
		 * 
		 * @param member
		 * @param score
		 */
		public void addScore(GCompMember member, int score);
		
		/**
		 * 
		 * 获取连胜次数
		 * 
		 * @param member
		 * @return
		 */
		public int getContinueWins(GCompMember member);
		
		/**
		 * 
		 * @param member
		 */
		public void checkBroadcast(GCompMember member, String groupName, int addGroupScoreCount);
	}
	
	private static class GCompMemberRobotAgent implements IGCompMemberAgent {

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

	}
	
	private static class GCompMemberCommonAgent implements IGCompMemberAgent {

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
				content = GCompUtil.format(content, groupName, member.userName, addGroupScoreCount);
				GroupCompetitionBroadcastCenter.getInstance().addBroadcastMsg(content);
			}
		}

	}
	
}
