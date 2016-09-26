package com.playerdata.activity.exChangeType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeHelper;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeHelper;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.activity.timeCountType.cfg.ActivityTimeCountTypeCfgDAO;
import com.playerdata.activity.timeCountType.data.ActivityTimeCountTypeItem;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ActivityExchangeTypeItemHolder{
	
	private static ActivityExchangeTypeItemHolder instance = new ActivityExchangeTypeItemHolder();
	
	public static ActivityExchangeTypeItemHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.ActivityExchangeType;
	
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<ActivityExchangeTypeItem> getItemList(String userId)	
	{
		
		List<ActivityExchangeTypeItem> itemList = new ArrayList<ActivityExchangeTypeItem>();
		MapItemStore<ActivityExchangeTypeItem> itemStore = getItemStore(userId);
		Enumeration<ActivityExchangeTypeItem> mapEnum = itemStore.getEnum();
		while (mapEnum.hasMoreElements()) {
			ActivityExchangeTypeItem item = (ActivityExchangeTypeItem) mapEnum.nextElement();
			List<ActivityExchangeTypeCfg> typeCfgList = ActivityExchangeTypeCfgDAO.getInstance().isCfgByEnumIdEmpty(item.getEnumId());
			if(typeCfgList == null||typeCfgList.isEmpty()){
				continue;//配置表已经被策划删除,又不添加下一期的同类型活动orz...
			}
			boolean isRight = false;
			for(ActivityExchangeTypeCfg cfg: typeCfgList){
				if(StringUtils.equals(cfg.getId(), item.getCfgId())){
					isRight = true;
				}
			}
			if(!isRight){//如果为false；则说明已经生成的数据，他对应的老配置表被改了枚举类型
				itemStore.removeItem(item.getId());
				continue;
			}
			itemList.add(item);
		}
		
		return itemList;
	}
	
	public void updateItem(Player player, ActivityExchangeTypeItem item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityExchangeTypeItem getItem(String userId, ActivityExChangeTypeEnum exChangeTypeEnum){		
		String itemId = ActivityExChangeTypeHelper.getItemId(userId, exChangeTypeEnum);
		return getItemStore(userId).getItem(itemId);
	}
	
	public boolean addItem(Player player, ActivityExchangeTypeItem item){
	
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<ActivityExchangeTypeItem> itemList){
		try {
			boolean addSuccess = getItemStore(player.getUserId()).addItem(itemList);
			if(addSuccess){
				ClientDataSynMgr.updateDataList(player, getItemList(player.getUserId()), synType, eSynOpType.UPDATE_LIST);
			}
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			//handle..
			e.printStackTrace();
			return false;
		}
	}
	
//	public boolean removeitem(Player player,ActivityCountTypeEnum type){
//		
//		String uidAndId = ActivityCountTypeHelper.getItemId(player.getUserId(), type);
//		boolean addSuccess = getItemStore(player.getUserId()).removeItem(uidAndId);
//		return addSuccess;
//	}
//	
	public void synAllData(Player player){
		List<ActivityExchangeTypeItem> itemList = getItemList(player.getUserId());		
//		Iterator<ActivityExchangeTypeItem> it = itemList.iterator();
//		while(it.hasNext()){
//			ActivityExchangeTypeItem item = (ActivityExchangeTypeItem)it.next();
//			if(ActivityExchangeTypeCfgDAO.getInstance().getCfgById(item.getCfgId()) == null){
////				removeItem(player, item);
//				it.remove();
//			}
//		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	public MapItemStore<ActivityExchangeTypeItem> getItemStore(String userId) {
		MapItemStoreCache<ActivityExchangeTypeItem> cache = MapItemStoreFactory.getActivityExchangeTypeItemCache();
		return cache.getMapItemStore(userId, ActivityExchangeTypeItem.class);
	}
	
}
