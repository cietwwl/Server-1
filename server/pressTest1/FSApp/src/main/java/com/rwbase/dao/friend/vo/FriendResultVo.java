package com.rwbase.dao.friend.vo;

import java.util.ArrayList;
import java.util.List;

import com.rwproto.FriendServiceProtos;
import com.rwproto.FriendServiceProtos.EFriendResultType;
import com.rwproto.FriendServiceProtos.FriendInfo;

public class FriendResultVo {
	public EFriendResultType resultType;
	public String resultMsg;
	public int powerCount;
	public List<FriendInfo> updateList = new ArrayList<FriendServiceProtos.FriendInfo>();
}
