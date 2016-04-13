package com.rw.support;

import com.rw.support.impl.FriendSupportImpl;

/**
 * 好友支持工厂
 * @author Jamaz
 *
 */
public class FriendSupportFactory {

	public static FriendSupportImpl friendSupport = new FriendSupportImpl();
	
	/**
	 * 获取好友支持实现
	 * @return
	 */
	public static FriendSupport getSupport(){
		return friendSupport;
	}
}
