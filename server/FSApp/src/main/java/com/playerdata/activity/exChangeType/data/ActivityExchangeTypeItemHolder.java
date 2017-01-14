package com.playerdata.activity.exChangeType.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.playerdata.Player;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
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
		RoleExtPropertyStore<ActivityExchangeTypeItem> itemStore = getItemStore(userId);
		Enumeration<ActivityExchangeTypeItem> mapEnum = itemStore.getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			ActivityExchangeTypeItem item = (ActivityExchangeTypeItem) mapEnum.nextElement();
			List<ActivityExchangeTypeCfg> typeCfgList = ActivityExchangeTypeCfgDAO.getInstance().isCfgByEnumIdEmpty(item.getEnumId());
			if(typeCfgList == null||typeCfgList.isEmpty()){
				continue;//配置表已经被策划删除,又不添加下一期的同类型活动orz...
			}
			boolean isRight = false;
			for(ActivityExchangeTypeCfg cfg: typeCfgList){
				if(StringUtils.equals(String.valueOf(cfg.getId()), item.getCfgId())){
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
		getItemStore(player.getUserId()).update(item.getId());
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public ActivityExchangeTypeItem getItem(String userId, ActivityExChangeTypeEnum exChangeTypeEnum){		
//		String itemId = ActivityExChangeTypeHelper.getItemId(userId, exChangeTypeEnum);
		int id = Integer.parseInt(exChangeTypeEnum.getCfgId());
		return getItemStore(userId).get(id);
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

	
	public RoleExtPropertyStore<ActivityExchangeTypeItem> getItemStore(String userId) {
//		RoleExtPropertyStoreCache<ActivityExchangeTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(null, ActivityExchangeTypeItem.class);
		RoleExtPropertyStoreCache<ActivityExchangeTypeItem> cach = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.ACTIVITY_EXCHANGE, ActivityExchangeTypeItem.class);
		
		try {
			return cach.getStore(userId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	
	}
	
}
