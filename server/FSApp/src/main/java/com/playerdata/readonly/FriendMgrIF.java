package com.playerdata.readonly;

import java.util.List;

import com.rwbase.dao.friend.TableFriend;
import com.rwproto.FriendServiceProtos.FriendInfo;

public interface FriendMgrIF {

	public TableFriend getTableFriend();
	
	public List<FriendInfo> getBlackList();
}
