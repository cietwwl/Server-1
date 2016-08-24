package com.playerdata.groupcompetition.holder;

import com.playerdata.Player;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;

public class GCOnlineMemberMgr {

	private static final GCOnlineMemberMgr _instance = new GCOnlineMemberMgr();
	
	public static final GCOnlineMemberMgr getInstance() {
		return _instance;
	}
	
	private GCOnlineMemberHolder _dataHolder = GCOnlineMemberHolder.getInstance();
	
	protected GCOnlineMemberMgr() {
		
	}
	
	public void sendOnlineMembers(Player player) {
		Group group = GroupHelper.getGroup(player);
		if (group != null) {
			_dataHolder.syn(player, group.getGroupBaseDataMgr().getGroupData().getGroupId());
		}
	}
	
	public void addToOnlineMembers(Player player) {
		String groupId = GroupHelper.getGroupId(player);
		if (groupId != null) {
			_dataHolder.addOnlineMember(player, groupId);
		}
	}
	
	public void removeOnlineMembers(Player player) {
		String groupId = GroupHelper.getGroupId(player);
		if(groupId != null) {
			_dataHolder.removeOnlineMember(player, groupId);
		}
	}
}
