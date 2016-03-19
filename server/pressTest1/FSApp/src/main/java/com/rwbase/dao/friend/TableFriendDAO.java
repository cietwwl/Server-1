package com.rwbase.dao.friend;

import com.rw.fsutil.cacheDao.DataKVDao;

public class TableFriendDAO extends DataKVDao<TableFriend> {
	private static TableFriendDAO m_instance = new TableFriendDAO();
	
	public TableFriendDAO(){
		
	}
	
	public static TableFriendDAO getInstance(){
		return m_instance;
	}
}
