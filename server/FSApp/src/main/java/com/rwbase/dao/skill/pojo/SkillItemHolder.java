package com.rwbase.dao.skill.pojo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.common.IHeroAction;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.hero.HeroExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SkillItemHolder {

	private static final SkillItemHolder _INSTANCE = new SkillItemHolder();
	
	public static final SkillItemHolder getSkillItemHolder() {
		return _INSTANCE;
	}
	
	final private eSynType skillSynType = eSynType.SKILL_ITEM;
	
	/*
	 * 获取用户已经拥有
	 */
	public List<SkillItem> getItemList(String heroId) {

		List<SkillItem> itemList = new ArrayList<SkillItem>();
		Enumeration<SkillItem> mapEnum = getMapItemStore(heroId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			SkillItem item = (SkillItem) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
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
		getMapItemStore(heroId).update(item.getId());
		ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.UPDATE_SINGLE);
		notifyChange(player.getUserId(), heroId);
	}

//	/**
//	 * 
//	 * 根据技能模板id，获取指定英雄的技能数据
//	 * 
//	 * @param ownerId
//	 * @param skillcfgId
//	 * @return
//	 */
//	public SkillItem getItem(String ownerId, String skillcfgId) {
//		Integer itemId = parseSkillItemId(skillcfgId);
//		return getItem(ownerId, itemId);
//	}
	
	/**
	 * 
	 * 根据英雄id和技能id，获取英雄的技能数据
	 * 
	 * @param heroId
	 * @param itemId
	 * @return
	 */
	public SkillItem getItemByItemId(String heroId, Integer itemId) {
		return getMapItemStore(heroId).get(itemId);
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
		Enumeration<SkillItem> mapEnum = getMapItemStore(heroId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			SkillItem item = (SkillItem) mapEnum.nextElement();
			if (item.getOrder() == order) {
				target = item;
				break;
			}
		}

		return target;
	}
	
	/**
	 * 
	 * 移除指定英雄的一个技能
	 * 
	 * @param player
	 * @param heroId
	 * @param item
	 * @return
	 */
	public boolean removeItem(Player player, String heroId, SkillItem item) {

		boolean success = getMapItemStore(heroId).removeItem(item.getId());
		if (success) {
			ClientDataSynMgr.updateData(player, item, skillSynType, eSynOpType.REMOVE_SINGLE);
			notifyChange(player.getUserId(), heroId);
		}
		return success;
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
		item.setId(parseSkillItemId(item.getSkillId()));

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
	 * 增加一批技能
	 * 
	 * @param player
	 * @param heroId
	 * @param skillList
	 */
	public void addItem(Player player, String heroId, List<SkillItem> skillList) {
		for (int i = skillList.size(); --i >= 0;) {
			SkillItem item = skillList.get(i);
			item.setOwnerId(heroId);
			item.setId(parseSkillItemId(item.getSkillId()));
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

	private Integer parseSkillItemId(String skillCfgId) {
		String skillId = skillCfgId;
		if(skillCfgId.contains("_")){
			skillId = StringUtils.substringBefore(skillCfgId, "_");
		}		
		// 生成技能id
		return Integer.valueOf(skillId);
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
	 * 
	 * 保存技能数据
	 * 
	 * @param heroId 目标英雄id
	 * @param immediately 是否马上写进数据库
	 */
//	public void flush(String heroId, boolean immediately) {
//		getMapItemStore(heroId).flush(immediately);
//	}
	
	private final List<IHeroAction> _dataChangeCallbacks = new ArrayList<IHeroAction>();
	
	public void regDataChangeCallback(IHeroAction callback) {
		_dataChangeCallbacks.add(callback);
	}
	
	private void notifyChange(String userId, String heroId) {
		for(IHeroAction action : _dataChangeCallbacks) {
			action.doAction(userId, heroId);
		}
	}
	
	private PlayerExtPropertyStore<SkillItem> getMapItemStore(String heroId) {
		RoleExtPropertyStoreCache<SkillItem> heroExtCache = RoleExtPropertyFactory.getHeroExtCache(HeroExtPropertyType.SKILL_ITEM, SkillItem.class);
		PlayerExtPropertyStore<SkillItem> store = null;
		try {
			store = heroExtCache.getStore(heroId);
		} catch (Throwable e) {
			GameLog.error(LogModule.Skill, "heroId:"+heroId, "can not get PlayerExtPropertyStore.", e);
		}
		return store;
	}
	
}
