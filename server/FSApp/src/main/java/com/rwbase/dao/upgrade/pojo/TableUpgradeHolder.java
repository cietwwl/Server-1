package com.rwbase.dao.upgrade.pojo;

import com.rwbase.dao.store.TableStoreDao;
import com.rwbase.dao.store.pojo.TableStore;


public class TableUpgradeHolder {
	
	final private String ownerId;
	private TableUpgradeDao tableUpgradeDao = TableUpgradeDao.getInstance();
	
	public TableUpgradeHolder(String userId){
		ownerId = userId;
	}
	
	public TableUpgradeData getTableUpgradeData(){
		TableUpgradeData upgradeData = TableUpgradeDao.getInstance().get(ownerId);
		return upgradeData;
	}
	
	public TableUpgradeData get(){
		return tableUpgradeDao.get(ownerId);
	}
	
	public void update(TableUpgradeData data, boolean blnUpdate){
		
		if(blnUpdate){
			TableUpgradeDao.getInstance().update(data);
		}else{
			TableUpgradeDao.getInstance().add(data);
		}
	}
}
