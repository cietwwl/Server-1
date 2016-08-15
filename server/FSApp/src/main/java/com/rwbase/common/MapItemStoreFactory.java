package com.rwbase.common;

import java.util.ArrayList;
import java.util.List;

import com.groupCopy.rwbase.dao.groupCopy.db.CopyItemDropAndApplyRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyRewardDistRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.UserGroupCopyMapRecord;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dateType.data.ActivityDateTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.redEnvelopeType.data.ActivityRedEnvelopeTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.embattle.EmbattleInfo;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.playerdata.groupFightOnline.data.GFBiddingItem;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFFinalRewardItem;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.teambattle.data.TBTeamItem;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.PFMapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.manager.GameManager;
import com.rw.manager.ServerPerformanceConfig;
import com.rw.service.guide.datamodel.GiveItemHistory;
import com.rwbase.dao.anglearray.pojo.db.AngelArrayEnemyInfoData;
import com.rwbase.dao.anglearray.pojo.db.AngelArrayFloorData;
import com.rwbase.dao.anglearray.pojo.db.AngelArrayTeamInfoData;
import com.rwbase.dao.copy.pojo.CopyLevelRecord;
import com.rwbase.dao.copy.pojo.CopyMapRecord;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fetters.pojo.MagicEquipFetterRecord;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityBigItem;
import com.rwbase.dao.group.pojo.db.GroupMemberData;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.magic.Magic;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.task.pojo.TaskItem;
import com.rwbase.dao.user.platformwhitelist.TablePlatformWhiteList;

public class MapItemStoreFactory {

	// ItemBag
	private static MapItemStoreCache<ItemData> itemCache;
	// CopyLevelRecord
	private static MapItemStoreCache<CopyLevelRecord> copyLevelRecord;
	// CopyMapRecord
	private static MapItemStoreCache<CopyMapRecord> copyMapRecord;
	// EquipItem
	private static MapItemStoreCache<EquipItem> equipCache;
	// FashionItem
	private static MapItemStoreCache<FashionItem> fashionCache;
	// new guide
	private static MapItemStoreCache<GiveItemHistory> newGuideGiveItemHistoryCache;
	// FresherActivityItem
	private static MapItemStoreCache<FresherActivityBigItem> fresherActivityCache;
	// InlayItem
	private static MapItemStoreCache<InlayItem> inlayItemCache;
	// Magic
	private static MapItemStoreCache<Magic> magicCache;
	// Skill
	private static MapItemStoreCache<Skill> skillCache;
	// TaskItem
	private static MapItemStoreCache<TaskItem> taskItemCache;
	// GroupMemberData
	private static MapItemStoreCache<GroupMemberData> groupMemberCache;
	// AngelArrayTeamInfo
	private static MapItemStoreCache<AngelArrayTeamInfoData> angelArrayTeamInfoData;
	// AngelArrayFloorData
	private static MapItemStoreCache<AngelArrayFloorData> angelArrayFloorData;
	// AngelArrayEnemyInfoData
	private static MapItemStoreCache<AngelArrayEnemyInfoData> angelArrayEnemyInfoData;

	private static MapItemStoreCache<GroupCopyMapRecord> groupCopyMapRecordCache;
	private static MapItemStoreCache<GroupCopyLevelRecord> groupCopyLevelRecordCache;
	private static MapItemStoreCache<GroupCopyRewardDistRecord> groupCopyRewardRecordCache;
	private static MapItemStoreCache<UserGroupCopyMapRecord> userGroupCopyLevelRecordCache;
	private static MapItemStoreCache<ServerGroupCopyDamageRecord> serverGroupCopyDamageRecordCache;
	private static MapItemStoreCache<CopyItemDropAndApplyRecord> itemDropAndApplyRecordCache;

	private static MapItemStoreCache<ActivityCountTypeItem> activityCountTypeItemCache;

	private static MapItemStoreCache<ActivityDailyTypeItem> activityDailyCountTypeItemCache;

	private static MapItemStoreCache<ActivityTimeCardTypeItem> activityTimeCardTypeItemCache;

