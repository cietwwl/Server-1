package com.playerdata.groupcompetition.quiz;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.holder.GCompHistoryDataMgr;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.rw.service.Email.EmailUtils;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.GroupCompetitionProto.RsqNewGuess.Builder;

/**
 * 赛事竞猜管理类
 * @author aken
 */
public class GCompQuizMgr {
	
	private static GCompQuizMgr instance = new GCompQuizMgr();

	public static String REWARD_EMAIL_ID = "16001";
	public static int INIT_BASE_COIN = 500000;
	public static float QUIZ_INIT_RATE = 10.0f;
	public static List<String> QUIZAMOUNT = new ArrayList<String>();
	
	public static GCompQuizMgr getInstance() {
		return instance;
	}
	
	/**
	 * 删除上届比赛所有可竞猜项目
	 * @param matchList
	 */
	public void deleteLastCompQuiz(List<Integer> matchList){
		GroupQuizEventItemDAO.getInstance().removeAllRecord(matchList);
	}
	
	/**
	 * 发起一个竞猜
	 * @param player
	 * @param gcRsp
	 * @param matchId
	 * @param groupId
	 * @param coin
	 */
	public void createNewQuiz(Player player, Builder gcRsp, int matchId, String groupId, int coin) {
		GCQuizEventItem quizEvent = GroupQuizEventItemDAO.getInstance().getQuizInfo(matchId);
		if(null == quizEvent) {
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setTipMsg("竞猜项目不存在");
			return;
		}
		int currentEventSign = GroupCompetitionMgr.getInstance().getCurrentEventsStatus().getSign();
		if(currentEventSign > GCompEventsStatus.PREPARE.getSign()){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setTipMsg("已经开战，不可竞猜");
			return;
		}
		if(StringUtils.isNotBlank(quizEvent.getWinGroupId())){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setTipMsg("胜负已分，无法竞猜");
			return;
		}
		QuizGroupInfo quizGroup = quizEvent.getQuizGroupInfo(groupId);
		if(null == quizGroup){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setTipMsg("竞猜过程中，帮派数据有误");
			return;
		}
		if(!QUIZAMOUNT.contains(String.valueOf(coin))){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setTipMsg("金币数量不符合竞猜规则");
			return;
		}
		boolean enoughCoin = player.getItemBagMgr().checkEnoughItem(eSpecialItemId.Coin.getValue(), coin);
		if(!enoughCoin){
			gcRsp.setRstType(GCResultType.COIN_NOT_ENOUGH);
			gcRsp.setTipMsg("金币不足");
			return;
		}
		boolean quizResult = quizForCompetion(player, matchId, groupId, coin);
		if(!quizResult){
			gcRsp.setRstType(GCResultType.DATA_ERROR);
			gcRsp.setTipMsg("竞猜失败，不要重复竞猜");
			return;
		}
		player.getItemBagMgr().addItem(eSpecialItemId.Coin.getValue(), -coin);
		GCompUserQuizItemHolder.getInstance().synAllData(player);
		GCompUserQuizItemHolder.getInstance().synCanQuizItem(player);
		gcRsp.setRstType(GCResultType.SUCCESS);
	}

	/**
	 * 获取当前的可竞猜项目
	 * @param player
	 * @param gcRsp
	 */
	public void getCanQuizMatch(Player player, com.rwproto.GroupCompetitionProto.RspAllGuessInfo.Builder gcRsp) {
		GCompUserQuizItemHolder.getInstance().synCanQuizItem(player);
		gcRsp.setRstType(GCResultType.SUCCESS);
	}
	
