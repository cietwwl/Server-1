package com.playerdata.group;

import com.rwbase.dao.group.pojo.db.GroupMemberData;

/*
 * @author HC
 * @date 2016年4月15日 下午2:27:33
 * @Description 
 */
public interface GroupMemberJoinCallback {
	/**
	 * 当加入帮派的时候有属性或者名字改变的话，刷新一次
	 * 
	 * @param groupMemberData
	 */
	public void updateGroupMemberData(GroupMemberData groupMemberData);
}