package com.rwbase.common;

import java.util.ArrayList;
import com.playerdata.activity.dateType.data.ActivityDateTypeItem;
import com.playerdata.embattle.EmbattleInfo;
import com.playerdata.groupFightOnline.data.GFBiddingItem;
import com.playerdata.groupFightOnline.data.GFDefendArmyItem;
import com.playerdata.groupFightOnline.data.GFFinalRewardItem;
import com.playerdata.groupcompetition.quiz.GCompUserQuizItem;
import com.playerdata.hero.core.FSHero;
import com.playerdata.mgcsecret.data.MagicChapterInfo;
import com.playerdata.teambattle.data.TBTeamItem;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.PFMapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.manager.GameManager;
import com.rw.manager.ServerPerformanceConfig;
import com.rw.service.guide.datamodel.GiveItemHistory;
import com.rwbase.dao.angelarray.pojo.db.AngelArrayEnemyInfoData;
import com.rwbase.dao.angelarray.pojo.db.AngelArrayFloorData;
import com.rwbase.dao.angelarray.pojo.db.AngelArrayTeamInfoData;
import com.rwbase.dao.copy.pojo.CopyLevelRecord;
import com.rwbase.dao.copy.pojo.CopyMapRecord;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fetters.pojo.MagicEquipFetterRecord;
import com.rwbase.dao.group.pojo.db.GroupMemberData;
import com.rwbase.dao.groupCopy.db.CopyItemDropAndApplyRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyRewardDistRecord;
import com.rwbase.dao.groupCopy.db.ServerGroupCopyDamageRecord;
import com.rwbase.dao.groupCopy.db.UserGroupCopyMapRecord;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.magic.Magic;
import com.rwbase.dao.task.pojo.TaskItem;
import com.rwbase.dao.user.platformwhitelist.TablePlatformWhiteList;

public class MapItemStoreFactory {

	// ItemBag
	private static MapItemStoreCache<ItemData> itemCache;
	// CopyLevelRecord
	private static MapItemStoreCache<CopyLevelRecord> copyLevelRecord;
	// CopyMapRecord
	private static MapItemStoreCache<CopyMapRecord> copyMapRecord;
	// FashionItem
	private static MapItemStoreCache<FashionItem> fashionCache;
	// new guide
	private static MapItemStoreCache<GiveItemHistory> newGuideGiveItemHistoryCache;
	// Magic
	private static MapItemStoreCache<Magic> magicCache;
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

	private static MapItemStoreCache<ActivityDateTypeItem> activityDateTypeItemCache;

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

	private static MapItemStoreCache<GCompUserQuizItem> groupCompQuizItemCache;

	private static ArrayList<MapItemStoreCache<? extends IMapItem>> list;

	private static ArrayList<MapItemStoreCache<? extends IMapItem>> notifyCreateList;

	private static boolean init = false;


	public static final String MAIN_ROLE_NAME = "main";

