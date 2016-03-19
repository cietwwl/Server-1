package com.rwbase.dao.chat;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.chat.pojo.UserPrivateChat;

/*
 * @author HC
 * @date 2015年8月12日 下午2:11:52
 * @Description 私聊的数据库表
 */
public class TableUserPrivateChatDao extends DataKVDao<UserPrivateChat> {
	private static TableUserPrivateChatDao instance;

	/**
	 * 获取DAO实例
	 * 
	 * @return
	 */
	public static TableUserPrivateChatDao getDao() {
		if (instance == null) {
			instance = new TableUserPrivateChatDao();
		}

		return instance;
	}
}