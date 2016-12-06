package com.playerdata.activity.consumeRank;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.consume.ConsumeRankMgr;
import com.bm.rank.consume.RankingConsumeData;
import com.playerdata.Player;
import com.playerdata.activity.consumeRank.cfg.ActivityConsumeRankCfg;
import com.playerdata.activity.consumeRank.cfg.ActivityConsumeRankCfgDAO;
import com.playerdata.activity.consumeRank.data.ActivityConsumeRankItem;
import com.playerdata.activity.consumeRank.data.ActivityConsumeRankItemHolder;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;
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
	
	/**
	 * 邮件发放排行的奖励
	 * 
	 * @param player
	 * @param item
	 */
	@Override
	public void expireActivityHandler(Player player, ActivityConsumeRankItem item) {
		ActivityConsumeRankCfg cfg = ActivityConsumeRankCfgDAO.getInstance().getCfgById(item.getCfgId());
		if (isLevelEnough(player, cfg)) {
			
		}
		item.reset();
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
		return redPointList;
	}
	
	protected UserActivityChecker<ActivityConsumeRankItem> getHolder(){
		return ActivityConsumeRankItemHolder.getInstance();
	}
	
	protected boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