	private static MapItemStoreCache<ActivityRateTypeItem> activityRateTypeItemCache;

	private static MapItemStoreCache<ActivityDateTypeItem> activityDateTypeItemCache;

	private static MapItemStoreCache<ActivityRankTypeItem> activityRankTypeItemCache;

	private static MapItemStoreCache<ActivityTimeCountTypeItem> activityTimeCountTypeItemCache;

	private static MapItemStoreCache<ActivityVitalityTypeItem> activityVitalityItemCache;

	private static MapItemStoreCache<ActivityExchangeTypeItem> activityExchangeTypeItemCache;

	private static MapItemStoreCache<ActivityDailyDiscountTypeItem> activityDailyDiscountTypeItemCache;
	
	private static MapItemStoreCache<FixExpEquipDataItem> fixExpEquipDataItemCache;
	
	private static MapItemStoreCache<ActivityRedEnvelopeTypeItem> activityRedEnvelopeTypeItemCache;
	
	private static MapItemStoreCache<FixNormEquipDataItem> fixNormEquipDataItemCache;

	private static MapItemStoreCache<MagicChapterInfo> magicChapterInfoCache;

	private static MapItemStoreCache<GFDefendArmyItem> groupDefendArmyItemCache;

	private static MapItemStoreCache<GFBiddingItem> groupFightBiddingItemCache;

	private static MapItemStoreCache<GFFinalRewardItem> groupFightRewardItemCache;

	private static MapItemStoreCache<EmbattleInfo> embattleInfoItemCache;

	private static MapItemStoreCache<TBTeamItem> teamBattleItemCache;

	
	private static MapItemStoreCache<MagicEquipFetterRecord> magicEquipFetterCache;
	
	private static PFMapItemStoreCache<TablePlatformWhiteList> platformWhiteListCache;
	
	private static List<MapItemStoreCache> list;

	private static boolean init = false;

