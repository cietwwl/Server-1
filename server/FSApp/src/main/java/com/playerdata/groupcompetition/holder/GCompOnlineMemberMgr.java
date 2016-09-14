package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;

public class GCompOnlineMemberMgr {

	private static final GCompOnlineMemberMgr _instance = new GCompOnlineMemberMgr();
	
	public static final GCompOnlineMemberMgr getInstance() {
		return _instance;
	}
	
	private GCompOnlineMemberHolder _dataHolder = GCompOnlineMemberHolder.getInstance();
	
	protected GCompOnlineMemberMgr() {
		
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
	
	public void onEventsEnd(GCEventsType type) {
		this._dataHolder.onEventsEnd();
	}
}
