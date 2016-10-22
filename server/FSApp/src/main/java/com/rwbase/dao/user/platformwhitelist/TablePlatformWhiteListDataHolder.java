package com.rwbase.dao.user.platformwhitelist;

import java.util.Enumeration;

import com.rw.fsutil.cacheDao.PFMapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;

public class TablePlatformWhiteListDataHolder {
	
	private String accountId;
	
	public TablePlatformWhiteListDataHolder(String accountId){
		this.accountId = accountId;
	}
	
	public MapItemStore<TablePlatformWhiteList> getTableWhiteListItemStore(){
		PFMapItemStoreCache<TablePlatformWhiteList> platformWhiteListCache = MapItemStoreFactory.getPlatformWhiteListCache();
		return platformWhiteListCache.getMapItemStore(accountId, TablePlatformWhiteList.class);
	}
	
	public TablePlatformWhiteList getTablePlatformWhiteList(){
		MapItemStore<TablePlatformWhiteList> tableWhiteListItemStore = getTableWhiteListItemStore();
		TablePlatformWhiteList tablePlatformWhiteList = new TablePlatformWhiteList();
		if (tableWhiteListItemStore != null) {
			Enumeration<TablePlatformWhiteList> list = tableWhiteListItemStore.getEnum();
			if (list != null) {

				while (list.hasMoreElements()) {
					tablePlatformWhiteList = (TablePlatformWhiteList) list.nextElement();
				}
			}
		}
		return tablePlatformWhiteList;
	}
	
	public boolean removeItem(String accountId){
		MapItemStore<TablePlatformWhiteList> tableWhiteListItemStore = getTableWhiteListItemStore();
		return tableWhiteListItemStore.removeItem(accountId);
	}
	
	public void saveItem(TablePlatformWhiteList item){
		MapItemStore<TablePlatformWhiteList> tableWhiteListItemStore = getTableWhiteListItemStore();
		if(tableWhiteListItemStore.getSize() > 0){
			tableWhiteListItemStore.updateItem(item);
		}else{
			tableWhiteListItemStore.addItem(item);
		}
	}

	public String getAccountId() {
		return accountId;
	} 
}
