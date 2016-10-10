package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCOnlineMemberDAO;
import com.playerdata.groupcompetition.holder.data.GCompOnlineMember;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCompOnlineMemberHolder {

	private static final GCompOnlineMemberHolder _instance = new GCompOnlineMemberHolder();
	
	public static final GCompOnlineMemberHolder getInstance() {
		return _instance;
	}
	
	private GCOnlineMemberDAO _dao;
	
	protected GCompOnlineMemberHolder() {
		_dao = GCOnlineMemberDAO.getInstance();
	}
	
	public void syn(Player player, String groupId) {
		List<GCompOnlineMember> members = _dao.getOnlineMembers(groupId);
		if (members != null) {
			ClientDataSynMgr.updateDataList(player, members, eSynType.GCompOnlineMember, eSynOpType.UPDATE_LIST);
//			GCompUtil.log("同步在线成员给玩家，列表：{}，玩家：{}", members, player);
		}
	}
	
	void synToAll(String groupId, GCompOnlineMember member, eSynOpType opType) {
		List<GCompOnlineMember> allMembers = _dao.getOnlineMembers(groupId);
		List<Player> list = new ArrayList<Player>(allMembers.size());
		GCompOnlineMember temp;
		for (int i = 0, size = allMembers.size(); i < size; i++) {
			temp = allMembers.get(i);
			if (temp != member) {
				list.add(PlayerMgr.getInstance().find(temp.getUserId()));
			}
		}
//		GCompUtil.log("同步member给所有玩家，member：{}，玩家列表：{}，opType:{}", member, list, opType);
		if (list.size() > 0) {
			ClientDataSynMgr.synDataMutiple(list, member, eSynType.GCompOnlineMember, opType);
		}
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
	public void remove(String userId, String groupId) {
		GCompOnlineMember member = _dao.removeOnlineMember(userId, groupId);
//		GCompUtil.log("---------- 帮派争霸移除一个在线member:{} ----------", member);
		if(member != null) {
			// 同步
			this.synToAll(groupId, member, eSynOpType.REMOVE_SINGLE);
		}
	}
	
	public void removeAll(String groupId, List<GCompOnlineMember> members) {
		this._dao.removeOnlineMembers(groupId, members);
	}
	
	public GCompOnlineMember getOnlineMember(String userId, String groupId) {
		List<GCompOnlineMember> onlineMembers = _dao.getOnlineMembers(groupId);
		for(GCompOnlineMember member : onlineMembers) {
			if(member.getUserId().equals(userId)) {
				return member;
			}
		}
		return null;
	}
	
	void synEmptyList(List<Player> players) {
		com.playerdata.groupcompetition.util.GCompUtil.log("发送一个空的在线列表给玩家：{}", players.size() > 5 ? players.subList(0, 4) : players);
		ClientDataSynMgr.synDataMutiple(players, Collections.emptyList(), eSynType.GCompOnlineMember, eSynOpType.UPDATE_LIST);
	}
	
	void addOnlineMemberList(String groupId) {
		_dao.addOnlineMemberList(groupId, new ArrayList<GCompOnlineMember>());
	}
	
	void reset() {
		this._dao.reset();
	}
	
	List<GCompOnlineMember> getAllOnlineMembersOfGroup(String groupId) {
		return _dao.getOnlineMembers(groupId);
	}
	
	Map<String, List<GCompOnlineMember>> getAllOnlineMembers() {
		return _dao.getAllOnlineMembers();
	}
}
