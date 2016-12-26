package com.rwbase.dao.friend.readonly;

import java.util.Enumeration;

import com.playerdata.readonly.FriendGiveStateIF;
import com.playerdata.readonly.FriendItemIF;
import com.rwbase.dao.friend.vo.FriendVo;

public interface TableFriendIF {
	public String getUserId();
	
	public Enumeration<? extends FriendItemIF> getFriendMap();
	
	public FriendItemIF getFriendItem(String key);
	
//	public Enumeration<? extends FriendItemIF> getRequestMap();
	
	public FriendItemIF getRequestItem(String key);
	
	public Enumeration<? extends FriendItemIF> getBlackMap();
	
	public FriendItemIF getBlackItem(String key);
	
	public Enumeration<? extends FriendGiveStateIF> getFriendGiveMap();
	
	public FriendGiveStateIF getFriendGiveState(String key);
	
	public FriendVo getFriendVo();
}
