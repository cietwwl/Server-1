package com.rwbase.common;

import java.util.ArrayList;
import java.util.List;

import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.manager.GameManager;
import com.rw.manager.ServerPerformanceConfig;
import com.rwbase.dao.copy.pojo.CopyLevelRecord;
import com.rwbase.dao.copy.pojo.CopyMapRecord;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityBigItem;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItem;
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

	private static List<MapItemStoreCache> list;

	static {
		init();
	}

	public static void init() {
		ServerPerformanceConfig config = GameManager.getPerformanceConfig();

		// int playerCapacity = config.getPlayerCapacity();
		int heroCapacity = config.getPlayerCapacity();

		list = new ArrayList<MapItemStoreCache>();
		register(itemCache = new MapItemStoreCache<ItemData>(ItemData.class, "userId", heroCapacity));

		register(copyLevelRecord = new MapItemStoreCache<CopyLevelRecord>(CopyLevelRecord.class, "userId", heroCapacity));

		register(copyMapRecord = new MapItemStoreCache<CopyMapRecord>(CopyMapRecord.class, "userId", heroCapacity));

		register(equipCache = new MapItemStoreCache<EquipItem>(EquipItem.class, "ownerId", heroCapacity));

		register(fashionCache = new MapItemStoreCache<FashionItem>(FashionItem.class, "userId", heroCapacity));

		register(fresherActivityCache = new MapItemStoreCache<FresherActivityBigItem>(FresherActivityBigItem.class, "ownerId", heroCapacity));

		register(inlayItemCache = new MapItemStoreCache<InlayItem>(InlayItem.class, "ownerId", heroCapacity));

		register(magicCache = new MapItemStoreCache<Magic>(Magic.class, "id", heroCapacity));

		register(skillCache = new MapItemStoreCache<Skill>(Skill.class, "ownerId", heroCapacity * 4));

		register(taskItemCache = new MapItemStoreCache<TaskItem>(TaskItem.class, "userId", heroCapacity));

		register(groupMemberCache = new MapItemStoreCache<GroupMemberData>(GroupMemberData.class, "groupId", heroCapacity));

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
}
