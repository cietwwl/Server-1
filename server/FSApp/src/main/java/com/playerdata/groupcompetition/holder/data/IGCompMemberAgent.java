package com.playerdata.groupcompetition.holder.data;

public interface IGCompMemberAgent {
	
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
	
	/**
	 * 
	 * 更新到客户端
	 * 
	 */
	public void updateToClient(GCompMember member);
}
