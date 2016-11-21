package com.rw.service.friend;

import java.util.Enumeration;

import com.rwbase.dao.friend.TableFriend;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.friend.vo.FriendResultVo;

/**
 * 基于TableFriend数据结构的封装
 * 
 * @author Jamaz
 *
 */
public interface FriendGetOperation {

	/**
	 * 获取指定某个关系的FriendItem
	 * @param tableFriend
	 * @param userId
	 * @return
	 */
	public FriendItem getItem(TableFriend tableFriend, String userId);

	/**
	 * 获取指定某种关系集
	 * @param tableFriend
	 * @return
	 */
	public Enumeration<FriendItem> getItemEnumeration(TableFriend tableFriend);

//	/**
//	 * <pre>
//	 * 向指定关系添加指定{@link FriendItem}
//	 * </pre>
//	 * @param host
//	 * @param guestUserId
//	 * @return
//	 */
//	public boolean addFriendItem(TableFriend host, String guestUserId, FriendResultVo resultVo);

}