	public static void init() {
		synchronized (MapItemStoreFactory.class) {
			if (init) {
				init = true;
				return;
			}
		}
		ServerPerformanceConfig config = GameManager.getPerformanceConfig();

		// int playerCapacity = config.getPlayerCapacity();
		int heroCapacity = config.getPlayerCapacity();
		int actualHeroCapacity = config.getHeroCapacity();
		list = new ArrayList<MapItemStoreCache>();
		register(itemCache = new MapItemStoreCache<ItemData>(ItemData.class, "userId", heroCapacity));

		register(copyLevelRecord = new MapItemStoreCache<CopyLevelRecord>(CopyLevelRecord.class, "userId", heroCapacity));

		register(copyMapRecord = new MapItemStoreCache<CopyMapRecord>(CopyMapRecord.class, "userId", heroCapacity));

		register(equipCache = new MapItemStoreCache<EquipItem>(EquipItem.class, "ownerId", actualHeroCapacity));

		register(fashionCache = new MapItemStoreCache<FashionItem>(FashionItem.class, "userId", heroCapacity));

		register(newGuideGiveItemHistoryCache = new MapItemStoreCache<GiveItemHistory>(GiveItemHistory.class, "userId", heroCapacity));

		register(fresherActivityCache = new MapItemStoreCache<FresherActivityBigItem>(FresherActivityBigItem.class, "ownerId", heroCapacity));

		register(inlayItemCache = new MapItemStoreCache<InlayItem>(InlayItem.class, "ownerId", heroCapacity));

		register(magicCache = new MapItemStoreCache<Magic>(Magic.class, "id", heroCapacity));

		register(skillCache = new MapItemStoreCache<Skill>(Skill.class, "ownerId", actualHeroCapacity));

		register(taskItemCache = new MapItemStoreCache<TaskItem>(TaskItem.class, "userId", heroCapacity));

		register(groupMemberCache = new MapItemStoreCache<GroupMemberData>(GroupMemberData.class, "groupId", heroCapacity));

		register(groupCopyMapRecordCache = new MapItemStoreCache<GroupCopyMapRecord>(GroupCopyMapRecord.class, "groupId", heroCapacity));

		register(groupCopyLevelRecordCache = new MapItemStoreCache<GroupCopyLevelRecord>(GroupCopyLevelRecord.class, "groupId", heroCapacity));
		register(groupCopyRewardRecordCache = new MapItemStoreCache<GroupCopyRewardDistRecord>(GroupCopyRewardDistRecord.class, "groupId", heroCapacity));

		register(userGroupCopyLevelRecordCache = new MapItemStoreCache<UserGroupCopyMapRecord>(UserGroupCopyMapRecord.class, "userId", heroCapacity));
		register(serverGroupCopyDamageRecordCache = new MapItemStoreCache<ServerGroupCopyDamageRecord>(ServerGroupCopyDamageRecord.class, "groupId", heroCapacity));
		register(itemDropAndApplyRecordCache = new MapItemStoreCache<CopyItemDropAndApplyRecord>(CopyItemDropAndApplyRecord.class, "groupId", heroCapacity));

		register(activityCountTypeItemCache = new MapItemStoreCache<ActivityCountTypeItem>(ActivityCountTypeItem.class, "userId", heroCapacity));

		register(activityTimeCardTypeItemCache = new MapItemStoreCache<ActivityTimeCardTypeItem>(ActivityTimeCardTypeItem.class, "userId", heroCapacity));

		register(activityRateTypeItemCache = new MapItemStoreCache<ActivityRateTypeItem>(ActivityRateTypeItem.class, "userId", heroCapacity));
		//
		// register(activityDateTypeItemCache = new MapItemStoreCache<ActivityDateTypeItem>(ActivityDateTypeItem.class, "userId", heroCapacity));
		//

		register(activityRankTypeItemCache = new MapItemStoreCache<ActivityRankTypeItem>(ActivityRankTypeItem.class, "userId", heroCapacity));

		register(activityExchangeTypeItemCache = new MapItemStoreCache<ActivityExchangeTypeItem>(ActivityExchangeTypeItem.class, "userId", heroCapacity));
		register(activityTimeCountTypeItemCache = new MapItemStoreCache<ActivityTimeCountTypeItem>(ActivityTimeCountTypeItem.class, "userId", heroCapacity));

		register(activityDailyCountTypeItemCache = new MapItemStoreCache<ActivityDailyTypeItem>(ActivityDailyTypeItem.class, "userId", heroCapacity));

		register(activityVitalityItemCache = new MapItemStoreCache<ActivityVitalityTypeItem>(ActivityVitalityTypeItem.class, "userId", heroCapacity));

		register(activityDailyDiscountTypeItemCache = new MapItemStoreCache<ActivityDailyDiscountTypeItem>(ActivityDailyDiscountTypeItem.class, "userId", heroCapacity));

		register(activityRedEnvelopeTypeItemCache = new MapItemStoreCache<ActivityRedEnvelopeTypeItem>(ActivityRedEnvelopeTypeItem.class, "userId", heroCapacity));

		register(fixExpEquipDataItemCache = new MapItemStoreCache<FixExpEquipDataItem>(FixExpEquipDataItem.class, "ownerId", actualHeroCapacity));

		register(fixNormEquipDataItemCache = new MapItemStoreCache<FixNormEquipDataItem>(FixNormEquipDataItem.class, "ownerId", actualHeroCapacity));

		// register(groupSecretDefLogCache = new MapItemStoreCache<GroupSecretDefLog>(GroupSecretDefLog.class, "secretId", heroCapacity));

		register(angelArrayTeamInfoData = new MapItemStoreCache<AngelArrayTeamInfoData>(AngelArrayTeamInfoData.class, "teamGroupId", heroCapacity));

		register(angelArrayFloorData = new MapItemStoreCache<AngelArrayFloorData>(AngelArrayFloorData.class, "userId", heroCapacity));

		register(angelArrayEnemyInfoData = new MapItemStoreCache<AngelArrayEnemyInfoData>(AngelArrayEnemyInfoData.class, "userId", heroCapacity));

		register(magicChapterInfoCache = new MapItemStoreCache<MagicChapterInfo>(MagicChapterInfo.class, "userId", heroCapacity));

		register(groupDefendArmyItemCache = new MapItemStoreCache<GFDefendArmyItem>(GFDefendArmyItem.class, "groupID", heroCapacity));

		register(groupFightBiddingItemCache = new MapItemStoreCache<GFBiddingItem>(GFBiddingItem.class, "resourceID", heroCapacity));

		register(groupFightRewardItemCache = new MapItemStoreCache<GFFinalRewardItem>(GFFinalRewardItem.class, "rewardOwner", heroCapacity));
		
		register(teamBattleItemCache = new MapItemStoreCache<TBTeamItem>(TBTeamItem.class, "hardID", heroCapacity));
		
		register(magicEquipFetterCache = new MapItemStoreCache<MagicEquipFetterRecord>(MagicEquipFetterRecord.class, "userID", heroCapacity));

		register(embattleInfoItemCache = new MapItemStoreCache<EmbattleInfo>(EmbattleInfo.class, "userId", heroCapacity));
		
		register(platformWhiteListCache = new PFMapItemStoreCache<TablePlatformWhiteList>(TablePlatformWhiteList.class, "accountId", heroCapacity, true));
	}

