package com.rwbase.dao.user.loginInfo;

import com.rw.fsutil.cacheDao.PFDataRdbDao;

public class TableAccountLoginRecordDAO extends PFDataRdbDao<TableAccountLoginRecord>{
	
	private static TableAccountLoginRecordDAO instance = new TableAccountLoginRecordDAO();
	private TableAccountLoginRecordDAO(){};
	
	public static TableAccountLoginRecordDAO getInstance(){
		return instance;
	}
	
	public TableAccountLoginRecord get(String accountId){
		TableAccountLoginRecord record = this.findOneByKey("accountId", accountId);
		return record;
	}
	
	public void update(TableAccountLoginRecord record, boolean blnInsert){
		if(blnInsert){
			this.insertToDB(record);
		}else{
			this.updateToDB(record);
		}
		
	}
}
