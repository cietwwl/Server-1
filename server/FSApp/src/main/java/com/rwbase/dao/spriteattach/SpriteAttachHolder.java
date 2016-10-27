package com.rwbase.dao.spriteattach;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.IHeroAction;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.hero.HeroExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class SpriteAttachHolder {
	private static final SpriteAttachHolder _INSTANCE = new SpriteAttachHolder();

	public static final SpriteAttachHolder getInstance() {
		return _INSTANCE;
	}

	final private eSynType synType = eSynType.SPRITE_ATTACH_SYN;

	private PlayerExtPropertyStore<SpriteAttachSyn> getItemStore(String ownerId) {
		RoleExtPropertyStoreCache<SpriteAttachSyn> cache = RoleExtPropertyFactory.getHeroExtCache(HeroExtPropertyType.SPRITE_ATTACH_ITEM, SpriteAttachSyn.class);
		try {
			return cache.getStore(ownerId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<SpriteAttachSyn> getItemList(String heroId) {

		List<SpriteAttachSyn> itemList = new ArrayList<SpriteAttachSyn>();
		PlayerExtPropertyStore<SpriteAttachSyn> itemStore = getItemStore(heroId);
		if(itemStore == null){
			return itemList;
		}
		Enumeration<SpriteAttachSyn> mapEnum = itemStore.getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			SpriteAttachSyn item = (SpriteAttachSyn) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

	public SpriteAttachSyn getSpriteAttachSyn(String heroId) {
		List<SpriteAttachSyn> itemList = getItemList(heroId);
		if (itemList.size() > 0) {
			return itemList.get(0);
		} else {
			return null;
		}
	}

	// public void createSpriteAttachItem(Player player, Hero hero){
	// int heroModelId = hero.getModeId();
	// String heroId = hero.getId();
	// String userId = player.getUserId();
	// SpriteAttachRoleCfg spriteAttachRoleCfg =
	// SpriteAttachRoleCfgDAO.getInstance().getCfgById(String.valueOf(heroModelId));
	// if (spriteAttachRoleCfg != null) {
	// List<SpriteAttachItem> list = new ArrayList<SpriteAttachItem>();
	// int spriteItem1 = spriteAttachRoleCfg.getSpriteItem1();
	// craeteSpriteAttach(spriteItem1, list);
	// int spriteItem2 = spriteAttachRoleCfg.getSpriteItem2();
	// craeteSpriteAttach(spriteItem2, list);
	// int spriteItem3 = spriteAttachRoleCfg.getSpriteItem3();
	// craeteSpriteAttach(spriteItem3, list);
	// int spriteItem4 = spriteAttachRoleCfg.getSpriteItem4();
	// craeteSpriteAttach(spriteItem4, list);
	// int spriteItem5 = spriteAttachRoleCfg.getSpriteItem5();
	// craeteSpriteAttach(spriteItem5, list);
	// int spriteItem6 = spriteAttachRoleCfg.getSpriteItem6();
	// craeteSpriteAttach(spriteItem6, list);
	//
	// List<SpriteAttachSyn> result = new ArrayList<SpriteAttachSyn>();
	// SpriteAttachSyn syn = new SpriteAttachSyn();
	// syn.setItems(list);
	// syn.setId(heroModelId);
	// syn.setOwnerId(heroId);
	// result.add(syn);
	// return result;
	//
	// } else {
	// GameLog.error("SpriteAttach", "userId:" + userId, "找不到对应英雄的灵蕴点,英雄id：" +
	// heroId);
	// return null;
	// }
	// }

	public Map<Integer, SpriteAttachItem> getSpriteAttachItemMap(String heroId) {
		Map<Integer, SpriteAttachItem> itemMap = new HashMap<Integer, SpriteAttachItem>();

		SpriteAttachSyn spriteAttachSyn = getSpriteAttachSyn(heroId);
		if (spriteAttachSyn != null) {
			Map<Integer, SpriteAttachItem> items = spriteAttachSyn.getItemMap();
			for (Iterator<Entry<Integer, SpriteAttachItem>> iterator = items.entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, SpriteAttachItem> entry = iterator.next();
				SpriteAttachItem spriteAttachItem = entry.getValue();
				itemMap.put(spriteAttachItem.getSpriteAttachId(), spriteAttachItem);
			}
		}

		return itemMap;
	}

	public List<SpriteAttachItem> getSpriteAttachItemList(String heroId) {
		List<SpriteAttachItem> SpriteAttachItemList = new ArrayList<SpriteAttachItem>();

		SpriteAttachSyn spriteAttachSyn = getSpriteAttachSyn(heroId);
		if (spriteAttachSyn != null) {
			Map<Integer, SpriteAttachItem> items = spriteAttachSyn.getItemMap();
			for (Iterator<Entry<Integer, SpriteAttachItem>> iterator = items.entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, SpriteAttachItem> entry = iterator.next();
				SpriteAttachItemList.add(entry.getValue());	
			}
		}
		return SpriteAttachItemList;
	}
	
	

	public void updateItem(Player player, SpriteAttachSyn item) {
		getItemStore(item.getOwnerId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		notifyChange(player.getUserId(), item.getOwnerId());
	}

	private void notifyChange(String playerId, String heroId) {
		for (IHeroAction heroAction : _dataChangeCallbacks) {
			heroAction.doAction(playerId, heroId);
		}
	}

	public void synAllData(Player player, Hero hero) {
		SpriteAttachSyn spriteAttachSyn = getSpriteAttachSyn(hero.getId());
		if (spriteAttachSyn != null) {
			ClientDataSynMgr.synData(player, spriteAttachSyn, synType, eSynOpType.ADD_SINGLE);
		}
	}

	private List<IHeroAction> _dataChangeCallbacks = new ArrayList<IHeroAction>();

	public void regDataChangeCallback(IHeroAction callback) {
		_dataChangeCallbacks.add(callback);
	}
}