	private static <T extends IMapItem> void register(MapItemStoreCache<T> cache) {
		list.add(cache);
	}

	public static void notifyPlayerCreated(String userId) {
		for (int i = list.size(); --i >= 0;) {
			MapItemStoreCache cache = list.get(i);
			cache.notifyPlayerCreate(userId);
		}
	}

	/**
	 * 获取ItemBag缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<ItemData> getItemCache() {
		return itemCache;
	}

	/**
	 * 获取CopyLevelRecord缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<CopyLevelRecord> getCopyLevelRecordCache() {
		return copyLevelRecord;
	}

	/**
	 * 获取CopyMapRecord缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<CopyMapRecord> getCopyMapRecordCache() {
		return copyMapRecord;
	}

	/**
	 * 获取装备缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<EquipItem> getEquipCache() {
		return equipCache;
	}

	/**
	 * 获取时装缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<FashionItem> getFashionCache() {
		return fashionCache;
	}

	/**
	 * 获取开服活动缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<FresherActivityBigItem> getFresherActivityCache() {
		return fresherActivityCache;
	}

	/**
	 * 获取镶嵌装备缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<InlayItem> getInlayItemCache() {
		return inlayItemCache;
	}

	/**
	 * 获取法宝缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<Magic> getMagicCache() {
		return magicCache;
	}

	/**
	 * 获取技能缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<Skill> getSkillCache() {
		return skillCache;
	}

	/**
	 * 获取任务缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<TaskItem> getTaskItemCache() {
		return taskItemCache;
	}

	/**
	 * 获取帮派成员缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<GroupMemberData> getGroupMemberCache() {
		return groupMemberCache;
	}

	public static MapItemStoreCache<GroupCopyMapRecord> getGroupCopyMapRecordCache() {
		return groupCopyMapRecordCache;
	}

	public static MapItemStoreCache<GroupCopyLevelRecord> getGroupCopyLevelRecordCache() {
		return groupCopyLevelRecordCache;
	}

	public static MapItemStoreCache<UserGroupCopyMapRecord> getUserGroupCopyLevelRecordCache() {
		return userGroupCopyLevelRecordCache;
	}

	/**
	 * 获取帮派奖励分配记录
	 * 
	 * @return
	 */
	public static MapItemStoreCache<GroupCopyRewardDistRecord> getGroupCopyRewardRecordCache() {
		return groupCopyRewardRecordCache;
	}

