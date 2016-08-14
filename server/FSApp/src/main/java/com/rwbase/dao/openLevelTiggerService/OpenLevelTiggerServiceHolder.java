package com.rwbase.dao.openLevelTiggerService;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rwbase.dao.openLevelTiggerService.pojo.OpenLevelTiggerServiceItem;

public class OpenLevelTiggerServiceHolder {
	private static OpenLevelTiggerServiceHolder instance = new OpenLevelTiggerServiceHolder();

	public static OpenLevelTiggerServiceHolder getInstance() {
		return instance;
	}
	
	/*
	 * 获取用户已经拥有的时装
	 */
	public List<OpenLevelTiggerServiceItem> getItemList(String userId) {
		List<OpenLevelTiggerServiceItem> itemList = new ArrayList<OpenLevelTiggerServiceItem>();
		Enumeration<OpenLevelTiggerServiceItem> mapEnum = getItemStore(userId)
				.getExtPropertyEnumeration();
		while (mapEnum.hasMoreElements()) {
			OpenLevelTiggerServiceItem item = (OpenLevelTiggerServiceItem) mapEnum
					.nextElement();			
			itemList.add(item);
		}

		return itemList;
	}
	
	
	
	
	public PlayerExtPropertyStore<OpenLevelTiggerServiceItem> getItemStore(String userId) {
//		RoleExtPropertyStoreCache<OpenLevelTiggerServiceItem> storeCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.OPENLEVEL_TIGGERSERVICE, OpenLevelTiggerServiceItem.class);
//		try {
//			return storeCache.getStore(userId);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return null;

	}
	
	
	
}
