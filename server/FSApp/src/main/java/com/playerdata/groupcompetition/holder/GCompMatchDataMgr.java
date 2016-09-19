package com.playerdata.groupcompetition.holder;

import com.playerdata.groupcompetition.holder.data.GCompMatchSynData;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;

public class GCompMatchDataMgr {
	
	private static GCompMatchDataMgr _instance = new GCompMatchDataMgr();
	
	public static GCompMatchDataMgr getInstance() {
		return _instance;
	}
	
	private GCompMatchDataHolder _dataHolder = GCompMatchDataHolder.getInstance();
//	private IBIConsumer<GCMatchSynSingleData, String> _setWinnerConsumer = new SetWinnerConsumer();
//	private IBIConsumer<GCMatchSynSingleData, Pair<String, Integer>> _updateScoreConsumer = new SetScoreConsumer();
	
//	private GCGroupSynData createNewGroupSynData(IGCGroup gcGroup) {
//		Group group = GroupBM.get(gcGroup.getGroupId());
//		GCGroupSynData data = new GCGroupSynData();
//		data.setGroupId(gcGroup.getGroupId()); // 帮派id
//		data.setGroupName(gcGroup.getGroupName()); // 帮派名字
//		data.setLeaderName(group.getGroupMemberMgr().getGroupLeader().getName()); // 帮主的名字
//		data.setAssistantName(""); // 副帮主的名字
//		data.setGCompScore(0); // 当前分数
//		data.setGCompPower(0); // 帮派战斗力
//		data.setHistoryNum(0); // 历史记录条数
//		return data;
//	}
	
//	private <E> void loopAndExecute(int matchId, E arg, IBIConsumer<GCMatchSynSingleData, E> consumer) {
//		List<GCMatchSynSingleData> dataList = _dataHolder.get().getMatches();
//		for (int i = 0, size = dataList.size(); i < size; i++) {
//			GCMatchSynSingleData data = dataList.get(i);
//			if (data.getMatchId() == matchId) {
//				consumer.accept(data, arg);
//				break;
//			}
//		}
//	}
	
	public void onEventStageStart(GCEventsType type) {
		GCompMatchSynData synData = _dataHolder.get();
		synData.clear();
		synData.setMatchNumType(type);
	}
	
	/**
	 * 
	 * 把对阵添加到同步数据中
	 * 
	 * @param againstList
	 * @param eventsType
	 */
	public void addEvents(GCompEventsData eventsData, GCEventsType eventsType) {
		_dataHolder.get().add(eventsType, eventsData);
	}
	
	/**
	 * 
	 * @param eventType
	 * @return
	 */
	public GCompEventsData getEventsData(GCEventsType eventType) {
		return _dataHolder.get().getEventsData(eventType);
	}
	
//	/**
//	 * 
//	 * @param matchId
//	 * @param winnerGroupId
//	 */
//	public void updateWinner(int matchId, String winnerGroupId) {
//		this.loopAndExecute(matchId, winnerGroupId, _setWinnerConsumer);
//	}
//	
//	/**
//	 * 
//	 * @param matchId
//	 * @param groupId
//	 * @param score
//	 */
//	public void updateScore(int matchId, String groupId, int score) {
//		Pair<String, Integer> pair = Pair.Create(groupId, score);
//		this.loopAndExecute(matchId, pair, _updateScoreConsumer);
//	}
//	
//	private static class SetWinnerConsumer implements IBIConsumer<GCMatchSynSingleData, String> {
//
//		@Override
//		public void accept(GCMatchSynSingleData matchData, String winnerId) {
//			matchData.setWinner(winnerId);
//		}
//		
//	}
//	
//	private static class SetScoreConsumer implements IBIConsumer<GCMatchSynSingleData, Pair<String, Integer>> {
//
//		@Override
//		public void accept(GCMatchSynSingleData matchData, Pair<String, Integer> scoreInfo) {
//			String groupId = scoreInfo.getT1();
//			GCGroupSynData group;
//			if (!(group = matchData.getGroupA()).getGroupId().equals(groupId)) {
//				group = matchData.getGroupB();
//			}
//			group.setGCompScore(scoreInfo.getT2());
//		}
//	}
}