	/**
	 * 获取帮派副本关卡全服单次伤害缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<ServerGroupCopyDamageRecord> getServerGroupCopyDamageRecordCache() {
		return serverGroupCopyDamageRecordCache;
	}

	public static MapItemStoreCache<CopyItemDropAndApplyRecord> getItemDropAndApplyRecordCache() {
		return itemDropAndApplyRecordCache;
	}

	public static MapItemStoreCache<GiveItemHistory> getNewGuideGiveItemHistoryCache() {
		return newGuideGiveItemHistoryCache;
	}

	public static MapItemStoreCache<ActivityCountTypeItem> getActivityCountTypeItemCache() {
		return activityCountTypeItemCache;
	}

	public static MapItemStoreCache<ActivityDailyTypeItem> getActivityDailyCountTypeItemCache() {
		return activityDailyCountTypeItemCache;
	}

	public static MapItemStoreCache<ActivityDailyDiscountTypeItem> getActivityDailyDiscountTypeItemCache() {
		return activityDailyDiscountTypeItemCache;
	}

	public static MapItemStoreCache<ActivityTimeCardTypeItem> getActivityTimeCardTypeItemCache() {
		return activityTimeCardTypeItemCache;
	}

	public static MapItemStoreCache<ActivityRateTypeItem> getActivityRateTypeItemCache() {
		return activityRateTypeItemCache;
	}

	public static MapItemStoreCache<ActivityDateTypeItem> getActivityDateTypeItemCache() {
		return activityDateTypeItemCache;
	}

	public static MapItemStoreCache<ActivityRankTypeItem> getActivityRankTypeItemCache() {
		return activityRankTypeItemCache;
	}

	public static MapItemStoreCache<ActivityTimeCountTypeItem> getActivityTimeCountTypeItemCache() {
		return activityTimeCountTypeItemCache;
	}

	public static MapItemStoreCache<ActivityExchangeTypeItem> getActivityExchangeTypeItemCache() {
		return activityExchangeTypeItemCache;
	}

	public static MapItemStoreCache<ActivityVitalityTypeItem> getActivityVitalityItemCache() {
		return activityVitalityItemCache;
	}
	
	
	

	public static MapItemStoreCache<ActivityRedEnvelopeTypeItem> getActivityRedEnvelopeTypeItemCache() {
		return activityRedEnvelopeTypeItemCache;
	}


	public static MapItemStoreCache<FixExpEquipDataItem> getFixExpEquipDataItemCache() {
		return fixExpEquipDataItemCache;
	}

	public static MapItemStoreCache<FixNormEquipDataItem> getFixNormEquipDataItemCache() {
		return fixNormEquipDataItemCache;
	}

	/**
	 * 获取万仙阵阵容信息缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<AngelArrayTeamInfoData> getAngelArrayTeamInfoData() {
		return angelArrayTeamInfoData;
	}

	/**
	 * 获取万仙阵层数信息缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<AngelArrayFloorData> getAngelArrayFloorData() {
		return angelArrayFloorData;
	}

	/**
	 * 获取万仙阵层中的敌方阵容血量信息变化
	 * 
	 * @return
	 */
	public static MapItemStoreCache<AngelArrayEnemyInfoData> getAngelArrayEnemyInfoData() {
		return angelArrayEnemyInfoData;
	}

	/**
	 * 获取法宝秘境的章节缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<MagicChapterInfo> getMagicChapterInfoCache() {
		return magicChapterInfoCache;
	}

	/**
	 * 获取在线帮派战斗的防守队伍缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<GFDefendArmyItem> getGFDefendArmyCache() {
		return groupDefendArmyItemCache;
	}

	/**
	 * 获取在线帮战帮派竞标缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<GFBiddingItem> getGFBiddingItemCache() {
		return groupFightBiddingItemCache;
	}

	/**
	 * 获取在线帮战个人奖励的缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<GFFinalRewardItem> getGFFinalRewardItemCache() {
		return groupFightRewardItemCache;
	}

	/**
	 * 获取在线帮战个人奖励的缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<TBTeamItem> getTBTeamItemCache() {
		return teamBattleItemCache;
	}

	public static MapItemStoreCache<MagicEquipFetterRecord> getMagicEquipFetterCache() {
		return magicEquipFetterCache;
	}
	
	
	

	/**
	 * 获取站位的Cache
	 * 
	 * @return
	 */
	public static MapItemStoreCache<EmbattleInfo> getEmbattleInfoCache() {
		return embattleInfoItemCache;
	}

	/**
	 * 获取白名单的Cache
	 * @return
	 */
	public static PFMapItemStoreCache<TablePlatformWhiteList> getPlatformWhiteListCache() {
		return platformWhiteListCache;
	}
}
