package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.groupcompetition.holder.data.GCompOnlineMember;
import com.playerdata.groupcompetition.prepare.PrepareAreaMgr;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompCommonTask;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.playerdata.groupcompetition.util.IConsumer;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwbase.dao.group.pojo.Group;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.GroupCompetitionProto.EventsResult;
import com.rwproto.MsgDef.Command;

public class GCompOnlineMemberMgr {

	private static GCompOnlineMemberMgr _instance = new GCompOnlineMemberMgr();
	
	public static GCompOnlineMemberMgr getInstance() {
		return _instance;
	}
	
	private GCompOnlineMemberHolder _dataHolder = GCompOnlineMemberHolder.getInstance();
	private GCompOnlineMemberMonitor _monitor = new GCompOnlineMemberMonitor();
	
	protected GCompOnlineMemberMgr() {
		
	}
	
	private void createAndSendEndMsg(List<GCompAgainst> againsts) {
		for (int i = 0, size = againsts.size(); i < size; i++) {
			GCompAgainst against = againsts.get(i);
			EventsResult.Builder builder = EventsResult.newBuilder();
			builder.setWinGroupId(against.getWinGroupId());
			SendWinGroupMsgTask task = new SendWinGroupMsgTask();
			task.against = against;
			GCompCommonTask.scheduleCommonTask(task, builder.build(), System.currentTimeMillis() + 1000);
		}
	}
	
	private void sendEmptyListToClient(List<GCompAgainst> againsts) {
		for (int i = 0, size = againsts.size(); i < size; i++) {
			GCompAgainst against = againsts.get(i);
			String groupId;
			if ((groupId = against.getGroupA().getGroupId()).length() > 0) {
				GCompCommonTask.scheduleCommonTask(new GCompSendEmptyList(), groupId, System.currentTimeMillis() + 1000);
			}
			if ((groupId = against.getGroupB().getGroupId()).length() > 0) {
				GCompCommonTask.scheduleCommonTask(new GCompSendEmptyList(), groupId, System.currentTimeMillis() + 1000);
			}
		}
	}
	
	public boolean isMemberOnline(Player player) {
		Group group = GroupHelper.getGroup(player);
		if (group != null) {
			return _dataHolder.getOnlineMember(player.getUserId(), group.getGroupBaseDataMgr().getGroupData().getGroupId()) != null;
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
			_dataHolder.remove(player.getUserId(), groupId);
		}
	}
	
	public void onEventsStart(GCEventsType type, List<String> relativeGroupIds) {
		_monitor._on = true;
		FSGameTimerMgr.getInstance().submitSecondTask(_monitor, GCompCommonConfig.getOnlineMemberMonitorTaskInterval());
		_dataHolder.reset();
		for(int i = 0, size = relativeGroupIds.size(); i < size; i++) {
			_dataHolder.addOnlineMemberList(relativeGroupIds.get(i));
		}
	}
	
	public void onEventsEnd(GCEventsType type, List<GCompAgainst> againsts) {
		_monitor._on = false;
		try {
			this.sendEmptyListToClient(againsts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.createAndSendEndMsg(againsts);
	}
	
	public void changeUserTeamStatus(String userId, boolean inTeam) {
		String groupId = GroupHelper.getUserGroupId(userId);
		GCompOnlineMember member = _dataHolder.getOnlineMember(userId, groupId);
		if (member != null) {
			member.setInTeam(inTeam);
			_dataHolder.synToAll(groupId, member, eSynOpType.UPDATE_SINGLE);
		}
	}
	
	private static class SendWinGroupMsgTask implements IConsumer<EventsResult> {

		GCompAgainst against;
		
		private void sendToAll(EventsResult result, String groupId) {
			if (groupId != null && groupId.length() > 0) {
				List<GCompOnlineMember> members = GCompOnlineMemberHolder.getInstance().getAllOnlineMembersOfGroup(groupId);
				Player tempPlayer;
				PlayerMgr playerMgr = PlayerMgr.getInstance();
				for (int i = 0, size = members.size(); i < size; i++) {
					tempPlayer = playerMgr.find(members.get(i).getUserId());
					if (tempPlayer != null && playerMgr.isOnline(tempPlayer.getUserId())) {
						tempPlayer.SendMsg(Command.MSG_GROUP_COMPETITION_EVENTS_FINISHED, result.toByteString());
						GCompUtil.log("发送结束消息给客户端：{}，winnerId：{}", tempPlayer, result.getWinGroupId());
					}
				}
			}
		}
		
		@Override
		public void accept(EventsResult result) {
			sendToAll(result, against.getGroupA().getGroupId());
			sendToAll(result, against.getGroupB().getGroupId());
		}
		
	}
	
	private static class GCompSendEmptyList implements IConsumer<String> {

		@Override
		public void accept(String groupId) {
			List<GCompOnlineMember> onlineMembers = GCompOnlineMemberHolder.getInstance().getAllOnlineMembersOfGroup(groupId);
			List<Player> players = new ArrayList<Player>(onlineMembers.size());
			for (GCompOnlineMember member : onlineMembers) {
				if (PlayerMgr.getInstance().isOnline(member.getUserId())) {
					players.add(PlayerMgr.getInstance().find(member.getUserId()));
				}
			}
			GCompOnlineMemberHolder.getInstance().synEmptyList(players);
		}

	}
	
	private class GCompOnlineMemberMonitor implements IGameTimerTask {
		
		private boolean _on = true;

		@Override
		public String getName() {
			return "GCompOnlineMemberMonitor";
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
//			GCompUtil.log("---------- 帮派争霸在线玩家监控任务通知 ----------");
			Map<String, List<GCompOnlineMember>> map = GCompOnlineMemberMgr.this._dataHolder.getAllOnlineMembers();
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
					GCompOnlineMemberMgr.this._dataHolder.removeAll(groupId, removeMembers);
					for (GCompOnlineMember member : removeMembers) {
						GCompOnlineMemberMgr.this._dataHolder.synToAll(groupId, member, eSynOpType.REMOVE_SINGLE);
						GCompUtil.log("---------- 自动移除不在线的成员{} ----------", member.getUserName());
						try {
							GCompTeamMgr.getInstance().forcePlayerLeaveTeam(PlayerMgr.getInstance().find(member.getUserId()));
						} catch (Exception e) {
							e.printStackTrace();
						}
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
