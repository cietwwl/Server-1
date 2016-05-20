package com.rwbase.dao.whiteList;

import java.util.Enumeration;
import com.rw.common.MapItemStoreFactory;
import com.rw.fsutil.cacheDao.PFMapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;

public class TableWhiteListHolder{
	
	private String accountId;
	
	public TableWhiteListHolder(String accountId){
		this.accountId = accountId;
	}
	
	public MapItemStore<TableWhiteList> getTableWhiteListItemStore(){
		PFMapItemStoreCache<TableWhiteList> tableWhiteList = MapItemStoreFactory.getTableWhiteList();
		return tableWhiteList.getMapItemStore(accountId, TableWhiteList.class);
	}
	
	public TableWhiteList getTableWhiteList(){
		MapItemStore<TableWhiteList> tableWhiteListItemStore = getTableWhiteListItemStore();
		TableWhiteList tableWhiteList = new TableWhiteList();
		if (tableWhiteListItemStore != null) {
			Enumeration<TableWhiteList> list = tableWhiteListItemStore.getEnum();
			if (list != null) {

				while (list.hasMoreElements()) {
					tableWhiteList = (TableWhiteList) list.nextElement();
				}
			}
		}
		return tableWhiteList;
	}
	
	public boolean removeItem(String accountId){
		MapItemStore<TableWhiteList> tableWhiteListItemStore = getTableWhiteListItemStore();
		return tableWhiteListItemStore.removeItem(accountId);
	}
	
	public void saveItem(TableWhiteList item){
		MapItemStore<TableWhiteList> tableWhiteListItemStore = getTableWhiteListItemStore();
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
