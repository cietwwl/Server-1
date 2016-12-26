package com.playerdata.activity.rankType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.ListRankingType;
import com.bm.rank.RankType;
import com.bm.rank.arena.ArenaExtAttribute;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeSubCfg;
import com.playerdata.activity.rankType.cfg.ActivityRankTypeSubCfgDAO;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItemHolder;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerPredecessor;

public class ActivityRankTypeMgr extends AbstractActivityMgr<ActivityRankTypeItem> {

	private static final int ACTIVITY_INDEX_BEGIN = 70000;
	private static final int ACTIVITY_INDEX_END = 80000;
	
	private static ActivityRankTypeMgr instance = new ActivityRankTypeMgr();

	public static ActivityRankTypeMgr getInstance() {
		return instance;
	}
	
	/**
	 * 根据等级限制和排行榜类型，获取排行榜
	 * @param levelLimit
	 * @param ranktype
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> getRankListByRankTypeAndsubCfgNum(int levelLimit, Integer ranktype) {
		List<String> rankList = new ArrayList<String>();
		RankType rankEnum = RankType.getRankType(ranktype, 1);
		ListRankingType sType = ListRankingType.getListRankingType(rankEnum);
		ListRanking<String, ArenaExtAttribute> sr = RankingFactory.getSRanking(sType);
		if(null == sr){
			Ranking<?, RankingLevelData> ranking = RankingFactory.getRanking(rankEnum);
			if(null == ranking){
				return rankList;
			}
			EnumerateList<? extends MomentRankingEntry<?, RankingLevelData>> enumList = ranking.getEntriesEnumeration();
			for (; enumList.hasMoreElements();) {
				MomentRankingEntry<?, RankingLevelData> entry = enumList.nextElement();
				RankingLevelData data = entry.getExtendedAttribute();
				if(data.getLevel() < levelLimit){
					rankList.add(null);
				}else{
					rankList.add(data.getUserId());
				}
			}
		}else{
			Iterator<? extends ListRankingEntry<String, ArenaExtAttribute>> it = sr.getEntrysCopy().iterator();
			for (; it.hasNext();) {
				ListRankingEntry<String, ArenaExtAttribute> entry = it.next();
				ArenaExtAttribute data = entry.getExtension();
				if(data.getLevel() < levelLimit){
					rankList.add(null);
				}else{
					rankList.add(entry.getKey());
				}
			}
		}
		return rankList;
	}
	
	/**
	 * 给个人派发排行奖励
	 * @param userId
	 * @param activityRankTypeItemHolder
	 * @param subCfg
	 * @param rank
	 */
	private void sendGifgSingel(String userId, ActivityRankTypeItemHolder activityRankTypeItemHolder, ActivityRankTypeSubCfg subCfg, int rank) {
		RoleExtPropertyStore<ActivityRankTypeItem> itemStore = activityRankTypeItemHolder.getItemStore(userId);
		if (itemStore == null) {
			GameLog.error("ActivityRankTypeMgr, sendGifgSingel", userId, String.format("玩家的%s的%s活动数据为空", userId, subCfg.getParentCfgId()));
			return;
		}
		ActivityRankTypeItem targetItem = null;
		targetItem = itemStore.get(Integer.parseInt(subCfg.getParentCfgId()));
		if (targetItem == null) {
			// 有排行无登录时生成的排行榜活动奖励数据，说明是机器人或活动期间没登陆过
			GameLog.error("ActivityRankTypeMgr, sendGifgSingel", userId, String.format("玩家%s%s活动期间没有登录过，数据为空，不发放奖励", userId, subCfg.getParentCfgId()));
			return;
		}
		if(!targetItem.isTaken()){
			GameLog.info("ActivityRankTypeMgr, sendGift", userId, String.format("发放玩家的%s奖励，排名[%s]，邮件id[%s]，物品[%s]", subCfg.getParentCfgId(), rank, subCfg.getEmailId(), subCfg.getReward()));
			EmailUtils.sendEmail(userId, subCfg.getEmailId(), subCfg.getReward());
			targetItem.setTaken(true);
			itemStore.update(targetItem.getId());
		}else{
			GameLog.info("ActivityRankTypeMgr, sendGifgSingel", userId, String.format("玩家%s%s活动的奖励已经发放过，不能重复发放", userId, subCfg.getParentCfgId()));
		}
	}
	
	@Override
	public void activityEndHandler(ActivityCfgIF cfg){
		final ActivityRankTypeItemHolder activityRankTypeItemHolder = ActivityRankTypeItemHolder.getInstance();
		List<ActivityRankTypeSubCfg> subCfgList =  ActivityRankTypeSubCfgDAO.getInstance().getByParentCfgId(String.valueOf(cfg.getCfgId()));
		ActivityRankTypeEnum typeEnum = ActivityRankTypeEnum.getById(cfg.getId());
		if (null == typeEnum) {
			// 代码没定义配置表里要的活动
			GameLog.error("ActivityRankTypeMgr, sendGift", String.valueOf(cfg.getId()), String.format("要发放奖励的类型[%s]不存在", cfg.getId()));
			return;
		}
		int ranktype = typeEnum.getRankType();
		// 该配表对应的所有排行榜
		List<String> rankList = getRankListByRankTypeAndsubCfgNum(cfg.getLevelLimit(), ranktype);
		for(final ActivityRankTypeSubCfg subCfg : subCfgList){
			//根据配置表的名次发放奖励
			int size = rankList.size();
			for(int i = subCfg.getRankRanges()[0]; i <= subCfg.getRankRanges()[1] && i > 0 && i <= size; i++){
				final String userId = rankList.get(i-1);
				if(StringUtils.isBlank(userId)){
					continue;
				}
				User user = UserDataDao.getInstance().getByUserId(userId);
				if (user == null) {
					GameLog.error("ActivityRankTypeMgr, sendGift", userId, "找不到玩家信息id:" + userId);
				} else {
					if (user.isRobot() || user.getLastLoginTime() < cfg.getStartTime()) {
						// 机器人，或者最后次登陆时间不在活动时间内；
						continue;
					}
					final int rank = i;
					GameLog.info("ActivityRankTypeMgr, sendGift", userId, String.format("发放玩家的%s奖励，排名[%s]", typeEnum, rank));
					GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerPredecessor() {
						@Override
						public void run(String e) {
							sendGifgSingel(userId, activityRankTypeItemHolder, subCfg, rank);
						}
					});
				}
			}
		}
	}

	@Override
	protected List<String> checkRedPoint(Player player, ActivityRankTypeItem item) {
		List<String> redPointList = new ArrayList<String>();
		if (!item.isHasViewed()) {
			redPointList.add(String.valueOf(item.getCfgId()));
		}
		return redPointList;
	}

	@Override
	protected UserActivityChecker<ActivityRankTypeItem> getHolder() {
		return ActivityRankTypeItemHolder.getInstance();
	}

	@Override
	public boolean isThisActivityIndex(int index) {
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
