package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

public class GCompMemberMgr {

	
	private static final GCompMemberMgr _instance = new GCompMemberMgr();
	
	private Map<String, Map<String, GCompMember>> _allMembers = new HashMap<String, Map<String, GCompMember>>();
	private Map<String, List<GCompMember>> _sorted = new HashMap<String, List<GCompMember>>();
	
	public static final GCompMemberMgr getInstance() {
		return _instance;
	}
	
	private void checkAndAddGroupMember(Player player, String groupId) {
		if (groupId != null && groupId.length() > 0) {
			Map<String, GCompMember> map = _allMembers.get(groupId);
			if (map != null) {
				String userId = player.getUserId();
				if (!map.containsKey(userId)) {
					GCompMember member = new GCompMember(userId, player.getUserName(), player.getLevel(), player.getHeadImage());
					map.put(userId, member);
					_sorted.get(groupId).add(member);
				}
			}
		}
	}
	
	public void notifyEventsStart(List<String> groupIds) {
		this._allMembers.clear();
		for (String groupId : groupIds) {
			Map<String, GCompMember> map = new ConcurrentHashMap<String, GCompMember>();
			List<GCompMember> list = new ArrayList<GCompMember>();
			_allMembers.put(groupId, map);
			_sorted.put(groupId, list);
			Group group = GroupBM.get(groupId);
			List<? extends GroupMemberDataIF> allMembers = group.getGroupMemberMgr().getMemberSortList(null);
			for (GroupMemberDataIF member : allMembers) {
				GCompMember gcompMember = new GCompMember(member.getUserId(), member.getName(), member.getLevel(), member.getHeadId());
				map.put(gcompMember.getUserId(), gcompMember);
				list.add(gcompMember);
			}
			Collections.sort(list);
		}
	}

	public void onPlayerEnterPrepareArea(Player player) {
		String groupId = GroupHelper.getGroupId(player);
		this.checkAndAddGroupMember(player, groupId);
		GCompMember member = this.getGCompMember(groupId, player.getUserId());
		GCompMemberHolder.getInstance().syn(player, member);
	}
	
	public GCompMember getGCompMember(String groupId, String userId) {
		return _allMembers.get(groupId).get(userId);
	}
	
	public List<GCompMember> getArrayCopyOfAllMembers(String groupId) {
		List<GCompMember> arrayList = new ArrayList<GCompMember>();
		getCopyOfAllMembers(groupId, arrayList);
		return arrayList;
	}
	
	/**
	 * 
	 * copy所有的帮派成员数据到指定的集合中
	 * 
	 * @param groupId
	 * @param targetList
	 */
	public void getCopyOfAllMembers(String groupId, Collection<GCompMember> targetList) {
		List<GCompMember> members = _sorted.get(groupId);
		targetList.addAll(members);
	}
}
