package com.rwbase.dao.skill.pojo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.common.IHeroAction;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SkillItemHolder {

	private static final SkillItemHolder _INSTANCE = new SkillItemHolder();
	
	public static final SkillItemHolder getSkillItemHolder() {
		return _INSTANCE;
	}
	
	final private eSynType skillSynType = eSynType.SKILL_ITEM;

//	/*
//	 * 获取用户已经拥有
//	 */
//	public List<Skill> getItemList() {
//
//		List<Skill> itemList = new ArrayList<Skill>();
//		Enumeration<Skill> mapEnum = getMapItemStore().getEnum();
//		while (mapEnum.hasMoreElements()) {
//			Skill item = (Skill) mapEnum.nextElement();
//			itemList.add(item);
//		}
//
//		return itemList;
//	}
	
	/*
	 * 获取用户已经拥有
	 */
	public List<Skill> getItemList(String heroId) {

		List<Skill> itemList = new ArrayList<Skill>();
		Enumeration<Skill> mapEnum = getMapItemStore(heroId).getEnum();
		while (mapEnum.hasMoreElements()) {
			Skill item = (Skill) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

//	public void updateItem(Player player, Skill item) {
//		getMapItemStore().updateItem(item);
//		ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.UPDATE_SINGLE);
//		notifyChange();
//	}
	
	public void updateItem(Player player, String heroId, Skill item) {
		getMapItemStore(heroId).updateItem(item);
		ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.UPDATE_SINGLE);
		notifyChange(player.getUserId(), heroId);
	}

	public Skill getItem(String ownerId, String skillcfgId) {
		String itemId = SkillHelper.getItemId(ownerId, skillcfgId);
		return getItem(ownerId, itemId);
	}

//	public Skill getItem(String itemId) {
//		return getMapItemStore().getItem(itemId);
//	}
	
	public Skill getItemByItemId(String heroId, String itemId) {
		return getMapItemStore(heroId).getItem(itemId);
	}

//	public Skill getByOrder(int order) {
//		Skill target = null;
//		Enumeration<Skill> mapEnum = getMapItemStore().getEnum();
//		while (mapEnum.hasMoreElements()) {
//			Skill item = (Skill) mapEnum.nextElement();
//			if (item.getOrder() == order) {
//				target = item;
//				break;
//			}
//		}
//
//		return target;
//	}
	
	public Skill getByOrder(String heroId, int order) {
		Skill target = null;
		Enumeration<Skill> mapEnum = getMapItemStore(heroId).getEnum();
		while (mapEnum.hasMoreElements()) {
			Skill item = (Skill) mapEnum.nextElement();
			if (item.getOrder() == order) {
				target = item;
				break;
			}
		}

		return target;
	}

//	public boolean removeItem(Player player, Skill item) {
//
//		boolean success = getMapItemStore().removeItem(item.getId());
//		if (success) {
//			ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.REMOVE_SINGLE);
//			notifyChange();
//		}
//		return success;
//	}
	
	public boolean removeItem(Player player, String heroId, Skill item) {

		boolean success = getMapItemStore(heroId).removeItem(item.getId());
		if (success) {
			ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.REMOVE_SINGLE);
			notifyChange(player.getUserId(), heroId);
		}
		return success;
	}

//	public boolean addItem(Player player, Skill item, boolean syn) {
//		item.setOwnerId(ownerId);
//		item.setId(newSkillItemId(ownerId, item.getSkillId()));
//
//		boolean addSuccess = getMapItemStore().addItem(item);
//		if (addSuccess) {
//			if (syn) {
//				ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.ADD_SINGLE);
//			}
//			notifyChange();
//		}
//		return addSuccess;
//	}
	
	public boolean addItem(Player player, String heroId, Skill item, boolean syn) {
		item.setOwnerId(heroId);
		item.setId(newSkillItemId(heroId, item.getSkillId()));

		boolean addSuccess = getMapItemStore(heroId).addItem(item);
		if (addSuccess) {
			if (syn) {
				ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.ADD_SINGLE);
			}
			notifyChange(player.getUserId(), heroId);
		}
		return addSuccess;
	}

//	public void addItem(Player player, List<Skill> skillList) {
//		for (int i = skillList.size(); --i >= 0;) {
//			Skill item = skillList.get(i);
//			item.setOwnerId(ownerId);
//			item.setId(newSkillItemId(ownerId, item.getSkillId()));
//		}
//		try {
//			getMapItemStore().addItem(skillList);
//			ClientDataSynMgr.updateDataList(player, skillList, skillSynType, eSynOpType.UPDATE_LIST);
//			notifyChange();
//		} catch (DuplicatedKeyException e) {
//			GameLog.error("SkillItemHolder", "#addItem()", "批量添加技能失败：" + ownerId, e);
//			e.printStackTrace();
//		}
//	}
	
	public void addItem(Player player, String heroId, List<Skill> skillList) {
		for (int i = skillList.size(); --i >= 0;) {
			Skill item = skillList.get(i);
			item.setOwnerId(heroId);
			item.setId(newSkillItemId(heroId, item.getSkillId()));
		}
		try {
			getMapItemStore(heroId).addItem(skillList);
			ClientDataSynMgr.updateDataList(player, skillList, skillSynType, eSynOpType.UPDATE_LIST);
			notifyChange(player.getUserId(), heroId);
		} catch (DuplicatedKeyException e) {
			GameLog.error("SkillItemHolder", "#addItem()", "批量添加技能失败：" + heroId, e);
			e.printStackTrace();
		}
	}

	private String newSkillItemId(String ownerId, String skillId) {
		return ownerId + "_" + skillId;
	}

//	public void synAllData(Player player, int version) {
//		List<Skill> itemList = getItemList();
//		ClientDataSynMgr.synDataList(player, itemList, skillSynType, eSynOpType.UPDATE_LIST);
//	}
	
	public void synAllData(Player player, String heroId, int version) {
		List<Skill> itemList = getItemList(heroId);
		ClientDataSynMgr.synDataList(player, itemList, skillSynType, eSynOpType.UPDATE_LIST);
	}

	// public AttrData toAttrData() {
	// AttrData attrData = new AttrData();
	// Enumeration<Skill> mapEnum = getMapItemStore().getEnum();
	// while (mapEnum.hasMoreElements()) {
	// Skill item = (Skill) mapEnum.nextElement();
	// if (item.getLevel() <= 0) {
	// continue;
	// }
	// SkillCfg pSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(item.getSkillId());
	// attrData.plus(AttrData.fromObject(pSkillCfg));
	//
	// }
	// return attrData;
	// }

	public void flush(String heroId, boolean immediately) {
		getMapItemStore(heroId).flush(immediately);
	}
	
	private final List<IHeroAction> _dataChangeCallbacks = new ArrayList<IHeroAction>();
	
	public void regDataChangeCallback(IHeroAction callback) {
		_dataChangeCallbacks.add(callback);
	}
	
	private void notifyChange(String userId, String heroId) {
		for(IHeroAction action : _dataChangeCallbacks) {
			action.doAction(userId, heroId);
		}
	}

//	private MapItemStore<Skill> getMapItemStore() {
//		MapItemStoreCache<Skill> cache = MapItemStoreFactory.getSkillCache();
//		return cache.getMapItemStore(ownerId, Skill.class);
//	}
	
	private MapItemStore<Skill> getMapItemStore(String heroId) {
		MapItemStoreCache<Skill> cache = MapItemStoreFactory.getSkillCache();
		return cache.getMapItemStore(heroId, Skill.class);
	}
}
