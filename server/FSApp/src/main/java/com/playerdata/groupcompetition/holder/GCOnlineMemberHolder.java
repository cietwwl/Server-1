package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCOnlineMemberDAO;
import com.playerdata.groupcompetition.holder.data.GCompOnlineMember;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCOnlineMemberHolder {

	private static final GCOnlineMemberHolder _instance = new GCOnlineMemberHolder();
	
	public static final GCOnlineMemberHolder getInstance() {
		return _instance;
	}
	
	private GCOnlineMemberDAO _dao;
	
	protected GCOnlineMemberHolder() {
		_dao = GCOnlineMemberDAO.getInstance();
	}
	
	public void syn(Player player, String groupId) {
		List<GCompOnlineMember> members = _dao.getOnlineMembers(groupId);
		if (members != null && members.size() > 0) {
			ClientDataSynMgr.synDataList(player, members, eSynType.GCompOnlineMember, eSynOpType.UPDATE_LIST);
		}
	}
	
	private void synToAll(String groupId, GCompOnlineMember member, eSynOpType opType) {
		List<GCompOnlineMember> allMembers = _dao.getOnlineMembers(groupId);
		List<Player> list = new ArrayList<Player>(allMembers.size());
		GCompOnlineMember temp;
		for (int i = 0, size = allMembers.size(); i < size; i++) {
			temp = allMembers.get(i);
			if (temp != member) {
				list.add(PlayerMgr.getInstance().find(temp.getUserId()));
			}
		}
		ClientDataSynMgr.synDataMutiple(list, member, eSynType.GCompOnlineMember, opType);
	}
	
	/**
	 * 
	 * 添加一个在线角色
	 * 
	 * @param player
	 * @param groupId
	 */
	public void addOnlineMember(Player player, String groupId) {
		List<GCompOnlineMember> onlineMembers = _dao.getOnlineMembers(groupId);
		String playerUserId = player.getUserId();
		for(int i = 0, size = onlineMembers.size(); i < size; i++) {
			GCompOnlineMember temp = onlineMembers.get(i);
			if(temp.getUserId().equals(playerUserId)) {
				return;
			}
		}
		GCompOnlineMember member = new GCompOnlineMember(player);
		_dao.addOnlineMembers(groupId, member);
		// 同步
		this.synToAll(groupId, member, eSynOpType.ADD_SINGLE);
	}
	
	/**
	 * 
	 * 删除一个在线角色
	 * 
	 * @param player
	 * @param groupId
	 */
	public void removeOnlineMember(Player player, String groupId) {
		GCompOnlineMember member = _dao.removeOnlineMember(groupId, player.getUserId());
		if(member != null) {
			// 同步
			this.synToAll(groupId, member, eSynOpType.REMOVE_SINGLE);
		}
	}
	
	public GCompOnlineMember getOnlineMember(Player player, String groupId) {
		List<GCompOnlineMember> onlineMembers = _dao.getOnlineMembers(groupId);
		for(GCompOnlineMember member : onlineMembers) {
			if(member.getUserId().equals(player.getUserId())) {
				return member;
			}
		}
		return null;
	}
	
	void addOnlineMemberList(String groupId) {
		_dao.addOnlineMemberList(groupId, new ArrayList<GCompOnlineMember>());
	}
	
	void reset() {
		this._dao.reset();
	}
}
