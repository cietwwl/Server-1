package com.rwbase.dao.whiteList;

import java.util.HashMap;
import java.util.List;

import com.rw.fsutil.cacheDao.PFDataRdbDao;

public class TableWhiteListDAO extends PFDataRdbDao<TableWhiteList> {
	private HashMap<String, TableWhiteList> WhiteListMap = new HashMap<String, TableWhiteList>();
	
	private static TableWhiteListDAO instance = new TableWhiteListDAO();
	
	public static TableWhiteListDAO getInstance(){
		return instance;
	}
	
	public void queryWhiteList() {
		String sql = "select * from mt_platform_whitelist";
		List<TableWhiteList> result = findBySql(sql);
		for (TableWhiteList tableWhiteList : result) {
			WhiteListMap.put(tableWhiteList.getAccountId(), tableWhiteList);
		}
	}
	
	public void addWhiteList(TableWhiteList item){
		if(WhiteListMap.containsKey(item.getAccountId())){
			TableWhiteList tableWhiteList = WhiteListMap.get(item.getAccountId());
			tableWhiteList.setClose(item.isClose());
		}else{
			WhiteListMap.put(item.getAccountId(), item);
		}
	}
	
	public void updateWhiteList(TableWhiteList item){
		TableWhiteList tableWhiteList = WhiteListMap.get(item.getAccountId());
		tableWhiteList.setClose(item.isClose());
	}
	
	public void removeWhiteList(String accountId){
		WhiteListMap.remove(accountId);
	}
	
	public TableWhiteList getWhiteListItem(String accountId){
		TableWhiteList tableWhiteList = WhiteListMap.get(accountId);
		if(tableWhiteList == null){
			tableWhiteList = new TableWhiteList();
		}
		return tableWhiteList;
	}
	
	public boolean CheckIsInWhiteList(String accountId){
		TableWhiteList tableWhiteList = WhiteListMap.get(accountId);
		if(tableWhiteList == null){
			return false;
		}else{
			return !tableWhiteList.isClose();
		}
	}
}
