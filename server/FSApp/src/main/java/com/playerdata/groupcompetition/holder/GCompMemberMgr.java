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
				GCompMember gcompMember = new GCompMember(member.getUserId(), member.getLevel());
				map.put(gcompMember.getUserId(), gcompMember);
				list.add(gcompMember);
			}
			Collections.sort(list);
		}
	}
	
	public void checkAndAddGroupMember(Player player) {
		String groupId = GroupHelper.getGroupId(player);
		if (groupId != null && groupId.length() > 0) {
			Map<String, GCompMember> map = _allMembers.get(groupId);
			if (map != null) {
				String userId = player.getUserId();
				if (!map.containsKey(userId)) {
					GCompMember member = new GCompMember(userId, player.getLevel());
					map.put(userId, member);
					_sorted.get(groupId).add(member);
					// P2DO 排序？
				}
			}
		}
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
	 * @param groupId
	 * @param isArray
	 * @return 如果 isArray为true，返回ArrayList类型，否则返回LinkedList类型
	 */
	public void getCopyOfAllMembers(String groupId, Collection<GCompMember> targetList) {
		List<GCompMember> members = _sorted.get(groupId);
		targetList.addAll(members);
	}
}
