package com.rwbase.dao.chat;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.chat.pojo.UserPrivateChat;

/*
 * @author HC
 * @date 2015年8月12日 下午2:11:52
 * @Description 私聊的数据库表
 */
public class TableUserPrivateChatDao extends DataKVDao<UserPrivateChat> {
	private static TableUserPrivateChatDao instance = new TableUserPrivateChatDao();

	/**
	 * 获取DAO实例
	 * 
	 * @return
	 */
	public static TableUserPrivateChatDao getDao() {
		return instance;
	}
	
	/**
	 * 获取更新周期间隔(单位：秒)
	 * 
	 * @return
	 */
	protected int getUpdatedSeconds() {
		return 600;
	}
}