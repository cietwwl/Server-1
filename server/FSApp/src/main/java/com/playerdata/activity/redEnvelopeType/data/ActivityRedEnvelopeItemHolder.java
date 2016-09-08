package com.playerdata.activity.redEnvelopeType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeHelper;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeHelper;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeEnum;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityRedEnvelopeItemHolder{
	
	private static ActivityRedEnvelopeItemHolder instance = new ActivityRedEnvelopeItemHolder();
	
	public static ActivityRedEnvelopeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityRedEnvelopeType;
	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityRedEnvelopeTypeItem> getItemList(String userId)	
	{
		
		List<ActivityRedEnvelopeTypeItem> itemList = new ArrayList<ActivityRedEnvelopeTypeItem>();
		Enumeration<ActivityRedEnvelopeTypeItem> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityRedEnvelopeTypeItem item = (ActivityRedEnvelopeTypeItem) mapEnum.nextElement();			
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void removeItem(Player player, ActivityRedEnvelopeTypeItem item){
		getItemStore(player.getUserId()).removeItem(item.getId());
	}
	
	public void updateItem(Player player, ActivityRedEnvelopeTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityRedEnvelopeTypeItem getItem(String userId){
		String itemId = ActivityRedEnvelopeHelper.getItemId(userId, ActivityRedEnvelopeTypeEnum.redEnvelope);
		return getItemStore(userId).getItem(itemId);
	}
	
//	public boolean removeItem(Player player, ActivityCountTypeItem item){
//		
//		boolean success = getItemStore(player.getUserId()).removeItem(item.getId());
//		if(success){
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
//		}
//		return success;
//	}
	
	public boolean addItem(Player player, ActivityRedEnvelopeTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public void synAllData(Player player){
		List<ActivityRedEnvelopeTypeItem> itemList = getItemList(player.getUserId());		
		Iterator<ActivityRedEnvelopeTypeItem> it = itemList.iterator();
		while(it.hasNext()){
			ActivityRedEnvelopeTypeItem item = (ActivityRedEnvelopeTypeItem)it.next();
			if(ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(item.getCfgId()) == null){
//				removeItem(player, item);
				it.remove();
			}
		}	
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<ActivityRedEnvelopeTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityRedEnvelopeTypeItem> cache = MapItemStoreFactory.getActivityRedEnvelopeTypeItemCache();
		return cache.getMapItemStore(userId, ActivityRedEnvelopeTypeItem.class);
	}
	
}
