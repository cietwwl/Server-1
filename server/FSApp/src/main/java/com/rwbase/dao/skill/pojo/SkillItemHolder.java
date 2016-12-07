package com.rwbase.dao.skill.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.IHeroAction;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.hero.HeroExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SkillItemHolder {

	private static SkillItemHolder _instance = new SkillItemHolder();
	private static Object PRESENT = new Object();

	public static SkillItemHolder getSkillItemHolder() {
		return _instance;
	}

	final private eSynType skillSynType = eSynType.SKILL_ITEM;

	/**
	 * 获取角色的技能
	 * 
	 * @param heroId
	 * @return
	 */
	public List<SkillItem> getItemList(String heroId) {
		RoleExtPropertyStore<SkillItem> mapItemStore = getMapItemStore(heroId);
		if (mapItemStore == null) {
			return Collections.emptyList();
		}

		Enumeration<SkillItem> mapEnum = mapItemStore.getExtPropertyEnumeration();
		List<SkillItem> itemList = new ArrayList<SkillItem>();
		while (mapEnum.hasMoreElements()) {
			SkillItem item = mapEnum.nextElement();
			itemList.add(item);
		}

		// 检查并初始化技能数据影响
		Object isInit = mapItemStore.getAttachment();
		if (isInit == null) {
			synchronized (mapItemStore) {
				isInit = mapItemStore.getAttachment();
				if (isInit == null) {
					SkillHelper.getInstance().checkAllSkill(itemList);
					mapItemStore.setAttachment(PRESENT);
				}
			}
		}

		return itemList;
	}

	/**
	 * 获取某个人所有的技能信息
	 * 
	 * @param heroId
	 * @return <技能的模版Id，技能数据>
	 */
	public Map<Integer, SkillItem> getItemMap(String heroId) {
		List<SkillItem> itemList = getItemList(heroId);
		if (itemList == null || itemList.isEmpty()) {
			return Collections.emptyMap();
		}

		int size = itemList.size();
		HashMap<Integer, SkillItem> skillMap = new HashMap<Integer, SkillItem>(size);
		for (int i = 0; i < size; i++) {
			SkillItem item = itemList.get(i);
			skillMap.put(item.getId(), item);
		}
		return skillMap;
	}

	/**
	 * 
	 * 更新一个技能数据
	 * 
	 * @param player
	 * @param heroId
	 * @param item
	 */
	public void updateItem(Player player, String heroId, SkillItem item) {
		if (!getMapItemStore(heroId).update(item.getId())) {// 更新不成功直接返回
			return;
		}

		ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.UPDATE_SINGLE);
		notifyChange(player.getUserId(), heroId);
	}

	/**
	 * 提交更新数据
	 * 
	 * @param heroId
	 * @param skillIdList
	 */
	public void updateSkillItemById(String heroId, List<Integer> skillIdList) {
		if (skillIdList == null) {
			return;
		}

		getMapItemStore(heroId).updateItems(skillIdList);
	}

	/**
	 * 同步变化的技能到前端并且调用notifyChange
	 * 
	 * @param player
	 * @param heroId
	 * @param skillList
	 */
	public void notifyChangedAndSynData(Player player, String heroId, List<SkillItem> skillList) {
		// TODO 要改前端换成eSynOpType.UPDATE_PART_LIST
		// 如果更改的技能是全部，可以调用eSynOpType.UPDATE_LIST
		for (int i = 0, size = skillList.size(); i < size; i++) {
			ClientDataSynMgr.updateData(player, skillList.get(i), skillSynType, eSynOpType.UPDATE_SINGLE);
		}
		notifyChange(player.getUserId(), heroId);
	}

	/**
	 * 
	 * 根据技能的索引顺序，获取技能数据
	 * 
	 * @param heroId
	 * @param order
	 * @return
	 */
	public SkillItem getByOrder(String heroId, int order) {
		SkillItem target = null;
		List<SkillItem> itemList = getItemList(heroId);
		for (int i = 0, size = itemList.size(); i < size; i++) {
			SkillItem item = itemList.get(i);
			if (item.getOrder() == order) {
				target = item;
				break;
			}
		}

		return target;
	}

	/**
	 * 
	 * 增加一个技能
	 * 
	 * @param player
	 * @param heroId
	 * @param item
	 * @param syn
	 * @return
	 */
	public boolean addItem(Player player, String heroId, SkillItem item, boolean syn) {
		item.setOwnerId(heroId);
		item.setId(SkillHelper.getInstance().parseSkillItemId(item.getSkillId()));

		boolean addSuccess = getMapItemStore(heroId).addItem(item);
		if (addSuccess) {
			if (syn) {
				ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.ADD_SINGLE);
			}
			notifyChange(player.getUserId(), heroId);
		}
		return addSuccess;
	}

	/**
	 * 
	 * 同步技能数据
	 * 
	 * @param player
	 * @param heroId
	 * @param version
	 */
	public void synAllData(Player player, String heroId, int version) {
		List<SkillItem> itemList = getItemList(heroId);
		ClientDataSynMgr.synDataList(player, itemList, skillSynType, eSynOpType.UPDATE_LIST);
	}

	/**
	 * 获取MapItemStore，这个对象限定只能在holder里访问
	 * 
	 * @param heroId
	 * @return
	 */
	private RoleExtPropertyStore<SkillItem> getMapItemStore(String heroId) {
		RoleExtPropertyStoreCache<SkillItem> heroExtCache = RoleExtPropertyFactory.getHeroExtCache(HeroExtPropertyType.SKILL_ITEM, SkillItem.class);
		RoleExtPropertyStore<SkillItem> store = null;
		try {
			store = heroExtCache.getStore(heroId);
		} catch (Throwable e) {
			GameLog.error(LogModule.Skill, "heroId:" + heroId, "can not get PlayerExtPropertyStore.", e);
		}
		return store;
	}

	// ======================================================一直存在的事件通知
	private final List<IHeroAction> _dataChangeCallbacks = new ArrayList<IHeroAction>();

	public void regDataChangeCallback(IHeroAction callback) {
		_dataChangeCallbacks.add(callback);
	}

	private void notifyChange(String userId, String heroId) {
		for (IHeroAction action : _dataChangeCallbacks) {
			action.doAction(userId, heroId);
		}
	}
}