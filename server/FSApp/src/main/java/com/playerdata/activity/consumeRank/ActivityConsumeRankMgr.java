package com.playerdata.activity.consumeRank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bm.rank.RankType;
import com.bm.rank.consume.ConsumeComparable;
import com.bm.rank.consume.ConsumeRankMgr;
import com.bm.rank.consume.RankingConsumeData;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.consumeRank.cfg.ActivityConsumeRankSubCfg;
import com.playerdata.activity.consumeRank.cfg.ActivityConsumeRankSubCfgDAO;
import com.playerdata.activity.consumeRank.data.ActivityConsumeRankItem;
import com.playerdata.activity.consumeRank.data.ActivityConsumeRankItemHolder;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.Email.EmailUtils;
import com.rwproto.ActivityChargeRankProto.ActivityCommonRspMsg;
import com.rwproto.ActivityChargeRankProto.RankItem;

public class ActivityConsumeRankMgr extends AbstractActivityMgr<ActivityConsumeRankItem> {
	
	private static final int ACTIVITY_INDEX_BEGIN = 180000;
	private static final int ACTIVITY_INDEX_END = 190000;

	private static ActivityConsumeRankMgr instance = new ActivityConsumeRankMgr();
	
	public static ActivityConsumeRankMgr getInstance() {
		return instance;
	}

	/**
	 * 添加完成的进度
	 * 
	 * @param player
	 * @param count
	 */
	public void addFinishCount(Player player, int count) {
		ActivityConsumeRankItemHolder holder = ActivityConsumeRankItemHolder.getInstance();
		List<ActivityConsumeRankItem> items = holder.getItemList(player.getUserId());
		if (null == items || items.isEmpty())
			return;
		for (ActivityConsumeRankItem item : items) {
			item.setFinishCount(item.getFinishCount() + count);
			holder.updateItem(player, item);
			//该榜目前不支持两个活动同时开启（只有一个榜）
			ConsumeRankMgr.addOrUpdateConsumeRank(player, item.getFinishCount());
		}
		holder.synAllData(player);
	}

	@Override
	public void activityStartHandler(ActivityCfgIF cfg){
		Ranking<ConsumeComparable, RankingConsumeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CONSUME_RANK);
		if(null != ranking){
			ranking.clear();
		}
	}
	
	@Override
	public void activityEndHandler(ActivityCfgIF cfg){
		int dispatchingRank = 0;  //记录正在发放奖励的排名，用做异常的时候查找出错点
		String dispatchingUser = "0";  //记录正在发放奖励的角色id，用做异常的时候查找出错点
		Ranking<ConsumeComparable, RankingConsumeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CONSUME_RANK);
		try {
			EnumerateList<? extends MomentRankingEntry<ConsumeComparable, RankingConsumeData>> it = ranking.getEntriesEnumeration();
			ActivityConsumeRankSubCfgDAO rankCfgDAO = ActivityConsumeRankSubCfgDAO.getInstance();
			int rewardCfgCount = rankCfgDAO.getEntryCount();
			for (int i = 1; i <= rewardCfgCount; i++) {
				int startRank = 1;
				if (i != 1){
					startRank = rankCfgDAO.getCfgById(String.valueOf(i - 1)).getRankEnd() + 1;
				}
				ActivityConsumeRankSubCfg rewardCfg = rankCfgDAO.getCfgById(String.valueOf(i));
				int endRank = rewardCfg.getRankEnd();
				for (int j = startRank; j <= endRank; j++) {
					dispatchingRank = j;
					if (it.hasMoreElements()) {
						MomentRankingEntry<ConsumeComparable, RankingConsumeData> entry = it.nextElement();
						dispatchingUser = entry.getExtendedAttribute().getUserId();
						EmailUtils.sendEmail(dispatchingUser, String.valueOf(rewardCfg.getEmailId()), rewardCfg.getReward());
					}else{
						return;
					}
				}
			}
		} catch (Exception ex) {
			GameLog.error(LogModule.ComActChargeRank, "ActivityConsumeRankMgr", String.format("expireActivityHandler, 给角色[%s]发放消费排行奖励[%s]的时候出现异常", dispatchingUser, dispatchingRank), ex);
		} finally {
			ranking.clear();
		}
	}
	
	/**
	 * 获取消费排行榜
	 * @param player
	 * @param fromRank
	 * @param toRank
	 */
	public void getConsumeRank(Player player, int fromRank, int toRank, ActivityCommonRspMsg.Builder response){
		List<RankingConsumeData> consumeRank = ConsumeRankMgr.getRankIndex(fromRank, toRank);
		for(RankingConsumeData data : consumeRank){
			RankItem.Builder rankItemBuilder = RankItem.newBuilder();
			rankItemBuilder.setUserId(data.getUserId());
			rankItemBuilder.setUserName(data.getUserName());
			rankItemBuilder.setCount(data.getConsume());
			response.addItems(rankItemBuilder.build());
		}
		response.setSelfRank(ConsumeRankMgr.getRankIndex(player.getUserId()));
	}

	@Override
	protected List<String> checkRedPoint(Player player, ActivityConsumeRankItem item) {
		List<String> redPointList = new ArrayList<String>();
		if (!item.isHasViewed()) {
			redPointList.add(String.valueOf(item.getCfgId()));
		}
		return Collections.emptyList();
		//return redPointList;
	}
	
	protected UserActivityChecker<ActivityConsumeRankItem> getHolder(){
		return ActivityConsumeRankItemHolder.getInstance();
	}
	
	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
