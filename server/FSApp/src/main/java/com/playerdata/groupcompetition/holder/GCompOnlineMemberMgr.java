package com.playerdata.groupcompetition.holder;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.groupcompetition.holder.data.GCompOnlineMember;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompCommonTask;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.playerdata.groupcompetition.util.IConsumer;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwproto.GroupCompetitionProto.EventsResult;
import com.rwproto.MsgDef.Command;

public class GCompOnlineMemberMgr {

	private static final GCompOnlineMemberMgr _instance = new GCompOnlineMemberMgr();
	
	public static final GCompOnlineMemberMgr getInstance() {
		return _instance;
	}
	
	private GCompOnlineMemberHolder _dataHolder = GCompOnlineMemberHolder.getInstance();
	
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
	
	public void onEventsEnd(GCEventsType type, List<GCompAgainst> againsts) {
		this._dataHolder.onEventsEnd();
		this.createAndSendEndMsg(againsts);
	}
	
	private static class SendWinGroupMsgTask implements IConsumer<EventsResult> {

		GCompAgainst against;
		
		private void sendToAll(EventsResult result, String groupId) {
			if (groupId != null && groupId.length() > 0) {
				List<GCompOnlineMember> members = GCompOnlineMemberHolder.getInstance().getAllOnlineMembers(groupId);
				Player tempPlayer;
				PlayerMgr playerMgr = PlayerMgr.getInstance();
				for (int i = 0, size = members.size(); i < size; i++) {
					tempPlayer = playerMgr.find(members.get(i).getUserId());
					if (tempPlayer != null && playerMgr.isOnline(tempPlayer.getUserId())) {
						tempPlayer.SendMsg(Command.MSG_GROUP_COMPETITION_EVENTS_FINISHED, result.toByteString());
						GCompUtil.log("发送结束消息给客户端：{}", tempPlayer);
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
}
