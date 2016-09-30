package com.playerdata.fixEquip.exp.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.common.IHeroAction;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.hero.HeroExtPropertyType;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FixExpEquipDataItemHolder{
	
	final private eSynType synType = eSynType.FIX_EXP_EQUIP;
	
	private static final FixExpEquipDataItemHolder _INSTANCE = new FixExpEquipDataItemHolder();
	
	public static FixExpEquipDataItemHolder getInstance() {
		return _INSTANCE;
	}
	

	public List<FixExpEquipDataItem> getItemList(String heroId)	
	{
		
		List<FixExpEquipDataItem> itemList = new ArrayList<FixExpEquipDataItem>();
		Enumeration<FixExpEquipDataItem> mapEnum = getItemStore(heroId).getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			FixExpEquipDataItem item = (FixExpEquipDataItem) mapEnum.nextElement();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, FixExpEquipDataItem item){
		getItemStore(item.getOwnerId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		notifyChange(player.getUserId(), item.getOwnerId());
	}
	
	public void updateItemList(Player player, List<FixExpEquipDataItem> itemList){
		String heroId = null;
		for (FixExpEquipDataItem item : itemList) {			
			getItemStore(item.getOwnerId()).update(item.getId());
			if(heroId == null) {
				heroId = item.getOwnerId();
			}
		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
		notifyChange(player.getUserId(), heroId);
	}
	
	public FixExpEquipDataItem getItem(String heroId, Integer itemId) {
		return getItemStore(heroId).get(itemId);
	}
	
	
	public boolean initItems(Player player, String heroId, List<FixExpEquipDataItem> itemList){
	
		boolean addSuccess = false;
		try {
			addSuccess = getItemStore(heroId).addItem(itemList);
			if(addSuccess){
				ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
				notifyChange(player.getUserId(), heroId);
			}
		} catch (DuplicatedKeyException e) {
			GameLog.error(LogModule.FixEquip, "FixExpEquipDataItemHolder[initItems] ownerId:"+heroId, "重复主键", e);
		}
		return addSuccess;
	}
	
	
	public void synAllData(Player player, Hero hero){
		List<FixExpEquipDataItem> itemList = getItemList(hero.getUUId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	private void notifyChange(String playerId, String heroId) {
		for(IHeroAction heroAction : _dataChangeCallbacks) {
			heroAction.doAction(playerId, heroId);
		}
	}
	
	private List<IHeroAction> _dataChangeCallbacks = new ArrayList<IHeroAction>();
	
	public void regDataChangeCallback(IHeroAction callback) {
		_dataChangeCallbacks.add(callback);
	}

	
	private PlayerExtPropertyStore<FixExpEquipDataItem> getItemStore(String heroId) {
		RoleExtPropertyStoreCache<FixExpEquipDataItem> heroExtCache = RoleExtPropertyFactory.getHeroExtCache(HeroExtPropertyType.FIX_EXP_EQUIP, FixExpEquipDataItem.class);
		PlayerExtPropertyStore<FixExpEquipDataItem> store = null;
		try {
			store = heroExtCache.getStore(heroId);
		} catch (Throwable e) {
			GameLog.error(LogModule.FixEquip, "heroId:"+heroId, "can not get PlayerExtPropertyStore.", e);
		}
		return store;
	}
	
}
