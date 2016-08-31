package com.playerdata.groupcompetition.quiz;

import com.playerdata.Player;

/**
 * 赛事竞猜管理类
 * @author aken
 */
public class GCompQuizMgr {
	
	private static GCompQuizMgr instance = new GCompQuizMgr();

	public static GCompQuizMgr getInstance() {
		return instance;
	}
	
	/**
	 * 
	 * @param player
	 * @param marchId
	 * @param groupId
	 * @param coinCount
	 */
	public boolean quizForCompetion(Player player, int matchId, String groupId, int coinCount){
		boolean haveQuized = GCompUserQuizItemHolder.getInstance().containsItem(player, matchId);
		if(haveQuized) {
			return false;
		}
		GCompUserQuizItem item = new GCompUserQuizItem();
		item.setId(GCompUserQuizItem.conbineId(player.getUserId(), matchId));
		item.setGroupId(groupId);
		item.setMatchId(matchId);
		item.setCoinCount(coinCount);
		item.setUserID(player.getUserId());
		item.setSessionId(GCompUserQuizItemHolder.getCurrentSessionID());
		GCompUserQuizItemHolder.getInstance().addItem(player, item);
		
		GroupQuizInfo groupQuizInfo = GroupQuizInfoDAO.getInstance().get(GroupQuizInfo.combineId(groupId, GCompUserQuizItemHolder.getCurrentStageID()));
		groupQuizInfo.addTotalQuizPlayer();
		groupQuizInfo.addTotalQuizCoin(coinCount);
		return true;
	}
}
