package com.playerdata.activity.chargeRank;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.bm.rank.recharge.ChargeComparable;
import com.bm.rank.recharge.ChargeRankMgr;
import com.bm.rank.recharge.RankingChargeData;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.chargeRank.cfg.ActivityChargeRankSubCfg;
import com.playerdata.activity.chargeRank.cfg.ActivityChargeRankSubCfgDAO;
import com.playerdata.activity.chargeRank.data.ActivityChargeRankItem;
import com.playerdata.activity.chargeRank.data.ActivityChargeRankItemHolder;
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
		ActivityChargeRankItemHolder holder = ActivityChargeRankItemHolder.getInstance();
		List<ActivityChargeRankItem> items = holder.getItemList(player.getUserId());
		if (null == items || items.isEmpty())
			return;
		for (ActivityChargeRankItem item : items) {
			item.setFinishCount(item.getFinishCount() + count);
			holder.updateItem(player, item);
			//该榜目前不支持两个活动同时开启（只有一个榜）
			ChargeRankMgr.addOrUpdateChargeRank(player, item.getFinishCount());
		}
		holder.synAllData(player);
	}
	
	
	@Override
	public void activityStartHandler(ActivityCfgIF cfg){
		Ranking<ChargeComparable, RankingChargeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CHARGE_RANK);
		if(null != ranking){
			ranking.clear();
		}
	}
	
	@Override
	public void activityEndHandler(ActivityCfgIF cfg){
		int dispatchingRank = 0;  //记录正在发放奖励的排名，用做异常的时候查找出错点
		String dispatchingUser = "0";  //记录正在发放奖励的角色id，用做异常的时候查找出错点
		Ranking<ChargeComparable, RankingChargeData> ranking = RankingFactory.getRanking(RankType.ACTIVITY_CHARGE_RANK);
		try {
			EnumerateList<? extends MomentRankingEntry<ChargeComparable, RankingChargeData>> it = ranking.getEntriesEnumeration();
			ActivityChargeRankSubCfgDAO rankCfgDAO = ActivityChargeRankSubCfgDAO.getInstance();
			int rewardCfgCount = rankCfgDAO.getEntryCount();
			for (int i = 1; i <= rewardCfgCount; i++) {
				int startRank = 1;
				if (i != 1){
					startRank = rankCfgDAO.getCfgById(String.valueOf(i - 1)).getRankEnd() + 1;
				}
				ActivityChargeRankSubCfg rewardCfg = rankCfgDAO.getCfgById(String.valueOf(i));
				int endRank = rewardCfg.getRankEnd();
				for (int j = startRank; j <= endRank; j++) {
					dispatchingRank = j;
					if (it.hasMoreElements()) {
						MomentRankingEntry<ChargeComparable, RankingChargeData> entry = it.nextElement();
						dispatchingUser = entry.getExtendedAttribute().getUserId();
						EmailUtils.sendEmail(dispatchingUser, String.valueOf(rewardCfg.getEmailId()), rewardCfg.getReward());
					}else{
						return;
					}
				}
			}
		} catch (Exception ex) {
			GameLog.error(LogModule.ComActChargeRank, "ActivityChargeRankMgr", String.format("expireActivityHandler, 给角色[%s]发放充值排行奖励[%s]的时候出现异常", dispatchingUser, dispatchingRank), ex);
		} finally {
			ranking.clear();
		}
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
	
	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
