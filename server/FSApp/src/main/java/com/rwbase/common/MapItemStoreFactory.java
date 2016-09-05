package com.rwbase.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyDiscountType.data.ActivityDailyDiscountTypeItem;
import com.playerdata.activity.dateType.data.ActivityDateTypeItem;
import com.playerdata.activity.exChangeType.data.ActivityExchangeTypeItem;
import com.playerdata.activity.fortuneCatType.data.ActivityFortuneCatTypeItem;
import com.playerdata.activity.limitHeroType.data.ActivityLimitHeroTypeItem;
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
import com.playerdata.hero.core.FSHero;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.teambattle.data.TBTeamItem;
import com.rw.dataaccess.mapitem.MapItemCreator;
import com.rw.dataaccess.mapitem.MapItemValidateParam;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.PFMapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.Tuple;
import com.rw.fsutil.dao.cache.CacheKey;
import com.rw.fsutil.dao.mapitem.MapItemEntity;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.manager.GameManager;
import com.rw.manager.ServerPerformanceConfig;
import com.rw.service.guide.datamodel.GiveItemHistory;
import com.rwbase.dao.angelarray.pojo.db.AngelArrayEnemyInfoData;
import com.rwbase.dao.angelarray.pojo.db.AngelArrayFloorData;
import com.rwbase.dao.angelarray.pojo.db.AngelArrayTeamInfoData;
import com.rwbase.dao.copy.pojo.CopyLevelRecord;
import com.rwbase.dao.copy.pojo.CopyMapRecord;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fetters.pojo.MagicEquipFetterRecord;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityBigItem;
import com.rwbase.dao.group.pojo.db.GroupMemberData;
import com.rwbase.dao.groupCopy.db.CopyItemDropAndApplyRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyRewardDistRecord;
import com.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecord;
import com.rwbase.dao.groupCopy.db.UserGroupCopyMapRecord;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.magic.Magic;
import com.rwbase.dao.skill.pojo.SkillItem;
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
	private static MapItemStoreCache<SkillItem> skillCache;
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

	private static MapItemStoreCache<ActivityDailyRechargeTypeItem> activityDailyRechargeItemCache;
	private static MapItemStoreCache<ActivityLimitHeroTypeItem> activityLimitHeroTypeItemCache;

	private static MapItemStoreCache<ActivityDailyDiscountTypeItem> activityDailyDiscountTypeItemCache;

	private static MapItemStoreCache<ActivityRedEnvelopeTypeItem> activityRedEnvelopeTypeItemCache;

	private static MapItemStoreCache<ActivityFortuneCatTypeItem> activityFortuneCatTypeItemCache;

	private static MapItemStoreCache<FixExpEquipDataItem> fixExpEquipDataItemCache;

	private static MapItemStoreCache<FixNormEquipDataItem> fixNormEquipDataItemCache;

	private static MapItemStoreCache<MagicChapterInfo> magicChapterInfoCache;

	private static MapItemStoreCache<GFDefendArmyItem> groupDefendArmyItemCache;

	private static MapItemStoreCache<GFBiddingItem> groupFightBiddingItemCache;

	private static MapItemStoreCache<GFFinalRewardItem> groupFightRewardItemCache;

	private static MapItemStoreCache<EmbattleInfo> embattleInfoItemCache;

	private static MapItemStoreCache<TBTeamItem> teamBattleItemCache;

	private static MapItemStoreCache<MagicEquipFetterRecord> magicEquipFetterCache;

	private static PFMapItemStoreCache<TablePlatformWhiteList> platformWhiteListCache;

	// 英雄的MapItemStore缓存
	private static MapItemStoreCache<FSHero> heroItemCache;

	private static MapItemStoreCache<FSHero> mainHeroItemCache;

	private static ArrayList<MapItemStoreCache<? extends IMapItem>> list;

	private static boolean init = false;

	private static HashMap<CacheKey, Pair<String, RowMapper<? extends IMapItem>>> storeInfos;

	private static ArrayList<Pair<CacheKey, MapItemStoreCache<? extends IMapItem>>> preloadCaches;
	private static HashMap<CacheKey, MapItemStoreCache<? extends IMapItem>> preloadCachesMapping;
	private static HashMap<Integer, Pair<Class<? extends IMapItem>, MapItemCreator<? extends IMapItem>>> mapItemCreators;
	private static HashMap<Class<? extends IMapItem>, Integer> mapItemIntegration;
	private static List<Tuple<Integer, Class<? extends IMapItem>, MapItemCreator<? extends IMapItem>>> integrationList;
	private static HashMap<Integer, MapItemStoreCache<? extends IMapItem>> integrationMap;

	public static void init(Map<Integer, Pair<Class<? extends IMapItem>, Class<? extends MapItemCreator<? extends IMapItem>>>> map) {
		synchronized (MapItemStoreFactory.class) {
			if (init) {
				init = true;
				return;
			}
		}
		ServerPerformanceConfig config = GameManager.getPerformanceConfig();

		integrationMap = new HashMap<Integer, MapItemStoreCache<? extends IMapItem>>();
		mapItemIntegration = new HashMap<Class<? extends IMapItem>, Integer>();
		integrationList = new ArrayList<Tuple<Integer, Class<? extends IMapItem>, MapItemCreator<? extends IMapItem>>>();
		mapItemCreators = new HashMap<Integer, Pair<Class<? extends IMapItem>, MapItemCreator<? extends IMapItem>>>();
		for (Map.Entry<Integer, Pair<Class<? extends IMapItem>, Class<? extends MapItemCreator<? extends IMapItem>>>> entry : map.entrySet()) {
			Pair<Class<? extends IMapItem>, Class<? extends MapItemCreator<? extends IMapItem>>> pair = entry.getValue();
			MapItemCreator<? extends IMapItem> creator;
			try {
				creator = pair.getT2().newInstance();
			} catch (Exception e) {
				throw new ExceptionInInitializerError(e);
			}
			Integer key = entry.getKey();
			Class<? extends IMapItem> mapItemClass = pair.getT1();
			mapItemIntegration.put(mapItemClass, key);
			mapItemCreators.put(key, Pair.<Class<? extends IMapItem>, MapItemCreator<? extends IMapItem>> Create(mapItemClass, creator));
			integrationList.add(Tuple.<Integer, Class<? extends IMapItem>, MapItemCreator<? extends IMapItem>> Create(key, mapItemClass, creator));
		}
		preloadCachesMapping = new HashMap<CacheKey, MapItemStoreCache<? extends IMapItem>>();
		preloadCaches = new ArrayList<Pair<CacheKey, MapItemStoreCache<? extends IMapItem>>>();
		storeInfos = new HashMap<CacheKey, Pair<String, RowMapper<? extends IMapItem>>>();

		// int playerCapacity = config.getPlayerCapacity();
		int heroCapacity = config.getPlayerCapacity();

		int actualHeroCapacity = config.getHeroCapacity();
		list = new ArrayList<MapItemStoreCache<? extends IMapItem>>();

		itemCache = createForPerload(ItemData.class, "userId", heroCapacity);

		copyLevelRecord = createForPerload(CopyLevelRecord.class, "userId", heroCapacity);

		copyMapRecord = createForPerload(CopyMapRecord.class, "userId", heroCapacity);

		equipCache = createForPerload(EquipItem.class, "ownerId", actualHeroCapacity);

		fashionCache = createForPerload(FashionItem.class, "userId", heroCapacity);

		register(newGuideGiveItemHistoryCache = new MapItemStoreCache<GiveItemHistory>(GiveItemHistory.class, "userId", heroCapacity));

		fresherActivityCache = createForPerload(FresherActivityBigItem.class, "ownerId", heroCapacity);

		inlayItemCache = createForPerload(InlayItem.class, "ownerId", heroCapacity);

		register(magicCache = new MapItemStoreCache<Magic>(Magic.class, "id", heroCapacity));

		skillCache = createForPerload(SkillItem.class, "ownerId", actualHeroCapacity);

		taskItemCache = createForPerload(TaskItem.class, "userId", heroCapacity);

		register(groupMemberCache = new MapItemStoreCache<GroupMemberData>(GroupMemberData.class, "groupId", heroCapacity));

		groupCopyMapRecordCache = createForPerload(GroupCopyMapRecord.class, "groupId", heroCapacity);

		register(groupCopyLevelRecordCache = new MapItemStoreCache<GroupCopyLevelRecord>(GroupCopyLevelRecord.class, "groupId", heroCapacity));
		register(groupCopyRewardRecordCache = new MapItemStoreCache<GroupCopyRewardDistRecord>(GroupCopyRewardDistRecord.class, "groupId", heroCapacity));

		userGroupCopyLevelRecordCache = createForPerload(UserGroupCopyMapRecord.class, "userId", heroCapacity);

		register(serverGroupCopyDamageRecordCache = new MapItemStoreCache<ServerGroupCopyDamageRecord>(ServerGroupCopyDamageRecord.class, "groupId", heroCapacity));
		register(itemDropAndApplyRecordCache = new MapItemStoreCache<CopyItemDropAndApplyRecord>(CopyItemDropAndApplyRecord.class, "groupId", heroCapacity));

		activityCountTypeItemCache = createForPerload(ActivityCountTypeItem.class, "userId", heroCapacity);

		activityTimeCardTypeItemCache = createForPerload(ActivityTimeCardTypeItem.class, "userId", heroCapacity);

		activityRateTypeItemCache = createForPerload(ActivityRateTypeItem.class, "userId", heroCapacity);

		activityRankTypeItemCache = createForPerload(ActivityRankTypeItem.class, "userId", heroCapacity);

		activityExchangeTypeItemCache = createForPerload(ActivityExchangeTypeItem.class, "userId", heroCapacity);

		activityTimeCountTypeItemCache = createForPerload(ActivityTimeCountTypeItem.class, "userId", heroCapacity);

		activityDailyCountTypeItemCache = createForPerload(ActivityDailyTypeItem.class, "userId", heroCapacity);

		activityVitalityItemCache = createForPerload(ActivityVitalityTypeItem.class, "userId", heroCapacity);

		activityDailyDiscountTypeItemCache = createForPerload(ActivityDailyDiscountTypeItem.class, "userId", heroCapacity);

		activityFortuneCatTypeItemCache = createForPerload(ActivityFortuneCatTypeItem.class, "userId", heroCapacity);

		activityLimitHeroTypeItemCache = createForPerload(ActivityLimitHeroTypeItem.class, "userId", heroCapacity);

		activityRedEnvelopeTypeItemCache = createForPerload(ActivityRedEnvelopeTypeItem.class, "userId", heroCapacity);

		fixExpEquipDataItemCache = createForPerload(FixExpEquipDataItem.class, "ownerId", actualHeroCapacity);

		fixNormEquipDataItemCache = createForPerload(FixNormEquipDataItem.class, "ownerId", actualHeroCapacity);

		register(angelArrayTeamInfoData = new MapItemStoreCache<AngelArrayTeamInfoData>(AngelArrayTeamInfoData.class, "teamGroupId", heroCapacity));

		register(angelArrayFloorData = new MapItemStoreCache<AngelArrayFloorData>(AngelArrayFloorData.class, "userId", heroCapacity));

		register(angelArrayEnemyInfoData = new MapItemStoreCache<AngelArrayEnemyInfoData>(AngelArrayEnemyInfoData.class, "userId", heroCapacity));

		magicChapterInfoCache = createForPerload(MagicChapterInfo.class, "userId", heroCapacity);

		register(groupDefendArmyItemCache = new MapItemStoreCache<GFDefendArmyItem>(GFDefendArmyItem.class, "groupID", heroCapacity));

		register(groupFightBiddingItemCache = new MapItemStoreCache<GFBiddingItem>(GFBiddingItem.class, "resourceID", heroCapacity));

		groupFightRewardItemCache = createForPerload(GFFinalRewardItem.class, "userID", heroCapacity);

		register(teamBattleItemCache = new MapItemStoreCache<TBTeamItem>(TBTeamItem.class, "hardID", heroCapacity));

		heroItemCache = createForPerload(FSHero.class, "other", "user_id", heroCapacity);

		mainHeroItemCache = createForPerload(FSHero.class, "main", "id", heroCapacity);

		register(magicEquipFetterCache = new MapItemStoreCache<MagicEquipFetterRecord>(MagicEquipFetterRecord.class, "userID", heroCapacity));

		embattleInfoItemCache = createForPerload(EmbattleInfo.class, "userId", heroCapacity);

		register(platformWhiteListCache = new PFMapItemStoreCache<TablePlatformWhiteList>(TablePlatformWhiteList.class, "accountId", heroCapacity, true));

		activityDailyRechargeItemCache = createForPerload(ActivityDailyRechargeTypeItem.class, "userId", heroCapacity);
	}

	private static <T extends IMapItem> void register(MapItemStoreCache<T> cache) {
		list.add(cache);
	}

	private static <T extends IMapItem> MapItemStoreCache<T> createForPerload(Class<T> clazz, String searchKey, int capacity) {
		return createForPerload(clazz, clazz.getSimpleName(), searchKey, capacity);
	}

	private static <T extends IMapItem> MapItemStoreCache<T> createForPerload(Class<T> clazz, String name, String searchKey, int capacity) {
		Integer type = mapItemIntegration.get(clazz);
		MapItemStoreCache<T> cache = new MapItemStoreCache<T>(clazz, name, searchKey, capacity, type);
		list.add(cache);
		if (type != null) {
			integrationMap.put(type, cache);
		} else {
			// TODO Pair可以只创建一次
			CacheKey cacheKey = new CacheKey(clazz, name);
			RowMapper<? extends IMapItem> rm = cache.getRowMapper();
			storeInfos.put(cacheKey, Pair.<String, RowMapper<? extends IMapItem>> Create(searchKey, rm));
			Pair<CacheKey, MapItemStoreCache<? extends IMapItem>> cacheWrap = Pair.<CacheKey, MapItemStoreCache<? extends IMapItem>> Create(cacheKey, cache);
			preloadCaches.add(cacheWrap);
			preloadCachesMapping.put(cacheKey, cache);
		}
		return cache;
	}

	public static void notifyPlayerCreated(String userId) {
		for (int i = list.size(); --i >= 0;) {
			MapItemStoreCache<? extends IMapItem> cache = list.get(i);
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
	public static MapItemStoreCache<SkillItem> getSkillCache() {
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

	public static MapItemStoreCache<ActivityDailyRechargeTypeItem> getActivityDailyRechargeItemCache() {
		return activityDailyRechargeItemCache;
	}

	public static MapItemStoreCache<ActivityFortuneCatTypeItem> getActivityFortuneCatTypeItemCache() {
		return activityFortuneCatTypeItemCache;
	}

	public static MapItemStoreCache<ActivityLimitHeroTypeItem> getActivityLimitHeroTypeItemCache() {
		return activityLimitHeroTypeItemCache;
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

	/**
	 * 
	 * 获取英雄的数据缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<FSHero> getHeroDataCache() {
		return heroItemCache;
	}

	/**
	 * 
	 * 获取主英雄的数据缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<FSHero> getMainHeroDataCache() {
		return mainHeroItemCache;
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
	 * 
	 * @return
	 */
	public static PFMapItemStoreCache<TablePlatformWhiteList> getPlatformWhiteListCache() {
		return platformWhiteListCache;
	}

	public static List<Pair<CacheKey, String>> getPreloadInfos(String userId) {
		int size = preloadCaches.size();
		ArrayList<Pair<CacheKey, String>> list = new ArrayList<Pair<CacheKey, String>>(size);
		for (int i = 0; i < size; i++) {
			Pair<CacheKey, MapItemStoreCache<? extends IMapItem>> pair = preloadCaches.get(i);
			String tableName = pair.getT2().getTableName(userId);
			list.add(Pair.Create(pair.getT1(), tableName));
		}
		return list;
	}

	public static Map<CacheKey, Pair<String, RowMapper<? extends IMapItem>>> getItemStoreInofs() {
		return Collections.unmodifiableMap(storeInfos);
	}

	public static void preInsertDatas(String userId, List<Pair<CacheKey, List<? extends IMapItem>>> datas) {
		for (int i = datas.size(); --i >= 0;) {
			Pair<CacheKey, List<? extends IMapItem>> pair = datas.get(i);
			MapItemStoreCache<? extends IMapItem> cache = preloadCachesMapping.get(pair.getT1());
			if (cache == null) {
				FSUtilLogger.error("can not find cache:" + pair.getT1());
				continue;
			}
			List items = pair.getT2();
			cache.putIfAbsent(userId, items);
		}
	}

	public static void preloadIntegration(String userId, int level) {
		long start = System.currentTimeMillis();
		ArrayList<Integer> typeList = new ArrayList<Integer>();
		MapItemValidateParam param = new MapItemValidateParam(level, start);
		for (int i = integrationList.size(); --i >= 0;) {
			Tuple<Integer, Class<? extends IMapItem>, MapItemCreator<? extends IMapItem>> tuple = integrationList.get(i);
			MapItemCreator<? extends IMapItem> creator = tuple.getT3();
			if (!creator.isOpen(param)) {
				continue;
			}
			Integer type = tuple.getT1();
			MapItemStoreCache<? extends IMapItem> store = integrationMap.get(type);
			if (store == null) {
				FSUtilLogger.error("can not find cache:" + type);
				continue;
			}
			if (!store.contains(userId)) {
				typeList.add(type);
			}
		}
		if (typeList.isEmpty()) {
			return;
		}
		List<MapItemEntity> datas = DataAccessFactory.getMapItemManager().load(userId, typeList);
		HashMap<Integer, List<MapItemEntity>> map = new HashMap<Integer, List<MapItemEntity>>();
		for (int i = datas.size(); --i >= 0;) {
			MapItemEntity entity = datas.get(i);
			Integer type = entity.getType();
			List<MapItemEntity> list = map.get(type);
			if (list == null) {
				list = new ArrayList<MapItemEntity>();
				map.put(type, list);
			}
			list.add(entity);
		}
		for (int i = typeList.size(); --i >= 0;) {
			Integer type = typeList.get(i);
			MapItemStoreCache<? extends IMapItem> store = integrationMap.get(type);
			if (store == null) {
				FSUtilLogger.error("can not find cache:" + type);
				continue;
			}
			List<MapItemEntity> data = map.get(type);
			if (data == null) {
				data = Collections.emptyList();
			}
			store.putIfAbsentByDBString(userId, datas);
		}
	}
}
