package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;

import com.bm.group.GroupBM;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.MainMsgProtos.EMsgType;
import com.rwproto.MsgDef.Command;

public class GroupCompetitionBroadcastCenter {

	private static GroupCompetitionBroadcastCenter _instance = new GroupCompetitionBroadcastCenter();

	private final GroupCompetitionBroadcastTask _task = new GroupCompetitionBroadcastTask();

	public static GroupCompetitionBroadcastCenter getInstance() {
		return _instance;
	}

	protected GroupCompetitionBroadcastCenter() {
	}

	public void onEventsStart(List<String> relativeGroupIds) {
		_task._on = true;
		_task._broadcastQueue.clear();
		FSGameTimerMgr.getInstance().submitSecondTask(_task, GCompCommonConfig.getBroadcastIntervalSeconds());
		FSGameTimerMgr.getInstance().submitSecondTask(new GroupCompetitionStartBroadcastTask(relativeGroupIds), 1);
	}

	public void onEventsEnd() {
		_task._on = false;
	}

	public void addBroadcastMsg(Integer pmdId, List<String> arr) {
		GCompUtil.log("添加一条广播消息:{}, {}", pmdId, arr);
		_task._broadcastQueue.add(Pair.CreateReadonly(pmdId, arr));
	}

	private static class GroupCompetitionBroadcastTask implements IGameTimerTask {

		private boolean _on = false;

		private Queue<IReadOnlyPair<Integer, List<String>>> _broadcastQueue = new ConcurrentLinkedQueue<IReadOnlyPair<Integer, List<String>>>();

		@Override
		public String getName() {
			return "GroupCompetitionBroadcastTask";
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			if (_broadcastQueue.size() > 0) {
				List<IReadOnlyPair<Integer, List<String>>> copy = new ArrayList<IReadOnlyPair<Integer, List<String>>>(_broadcastQueue);
				_broadcastQueue.clear();
				for (IReadOnlyPair<Integer, List<String>> msg : copy) {
					MainMsgHandler.getInstance().sendMainCityMsg(msg.getT1().intValue(), EMsgType.GroupCompetitionMsg, msg.getT2());
				}
			}
			return null;
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

	private static class GroupCompetitionStartBroadcastTask implements IGameTimerTask {

		private List<String> groupIds;

		public GroupCompetitionStartBroadcastTask(List<String> groupIds) {
			this.groupIds = new ArrayList<String>(groupIds);
		}

		@Override
		public String getName() {
			return "GroupCompetitionStartBroadcastTask";
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			Group group;
			List<String> userIds = new ArrayList<String>();
			for (String groupId : groupIds) {
				group = GroupBM.getInstance().get(groupId);
				if (group != null) {
					List<? extends GroupMemberDataIF> memberSortList = group.getGroupMemberMgr().getMemberSortList(null);
					if (memberSortList.size() > 0) {
						String userId;
						for (int i = 0, size = memberSortList.size(); i < size; i++) {
							userId = memberSortList.get(i).getUserId();
							if (PlayerMgr.getInstance().isOnline(userId)) {
								userIds.add(userId);
							}
						}
					}
				}
			}
			if (userIds.size() > 0) {
				for (int i = 0, size = userIds.size(); i < size; i++) {
					Player player = PlayerMgr.getInstance().find(userIds.get(i));
					if (player != null) {
						System.out.println("发送消息给：" + player);
						player.SendMsg(Command.MSG_GROUP_COMPETITION_EVENTS_START, ByteString.EMPTY);
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
			return false;
		}

		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}

	}
}
