package com.rwbase.dao.chat.pojo;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;

/**
 * @Author HC
 * @date 2016年12月17日 上午11:20:22
 * @desc 聊天违规的数据的构造
 **/

public class ChatIllegalDataCreator implements DataExtensionCreator<ChatIllegalData> {

	@Override
	public ChatIllegalData create(String key) {
		ChatIllegalData data = new ChatIllegalData();
		data.setUserId(key);
		return data;
	}
}