	public static void init() {
		synchronized (MapItemStoreFactory.class) {
			if (init) {
				init = true;
				return;
			}
		}
		ServerPerformanceConfig config = GameManager.getPerformanceConfig();

		int heroCapacity = config.getPlayerCapacity();

		int actualHeroCapacity = config.getHeroCapacity();
		list = new ArrayList<MapItemStoreCache<? extends IMapItem>>();
		notifyCreateList = new ArrayList<MapItemStoreCache<? extends IMapItem>>();

		itemCache = createForPerload(ItemData.class, "userId", heroCapacity);

		copyLevelRecord = createForPerload(CopyLevelRecord.class, "userId", heroCapacity);

		copyMapRecord = createForPerload(CopyMapRecord.class, "userId", heroCapacity);

		fashionCache = createForPerload(FashionItem.class, "userId", heroCapacity);

		newGuideGiveItemHistoryCache = createForPerload(GiveItemHistory.class, "userId", heroCapacity);

		magicCache = createForPerload(Magic.class, "id", heroCapacity);

		taskItemCache = createForPerload(TaskItem.class, "userId", heroCapacity);

		groupMemberCache = createForPerload(GroupMemberData.class, "groupId", heroCapacity);

		groupCopyMapRecordCache = createForPerload(GroupCopyMapRecord.class, "groupId", heroCapacity);

		groupCopyLevelRecordCache = createForPerload(GroupCopyLevelRecord.class, "groupId", heroCapacity);
		groupCopyRewardRecordCache = createForPerload(GroupCopyRewardDistRecord.class, "groupId", heroCapacity);

		userGroupCopyLevelRecordCache = createForPerload(UserGroupCopyMapRecord.class, "userId", heroCapacity);

		serverGroupCopyDamageRecordCache = createForPerload(ServerGroupCopyDamageRecord.class, "groupId", heroCapacity);
		itemDropAndApplyRecordCache = createForPerload(CopyItemDropAndApplyRecord.class, "groupId", heroCapacity);

		angelArrayTeamInfoData = createForPerload(AngelArrayTeamInfoData.class, "teamGroupId", heroCapacity);

		angelArrayFloorData = createForPerload(AngelArrayFloorData.class, "userId", heroCapacity);

		angelArrayEnemyInfoData = createForPerload(AngelArrayEnemyInfoData.class, "userId", heroCapacity);

		magicChapterInfoCache = createForPerload(MagicChapterInfo.class, "userId", heroCapacity);

		groupDefendArmyItemCache = createForPerload(GFDefendArmyItem.class, "groupID", heroCapacity);

		groupFightBiddingItemCache = createForPerload(GFBiddingItem.class, "resourceID", heroCapacity);

		groupFightRewardItemCache = createForPerload(GFFinalRewardItem.class, "userID", heroCapacity);

		teamBattleItemCache = createForPerload(TBTeamItem.class, "hardID", heroCapacity);

		heroItemCache = createForPerload(FSHero.class, "other", "user_id", heroCapacity, true);

		mainHeroItemCache = createForPerload(FSHero.class, MAIN_ROLE_NAME, "id", heroCapacity, true);

		magicEquipFetterCache = createForPerload(MagicEquipFetterRecord.class, "userID", heroCapacity);

		embattleInfoItemCache = createForPerload(EmbattleInfo.class, "userId", heroCapacity);

		register(platformWhiteListCache = new PFMapItemStoreCache<TablePlatformWhiteList>(TablePlatformWhiteList.class, "accountId", heroCapacity, true));

		groupCompQuizItemCache = createForPerload(GCompUserQuizItem.class, "userID", heroCapacity);
	}

	private static <T extends IMapItem> void register(MapItemStoreCache<T> cache) {
		list.add(cache);
	}

	private static <T extends IMapItem> MapItemStoreCache<T> createForPerload(Class<T> clazz, String searchKey, int capacity) {
		return createForPerload(clazz, clazz.getSimpleName(), searchKey, capacity, true);
	}

	private static <T extends IMapItem> MapItemStoreCache<T> createForPerload(Class<T> clazz, String name, String searchKey, int capacity, boolean relatedToPlayer) {
		// Integer type = mapItemIntegration.get(clazz);
		MapItemStoreCache<T> cache = new MapItemStoreCache<T>(clazz, name, searchKey, capacity, null);
		list.add(cache);
		if (relatedToPlayer) {
			notifyCreateList.add(cache);
		}
		return cache;
	}

	public static void notifyPlayerCreated(String userId) {
		for (int i = notifyCreateList.size(); --i >= 0;) {
			MapItemStoreCache<? extends IMapItem> cache = notifyCreateList.get(i);
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
	 * 获取时装缓存
	 * 
	 * @return
	 */
	public static MapItemStoreCache<FashionItem> getFashionCache() {
		return fashionCache;
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

	public static MapItemStoreCache<ActivityDateTypeItem> getActivityDateTypeItemCache() {
		return activityDateTypeItemCache;
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

	/**
	 * 
	 * 获取帮派争霸竞猜的cache
	 * 
	 * @return
	 */
	public static MapItemStoreCache<GCompUserQuizItem> getGCompQuizItemCache() {
		return groupCompQuizItemCache;
	}

}