	/**
	 * 阶段开始时，创建竞猜项目
	 */
	public void groupCompEventsStart(){
		List<GCompAgainst> list = GCompHistoryDataMgr.getInstance().getHistoryData().getAgainsts();
		if (list.size() > 0) {
			List<Integer> matchIds = new ArrayList<Integer>();
			for(int i = 0, size = list.size(); i < size; i++) {
				matchIds.add(list.get(i).getId());
			}
			deleteLastCompQuiz(matchIds);
		}
		GCEventsType currentEvent = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		GCompEventsData envetsData = GCompEventsDataMgr.getInstance().getEventsData(currentEvent);
		final int currentSession = GroupCompetitionMgr.getInstance().getCurrentSessionId();
		List<GCompAgainst> currentAgainst = envetsData.getAgainsts();
		int fightNum = 0;
		GroupQuizEventItemDAO dao = GroupQuizEventItemDAO.getInstance();
		for(GCompAgainst against :currentAgainst){
			if (dao.getQuizInfo(against.getId()) != null) {
				continue;
			}
			IGCGroup groupA = against.getGroupA();
			IGCGroup groupB = against.getGroupB();
			if(StringUtils.isBlank(groupA.getGroupId()) || StringUtils.isBlank(groupB.getGroupId())){
				continue;
			}
			GCQuizEventItem quizEvent = new GCQuizEventItem(currentSession, currentEvent.chineseName, 
					++fightNum, against.getId(), INIT_BASE_COIN, groupA, groupB, QUIZ_INIT_RATE);
			GroupQuizEventItemDAO.getInstance().update(quizEvent);
		}
	}
	
	/**
	 * 发放某场比赛获胜方竞猜者的奖励
	 * 
	 * 只发在线玩家的奖励，不在线玩家会在下次登录时发放
	 * @param matchId 结算的比赛id
	 * @param winGroupId 获胜的帮派id
	 */
	public void groupCompEventsEnd(int matchId, String winGroupId){
		GCQuizEventItem quizEvent = GroupQuizEventItemDAO.getInstance().getQuizInfo(matchId);
		if(null == quizEvent){
			return;
		}
		quizEvent.setWinGroupId(winGroupId);
		GroupQuizEventItemDAO.getInstance().update(quizEvent);
		List<Player> onlinePlayers = PlayerMgr.getInstance().getOnlinePlayers();
		for(Player player : onlinePlayers){
			GCompUserQuizItem userQuizItem = GCompUserQuizItemHolder.getInstance().getItem(player, matchId);
			sendQuizReward(player, userQuizItem);
			GCompUserQuizItemHolder.getInstance().synAllData(player);
			GCompUserQuizItemHolder.getInstance().synCanQuizItem(player);
		}
	}
	
	/**
	 * 发放竞猜奖励
	 * @param player
	 * @param item 竞猜的项
	 */
	public void sendQuizReward(Player player, GCompUserQuizItem item){
		if(null == player || null == item || item.isGetReward()){
			return;
		}
		GCQuizEventItem quizEvent = GroupQuizEventItemDAO.getInstance().getQuizInfo(item.getMatchId());
		if(StringUtils.isNotBlank(quizEvent.getWinGroupId())){
			//表示竞猜结果已经出来
			if(StringUtils.equals(quizEvent.getWinGroupId(), item.getGroupId())){
				//最后一次刷新倍率
				quizEvent.refreshRate(true);
				float finalRate = quizEvent.getWinQuizGroup().getRate();
				int rewardCount = (int)(finalRate * item.getCoinCount());
				//猜对，发放奖励
				EmailUtils.sendEmail(player.getUserId(), REWARD_EMAIL_ID, eSpecialItemId.Coin.getValue() + "~" + rewardCount);
			}
			item.setGetReward(true);
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param matchId 比赛的id
	 * @param groupId	所押的帮派id
	 * @param coinCount 竞猜金额（需要在外层读完配置传进来）
	 * @return 如果已经竞猜过，返回false（其它返回true）
	 */
	private boolean quizForCompetion(Player player, int matchId, String groupId, int coinCount){
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
		GCompUserQuizItemHolder.getInstance().synAllData(player);
		//上面记录玩家的竞猜
		//下面统计某个帮派被竞猜的总人数和总金额
		GCQuizEventItem quizEvent = GroupQuizEventItemDAO.getInstance().getQuizInfo(matchId);
		QuizGroupInfo quizGroup = quizEvent.getQuizGroupInfo(groupId);
		quizGroup.addTotalPlayer();
		quizGroup.addTotalCoin(coinCount);
		GroupQuizEventItemDAO.getInstance().update(quizEvent);
		return true;
	}
}
