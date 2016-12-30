package com.rwbase.dao.chat.pojo;

import com.rw.fsutil.cacheDao.DataKVDao;

/**
 * @Author HC
 * @date 2016年12月17日 上午11:17:53
 * @desc
 **/

public class ChatIllegalDataDAO extends DataKVDao<ChatIllegalData> {
	private static ChatIllegalDataDAO dao = new ChatIllegalDataDAO();

	public static ChatIllegalDataDAO getDAO() {
		return dao;
	}

	protected ChatIllegalDataDAO() {
	}
}