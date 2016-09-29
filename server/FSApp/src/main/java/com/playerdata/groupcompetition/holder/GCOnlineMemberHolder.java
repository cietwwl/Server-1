package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupcompetition.dao.GCOnlineMemberDAO;
import com.playerdata.groupcompetition.holder.data.GCompOnlineMember;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GCOnlineMemberHolder {

	private static final GCOnlineMemberHolder _instance = new GCOnlineMemberHolder();
	
	public static final GCOnlineMemberHolder getInstance() {
		return _instance;
	}
	
	private GCOnlineMemberDAO _dao;
	private GCompOnlineMemberMonitor _monitor = new GCompOnlineMemberMonitor();
	
	protected GCOnlineMemberHolder() {
		_dao = GCOnlineMemberDAO.getInstance();
	}
	
	public void syn(Player player, String groupId) {
		List<GCompOnlineMember> members = _dao.getOnlineMembers(groupId);
		if (members != null) {
			ClientDataSynMgr.updateDataList(player, members, eSynType.GCompOnlineMember, eSynOpType.UPDATE_LIST);
			GCompUtil.log("同步在线成员给玩家，列表：{}，玩家：{}", members, player);
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
		GCompUtil.log("同步新加的member给所有玩家，member：{}，玩家列表：{}，opType:{}", member, list, opType);
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
	public void removeOnlineMember(Player player, String groupId) {
		GCompOnlineMember member = _dao.removeOnlineMember(groupId, player.getUserId());
		GCompUtil.log("---------- 帮派争霸移除一个在线member:{} ----------", member);
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
		FSGameTimerMgr.getInstance().submitSecondTask(_monitor, 30);
	}
	
	void onEventsEnd() {
		this._monitor._on = false;
	}
	
	private class GCompOnlineMemberMonitor implements IGameTimerTask {
		
		private boolean _on = true;

		@Override
		public String getName() {
			return "GCompOnlineMemberMonitor";
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			GCompUtil.log("---------- 帮派争霸在线玩家监控任务通知 ----------");
			GCOnlineMemberDAO dao = GCOnlineMemberHolder.this._dao;
			Map<String, List<GCompOnlineMember>> map = dao.getAllOnlineMembers();
			for (Iterator<String> keyItr = map.keySet().iterator(); keyItr.hasNext();) {
				String groupId = keyItr.next();
				List<GCompOnlineMember> list = map.get(groupId);
				List<String> onlineUserId = PrepareAreaMgr.getInstance().getOnlineUserFromPrepareScene(groupId);
				List<GCompOnlineMember> removeMembers = new ArrayList<GCompOnlineMember>();
				for (GCompOnlineMember member : list) {
					if (!onlineUserId.contains(member.getUserId())) {
						removeMembers.add(member);
					}
				}
				if (removeMembers.size() > 0) {
					dao.removeOnlineMembers(groupId, removeMembers);
					for (GCompOnlineMember member : removeMembers) {
						GCOnlineMemberHolder.this.synToAll(groupId, member, eSynOpType.REMOVE_SINGLE);
						GCompUtil.log("---------- 自动移除不在线的成员{} ----------", member.getUserName());
					}
				}
			}
			return "SUCCESS";
		}

		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
			
		}

		@Override
		public void rejected(RejectedExecutionException e) {
			
		}

		@Override
		public boolean isContinue() {
			return _on;
		}

		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}
		
	}
}
