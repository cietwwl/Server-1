package com.playerdata.groupcompetition.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.groupcompetition.holder.data.GCompOnlineMember;

public class GCOnlineMemberDAO {

	private static final GCOnlineMemberDAO _instance = new GCOnlineMemberDAO();
	
	public static final GCOnlineMemberDAO getInstance() {
		return _instance;
	}
	
	private Map<String, List<GCompOnlineMember>> _dataMap = new HashMap<String, List<GCompOnlineMember>>();
	private Map<String, List<GCompOnlineMember>> _dataMapRO = new HashMap<String, List<GCompOnlineMember>>();
	
	private List<GCompOnlineMember> addGroup(String groupId) {
		synchronized (_dataMap) {
			List<GCompOnlineMember> list = _dataMap.get(groupId);
			if (list == null) {
				list = new ArrayList<GCompOnlineMember>();
				_dataMap.put(groupId, list);
				_dataMapRO.put(groupId, Collections.unmodifiableList(list));
			}
			return list;
		}
	}
	
	public void addOnlineMemberList(String groupId, List<GCompOnlineMember> list) {
		this.addGroup(groupId);
	}
	
	public List<GCompOnlineMember> getOnlineMembers(String groupId) {
		return _dataMap.get(groupId);
	}
	
	public void addOnlineMembers(String groupId, GCompOnlineMember member) {
		List<GCompOnlineMember> members = _dataMap.get(groupId);
		members.add(member);
	}
	
	public GCompOnlineMember removeOnlineMember(String userId, String groupId) {
		List<GCompOnlineMember> members = _dataMap.get(groupId);
		if (members != null) {
			synchronized (members) {
				for (int i = 0, size = members.size(); i < size; i++) {
					GCompOnlineMember member = members.get(i);
					if (member.getUserId().equals(userId)) {
						return members.remove(i);
					}
				}
			}
		}
		return null;
	}
	
	public void removeOnlineMembers(String groupId, List<GCompOnlineMember> removes) {
		List<GCompOnlineMember> members = _dataMap.get(groupId);
		if (members != null) {
			synchronized (members) {
				members.removeAll(removes);
			}
		}
	}
	
	/**
	 * 
	 * 获取所有在线的成员
	 * 
	 * @return
	 */
	public Map<String, List<GCompOnlineMember>> getAllOnlineMembers() {
		return _dataMapRO;
	}
	
	public void reset() {
		this._dataMap.clear();
	}
}
