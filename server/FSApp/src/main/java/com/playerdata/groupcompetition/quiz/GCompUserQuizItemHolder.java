package com.playerdata.groupcompetition.quiz;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompUserQuizItemHolder {
	
	final private eSynType selfQuizSynType = eSynType.GCompSelfGuess;
	final private eSynType quizDetailSynType = eSynType.GCompSelfGuessDetail;
	final private eSynType canQuizSynType = eSynType.GCompCanGuessItem;
	
	private static GCompUserQuizItemHolder instance = new GCompUserQuizItemHolder();

	public static GCompUserQuizItemHolder getInstance() {
		return instance;
	}
	
	/**
	 * 检查是否已竞猜
	 * @param player
	 * @param item
	 */
	public boolean containsItem(Player player, int matchId){
		return getItemStore(player.getUserId()).getItem(player.getUserId() + "_" + matchId) == null;
	}
	
	/**
	 * 检查是否已竞猜
	 * @param player
	 * @param item
	 */
	public GCompUserQuizItem getItem(Player player, int matchId){
		return getItemStore(player.getUserId()).getItem(player.getUserId() + "_" + matchId);
	}
	
	/**
	 * 添加一条压标信息
	 * @param player
	 * @param item
	 * @return
	 */
	public boolean addItem(Player player, GCompUserQuizItem item){
		return getItemStore(player.getUserId()).addItem(item);
	}
	
	/**
	 * 同步个人的所有竞猜信息
	 * @param player
	 */
	public void synAllData(Player player){
		List<GCompUserQuizItem> itemList = getItemList(player.getUserId());
		List<GCQuizEventItem> eventList = new ArrayList<GCQuizEventItem>();
		for(GCompUserQuizItem item: itemList){
			GCompQuizMgr.getInstance().sendQuizReward(player, item);
			GCQuizEventItem quizEventItem = GroupQuizEventItemDAO.getInstance().getQuizInfo(item.getMatchId());
			if(null != quizEventItem) {
				eventList.add(quizEventItem);
			}
		}
		if(!itemList.isEmpty()){
			ClientDataSynMgr.synDataList(player, itemList, selfQuizSynType, eSynOpType.UPDATE_LIST);
			ClientDataSynMgr.synDataList(player, eventList, quizDetailSynType, eSynOpType.UPDATE_LIST);
		}else{
			GCompUserQuizItem testItem = new GCompUserQuizItem();
			testItem.setId(player.getUserId() + "_" + 123);
			testItem.setCoinCount(1000);
			testItem.setMatchId(123);
			testItem.setGroupId("99998");
			testItem.setUserID(player.getUserId());
			itemList.add(testItem);
			
			QuizGroupInfo groupA = new QuizGroupInfo("groupId001", "groupName001", "groupIcon001", 10.0f);
			QuizGroupInfo groupB = new QuizGroupInfo("groupId002", "groupName002", "groupIcon002", 10.0f);
			GCQuizEventItem eventItem1 = new GCQuizEventItem();
			eventItem1.setBaseCoin(50000);
			eventItem1.setMatchId(123);
			eventItem1.setWinGroupId("");
			eventItem1.setGroupA(groupA);
			eventItem1.setGroupB(groupB);
			eventList.add(eventItem1);
			ClientDataSynMgr.synDataList(player, itemList, selfQuizSynType, eSynOpType.UPDATE_LIST);
			ClientDataSynMgr.synDataList(player, eventList, quizDetailSynType, eSynOpType.UPDATE_LIST);
		}
	}
	
	/**
	 * 同步所有可竞猜的项
	 * @param player
	 */
	public void synCanQuizItem(Player player){
		List<GCQuizEventItem> itemList =  getCurrentFightForQuiz();
		if(itemList != null && !itemList.isEmpty()) {
			ClientDataSynMgr.synDataList(player, itemList, canQuizSynType, eSynOpType.UPDATE_LIST);
		}else{
			QuizGroupInfo groupA = new QuizGroupInfo("groupId001", "groupName001", "groupIcon001", 10.0f);
			QuizGroupInfo groupB = new QuizGroupInfo("groupId002", "groupName002", "groupIcon002", 10.0f);
			GCQuizEventItem eventItem1 = new GCQuizEventItem();
			eventItem1.setBaseCoin(50000);
			eventItem1.setMatchId(123);
			eventItem1.setWinGroupId("");
			eventItem1.setGroupA(groupA);
			eventItem1.setGroupB(groupB);
			itemList.add(eventItem1);
			
			QuizGroupInfo groupC = new QuizGroupInfo("groupId003", "groupName003", "groupIcon003", 10.0f);
			QuizGroupInfo groupD = new QuizGroupInfo("groupId004", "groupName004", "groupIcon004", 10.0f);
			GCQuizEventItem eventItem2 = new GCQuizEventItem();
			eventItem2.setBaseCoin(50000);
			eventItem2.setMatchId(124);
			eventItem2.setWinGroupId("");
			eventItem2.setGroupA(groupC);
			eventItem2.setGroupB(groupD);
			itemList.add(eventItem2);
			ClientDataSynMgr.synDataList(player, itemList, canQuizSynType, eSynOpType.UPDATE_LIST);
		}
	}
	
	/**
	 * 获取一个玩家所有的竞猜项目
	 * 如果到了新的一届，会清除老一届的数据
	 * @param userId
	 * @return
	 */
	public List<GCompUserQuizItem> getItemList(String userId){
		List<GCompUserQuizItem> itemList = new ArrayList<GCompUserQuizItem>();
		MapItemStore<GCompUserQuizItem> mapItem = getItemStore(userId);
		if(null == mapItem){
			return itemList;
		}
		boolean needClear = false;
		Enumeration<GCompUserQuizItem> itemEnum = mapItem.getEnum();
		while(itemEnum.hasMoreElements()){
			GCompUserQuizItem item = itemEnum.nextElement();
			if(item.getSessionId() < getCurrentSessionID()){
				needClear = true;
				break;
			}
			itemList.add(item);
		}
		if(needClear){
			mapItem.clearAllRecords();
		}
		return itemList;
	}
	
	private MapItemStore<GCompUserQuizItem> getItemStore(String userId) {
		MapItemStoreCache<GCompUserQuizItem> cache = MapItemStoreFactory.getGCompQuizItemCache();
		return cache.getMapItemStore(userId, GCompUserQuizItem.class);
	}

	/**
	 * 获取当前阶段可以竞猜的项目
	 * @return
	 */
	private List<GCQuizEventItem> getCurrentFightForQuiz(){
		List<GCQuizEventItem> result = new ArrayList<GCQuizEventItem>();
		GCEventsType currentEvent = GroupCompetitionMgr.getInstance().getCurrentEventsType();
		GCompEventsData envetsData = GCompEventsDataMgr.getInstance().getEventsData(currentEvent);
		List<GCompAgainst> currentAgainst = envetsData.getAgainsts();
		for(GCompAgainst against :currentAgainst){
			GCQuizEventItem quizEvent = GroupQuizEventItemDAO.getInstance().getQuizInfo(against.getId());
			if(null != quizEvent){
				result.add(quizEvent);
			}
		}
		return result;
	}
	
	/**
	 * 获取当前赛事是第几届
	 * @return
	 */
	public static int getCurrentSessionID(){
		return 0;
	}
}
