package com.rwbase.dao.spriteattach;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public List<SpriteAttachSyn> getItemList(String heroId)	
	{
		
		List<SpriteAttachSyn> itemList = new ArrayList<SpriteAttachSyn>();
		Enumeration<SpriteAttachSyn> mapEnum = getItemStore(heroId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			SpriteAttachSyn item = (SpriteAttachSyn) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public Map<Integer, SpriteAttachItem> getSpriteAttachItemMap(String heroId){
		Map<Integer, SpriteAttachItem> itemMap = new HashMap<Integer, SpriteAttachItem>();
		List<SpriteAttachSyn> itemList = getItemList(heroId);
		SpriteAttachSyn spriteAttachSyn = itemList.get(0);
		List<SpriteAttachItem> items = spriteAttachSyn.getItems();
		for (SpriteAttachItem spriteAttachItem : items) {
			itemMap.put(spriteAttachItem.getSpriteAttachId(), spriteAttachItem);
		}
		
		return itemMap;
	}
	
	public List<SpriteAttachItem> getSpriteAttachItemList(String heroId){
		List<SpriteAttachItem> SpriteAttachItemList = new ArrayList<SpriteAttachItem>();
		List<SpriteAttachSyn> itemList = getItemList(heroId);
		if (itemList != null && itemList.size() > 0) {
			SpriteAttachSyn spriteAttachSyn = itemList.get(0);
			if (spriteAttachSyn != null) {
				List<SpriteAttachItem> items = spriteAttachSyn.getItems();
				for (SpriteAttachItem spriteAttachItem : items) {
					SpriteAttachItemList.add(spriteAttachItem);
				}
			}
		}
		return SpriteAttachItemList;
	}
	
	public void updateItem(Player player, SpriteAttachSyn item){
		getItemStore(item.getOwnerId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		notifyChange(player.getUserId(), item.getOwnerId());
	}
	
	
	private void notifyChange(String playerId, String heroId) {
		for(IHeroAction heroAction : _dataChangeCallbacks) {
			heroAction.doAction(playerId, heroId);
		}
	}
	
	public void synAllData(Player player, Hero hero){
		List<SpriteAttachSyn> itemList = getItemList(hero.getUUId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}
	
	private List<IHeroAction> _dataChangeCallbacks = new ArrayList<IHeroAction>();
	
	
	public void regDataChangeCallback(IHeroAction callback) {
		_dataChangeCallbacks.add(callback);
	}
}
