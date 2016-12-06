package com.playerdata.activity.chargeRank;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.recharge.ChargeRankMgr;
import com.bm.rank.recharge.RankingChargeData;
import com.playerdata.Player;
import com.playerdata.activity.chargeRank.cfg.ActivityChargeRankCfg;
import com.playerdata.activity.chargeRank.cfg.ActivityChargeRankCfgDAO;
import com.playerdata.activity.chargeRank.data.ActivityChargeRankItem;
import com.playerdata.activity.chargeRank.data.ActivityChargeRankItemHolder;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItemHolder;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;
import com.rwproto.ActivityChargeRankProto.ActivityCommonRspMsg;
import com.rwproto.ActivityChargeRankProto.RankItem;

public class ActivityChargeRankMgr extends AbstractActivityMgr<ActivityChargeRankItem> {
	
	private static final int ACTIVITY_INDEX_BEGIN = 170000;
	private static final int ACTIVITY_INDEX_END = 180000;

	private static ActivityChargeRankMgr instance = new ActivityChargeRankMgr();
	
	public static ActivityChargeRankMgr getInstance() {
		return instance;
	}

	/**
	 * 添加完成的进度
	 * 
	 * @param player
	 * @param count
	 */
	public void addFinishCount(Player player, int count) {
		ActivityDailyRechargeTypeItemHolder holder = ActivityDailyRechargeTypeItemHolder.getInstance();
		List<ActivityDailyRechargeTypeItem> items = holder.getItemList(player.getUserId());
		if (null == items || items.isEmpty())
			return;
		for (ActivityDailyRechargeTypeItem item : items) {
			item.setFinishCount(item.getFinishCount() + count);
			holder.updateItem(player, item);
			//该榜目前不支持两个活动同时开启（只有一个榜）
			ChargeRankMgr.addOrUpdateChargeRank(player, item.getFinishCount());
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
	public void expireActivityHandler(Player player, ActivityChargeRankItem item) {
		ActivityChargeRankCfg cfg = ActivityChargeRankCfgDAO.getInstance().getCfgById(item.getCfgId());
		if (isLevelEnough(player, cfg)) {
			
		}
		item.reset();
	}
	
	/**
	 * 获取充值排行榜
	 * @param player
	 * @param fromRank
	 * @param toRank
	 */
	public void getChargeRank(Player player, int fromRank, int toRank, ActivityCommonRspMsg.Builder response){
		List<RankingChargeData> chargeRank = ChargeRankMgr.getRankIndex(fromRank, toRank);
		for(RankingChargeData data : chargeRank){
			RankItem.Builder rankItemBuilder = RankItem.newBuilder();
			rankItemBuilder.setUserId(data.getUserId());
			rankItemBuilder.setUserName(data.getUserName());
			rankItemBuilder.setCount(data.getCharge());
			response.addItems(rankItemBuilder.build());
		}
		response.setSelfRank(ChargeRankMgr.getRankIndex(player.getUserId()));
	}
	
	/**
	 * 获取消费排行榜
	 * @param player
	 * @param fromRank
	 * @param toRank
	 */
	public void getConsumeRank(Player player, int fromRank, int toRank, ActivityCommonRspMsg.Builder response){
		
	}

	@Override
	protected List<String> checkRedPoint(Player player, ActivityChargeRankItem item) {
		List<String> redPointList = new ArrayList<String>();
		if (!item.isHasViewed()) {
			redPointList.add(String.valueOf(item.getCfgId()));
		}
		return redPointList;
	}
	
	protected UserActivityChecker<ActivityChargeRankItem> getHolder(){
		return ActivityChargeRankItemHolder.getInstance();
	}
	
	protected boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
