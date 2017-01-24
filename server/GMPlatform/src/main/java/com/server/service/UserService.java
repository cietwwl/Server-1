package com.server.service;

import com.server.beans.User;

public interface UserService {


	/**
	 * 用户登录
	 * @param u
	 * @return
	 */
	boolean doUserLogin(User u);
	
	/**
	 * 检查是否存在账号
	 * @param account
	 * @return
	 */
	boolean checkUserExist(String account);

	/**
	 * 玩家注册
	 * @param user
	 * @return
	 */
	boolean registerUser(User user);
}
