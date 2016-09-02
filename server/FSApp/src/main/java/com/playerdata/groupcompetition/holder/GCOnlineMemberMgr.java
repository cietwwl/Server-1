package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.util.GCEventsType;
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
	
	public boolean isMemberOnline(Player player) {
		Group group = GroupHelper.getGroup(player);
		if (group != null) {
			return _dataHolder.getOnlineMember(player, group.getGroupBaseDataMgr().getGroupData().getGroupId()) != null;
		}
		return false;
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
	
	public void onEventsStart(GCEventsType type, List<String> relativeGroupIds) {
		_dataHolder.reset();
		for(int i = 0, size = relativeGroupIds.size(); i < size; i++) {
			_dataHolder.addOnlineMemberList(relativeGroupIds.get(i));
		}
	}
}
