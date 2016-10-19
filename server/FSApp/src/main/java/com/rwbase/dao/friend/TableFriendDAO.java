package com.rwbase.dao.friend;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TableFriendDAO extends DataKVDao<TableFriend> {
	private static TableFriendDAO m_instance = new TableFriendDAO();
	
	public TableFriendDAO(){
		
	}
	
	public static TableFriendDAO getInstance(){
		return m_instance;
	}
	
	/**
	 * 获取更新周期间隔(单位：秒)
	 * 
	 * @return
	 */
	protected int getUpdatedSeconds() {
		return 300;
	}
	
	@Override
	protected boolean forceUpdateOnEviction() {
		return false;
	}
}
