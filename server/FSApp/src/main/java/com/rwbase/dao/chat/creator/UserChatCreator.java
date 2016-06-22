package com.rwbase.dao.chat.creator;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.chat.pojo.UserPrivateChat;

/*
 * @author HC
 * @date 2016年6月19日 下午3:50:16
 * @Description 
 */
public class UserChatCreator implements DataExtensionCreator<UserPrivateChat> {

	@Override
	public UserPrivateChat create(String key) {
		UserPrivateChat userPrivateChat = new UserPrivateChat();
		userPrivateChat.setUserId(key);
		return userPrivateChat;
	}
}