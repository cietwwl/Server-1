package com.rwbase.common;

import java.util.ArrayList;
import java.util.List;

import com.bm.groupSecret.data.group.GroupSecretDefLog;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dateType.data.ActivityDateTypeItem;
import com.playerdata.activity.rankType.data.ActivityRankTypeItem;
import com.playerdata.activity.rateType.data.ActivityRateTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
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
import com.rwbase.dao.fresherActivity.pojo.FresherActivityBigItem;
import com.rwbase.dao.group.pojo.db.GroupMemberData;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.magic.Magic;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.task.pojo.TaskItem;

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

	private static MapItemStoreCache<ActivityCountTypeItem> activityCountTypeItemCache;
	
	private static MapItemStoreCache<ActivityDailyTypeItem> activityDailyCountTypeItemCache;

	private static MapItemStoreCache<ActivityTimeCardTypeItem> activityTimeCardTypeItemCache;
	
	private static MapItemStoreCache<ActivityRateTypeItem> activityRateTypeItemCache;
	
	private static MapItemStoreCache<ActivityDateTypeItem> activityDateTypeItemCache;
	
	private static MapItemStoreCache<ActivityRankTypeItem> activityRankTypeItemCache;
	
	private static MapItemStoreCache<ActivityTimeCountTypeItem> activityTimeCountTypeItemCache;
	
	private static MapItemStoreCache<ActivityVitalityTypeItem> activityVitalityItemCache;
	
	
	private static MapItemStoreCache<FixExpEquipDataItem> fixExpEquipDataItemCache;
	
	private static MapItemStoreCache<FixNormEquipDataItem> fixNormEquipDataItemCache;
	
	private static MapItemStoreCache<GroupSecretDefLog> groupSecretDefLogCache;

	private static List<MapItemStoreCache> list;

	private static boolean init = false;
	
	static {
		init();
	}

	public static void init() {
		synchronized (MapItemStoreFactory.class) {
			if (init) {
				return;
			}else{
				init = true;
			}
		}
		ServerPerformanceConfig config = GameManager.getPerformanceConfig();

		// int playerCapacity = config.getPlayerCapacity();
		int heroCapacity = config.getPlayerCapacity();

		list = new ArrayList<MapItemStoreCache>();
		register(itemCache = new MapItemStoreCache<ItemData>(ItemData.class, "userId", heroCapacity));

		register(copyLevelRecord = new MapItemStoreCache<CopyLevelRecord>(CopyLevelRecord.class, "userId", heroCapacity));

		register(copyMapRecord = new MapItemStoreCache<CopyMapRecord>(CopyMapRecord.class, "userId", heroCapacity));

		register(equipCache = new MapItemStoreCache<EquipItem>(EquipItem.class, "ownerId", heroCapacity));

		register(fashionCache = new MapItemStoreCache<FashionItem>(FashionItem.class, "userId", heroCapacity));

		register(newGuideGiveItemHistoryCache = new MapItemStoreCache<GiveItemHistory>(GiveItemHistory.class, "userId", heroCapacity));

		register(fresherActivityCache = new MapItemStoreCache<FresherActivityBigItem>(FresherActivityBigItem.class, "ownerId", heroCapacity));

		register(inlayItemCache = new MapItemStoreCache<InlayItem>(InlayItem.class, "ownerId", heroCapacity));

		register(magicCache = new MapItemStoreCache<Magic>(Magic.class, "id", heroCapacity));

		register(skillCache = new MapItemStoreCache<Skill>(Skill.class, "ownerId", heroCapacity * 4));

		register(taskItemCache = new MapItemStoreCache<TaskItem>(TaskItem.class, "userId", heroCapacity));

		register(groupMemberCache = new MapItemStoreCache<GroupMemberData>(GroupMemberData.class, "groupId", heroCapacity));

		register(activityCountTypeItemCache = new MapItemStoreCache<ActivityCountTypeItem>(ActivityCountTypeItem.class, "userId", heroCapacity));

		register(activityTimeCardTypeItemCache = new MapItemStoreCache<ActivityTimeCardTypeItem>(ActivityTimeCardTypeItem.class, "userId", heroCapacity));
		
		register(activityRateTypeItemCache = new MapItemStoreCache<ActivityRateTypeItem>(ActivityRateTypeItem.class, "userId", heroCapacity));
//		
//		register(activityDateTypeItemCache = new MapItemStoreCache<ActivityDateTypeItem>(ActivityDateTypeItem.class, "userId", heroCapacity));
//
//		register(activityRankTypeItemCache = new MapItemStoreCache<ActivityRankTypeItem>(ActivityRankTypeItem.class, "userId", heroCapacity));
		
		register(activityTimeCountTypeItemCache = new MapItemStoreCache<ActivityTimeCountTypeItem>(ActivityTimeCountTypeItem.class, "userId", heroCapacity));
		
		register(activityDailyCountTypeItemCache = new MapItemStoreCache<ActivityDailyTypeItem>(ActivityDailyTypeItem.class, "userId", heroCapacity));
		
		register(fixExpEquipDataItemCache = new MapItemStoreCache<FixExpEquipDataItem>(FixExpEquipDataItem.class, "ownerId", heroCapacity));
		register(activityVitalityItemCache = new MapItemStoreCache<ActivityVitalityTypeItem>(ActivityVitalityTypeItem.class, "userId", heroCapacity));
//		register(fixExpEquipDataItemCache = new MapItemStoreCache<FixExpEquipDataItem>(FixExpEquipDataItem.class, "ownerId", heroCapacity));
		
		register(fixNormEquipDataItemCache = new MapItemStoreCache<FixNormEquipDataItem>(FixNormEquipDataItem.class, "ownerId", heroCapacity));
		
//		register(groupSecretDefLogCache = new MapItemStoreCache<GroupSecretDefLog>(GroupSecretDefLog.class, "secretId", heroCapacity));
		
		register(angelArrayTeamInfoData = new MapItemStoreCache<AngelArrayTeamInfoData>(AngelArrayTeamInfoData.class, "teamGroupId", heroCapacity));

		register(angelArrayFloorData = new MapItemStoreCache<AngelArrayFloorData>(AngelArrayFloorData.class, "userId", heroCapacity));

		register(angelArrayEnemyInfoData = new MapItemStoreCache<AngelArrayEnemyInfoData>(AngelArrayEnemyInfoData.class, "userId", heroCapacity));
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

	public static MapItemStoreCache<GiveItemHistory> getNewGuideGiveItemHistoryCache() {
		return newGuideGiveItemHistoryCache;
	}

	public static MapItemStoreCache<ActivityCountTypeItem> getActivityCountTypeItemCache() {
		return activityCountTypeItemCache;
	}
	
	public static MapItemStoreCache<ActivityDailyTypeItem> getActivityDailyCountTypeItemCache() {
		return activityDailyCountTypeItemCache;
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

	public static MapItemStoreCache<ActivityVitalityTypeItem> getActivityVitalityItemCache() {
		return activityVitalityItemCache;
	}
	
	
	public static MapItemStoreCache<FixExpEquipDataItem> getFixExpEquipDataItemCache() {
		return fixExpEquipDataItemCache;
	}
	
	

	public static MapItemStoreCache<FixNormEquipDataItem> getFixNormEquipDataItemCache() {
		return fixNormEquipDataItemCache;
	}
	

	public static MapItemStoreCache<GroupSecretDefLog> getGroupSecretDefLogCache() {
		return groupSecretDefLogCache;
	}

	public static void setGroupSecretDefLogCache(
			MapItemStoreCache<GroupSecretDefLog> groupSecretDefLogCache) {
		MapItemStoreFactory.groupSecretDefLogCache = groupSecretDefLogCache;
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
}
