package com.rw.service.gm.groupcomp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.bm.group.GroupBM;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.group.GroupBaseManagerHandler;
import com.rw.service.group.GroupPersonalHandler;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.GroupBaseMgrProto.CreateGroupReqMsg;
import com.rwproto.GroupBaseMgrProto.GroupBaseMgrCommonRspMsg;
import com.rwproto.GroupPersonalProto.ApplyJoinGroupReqMsg;
import com.rwproto.GroupPersonalProto.GroupPersonalCommonRspMsg;

/**
 * 
 * 帮派争霸的GM指令处理
 * 
 * @author CHEN.P
 *
 */
public class GCGMHandler {

	private static GCGMHandler _instance = new GCGMHandler();

	public static GCGMHandler getHandler() {
		return _instance;
	}

	private final Map<String, AtomicBoolean> groupNameMap = new HashMap<String, AtomicBoolean>();

	private final AtomicInteger _checkTimes = new AtomicInteger();

	protected GCGMHandler() {
		groupNameMap.put("亞洲", new AtomicBoolean());
		groupNameMap.put("北美洲", new AtomicBoolean());
		groupNameMap.put("大洋洲", new AtomicBoolean());
		groupNameMap.put("歐盟", new AtomicBoolean());
		groupNameMap.put("北歐理事會", new AtomicBoolean());
		groupNameMap.put("中歐代表", new AtomicBoolean());
		groupNameMap.put("石油輸出國", new AtomicBoolean());
		groupNameMap.put("國際貨幣基金", new AtomicBoolean());
	}

	public boolean createGroup(String[] arrCommandContents, Player player) {
		String groupName = arrCommandContents[0];
		AtomicBoolean flag = groupNameMap.get(groupName);
		if (flag != null && flag.compareAndSet(false, true)) {
			CreateGroupReqMsg.Builder builder = CreateGroupReqMsg.newBuilder();
			builder.setGroupName(arrCommandContents[0]);
			builder.setIcon("1");
			ByteString bs = GroupBaseManagerHandler.getHandler().createGroupHandler(player, builder.build());
			try {
				GroupBaseMgrCommonRspMsg rsp = GroupBaseMgrCommonRspMsg.parseFrom(bs);
				if (rsp.getIsSuccess()) {
					GameWorldFactory.getGameWorld().asynExecute(new UpdateGroupSettingTask(groupName, player.getUserId()));
				}
				return rsp.getIsSuccess();
			} catch (InvalidProtocolBufferException e) {
				return false;
			}
		}
		return false;
	}

	public boolean joinGroup(String[] arrCommandContents, Player player) {
		String groupName = arrCommandContents[0];
		String groupId = GroupBM.getInstance().getGroupId(groupName);
		if (groupId != null && groupId.length() > 0) {
			ApplyJoinGroupReqMsg.Builder reqBuilder = ApplyJoinGroupReqMsg.newBuilder();
			reqBuilder.setGroupId(groupId);
			ByteString bs = GroupPersonalHandler.getHandler().applyJoinGroupHandler(player, reqBuilder.build());
			try {
				GroupPersonalCommonRspMsg rsp = GroupPersonalCommonRspMsg.parseFrom(bs);
				return rsp.getIsSuccess();
			} catch (InvalidProtocolBufferException e) {
				return false;
			}
		}
		return false;
	}

	public boolean checkIfLeader(String[] arrCommandContents, Player player) {
		String groupName = arrCommandContents[0];
		String groupId = GroupBM.getInstance().getGroupId(groupName);
		if (groupId != null && groupId.length() > 0) {
			Group group = GroupBM.getInstance().get(groupId);
			boolean result = group.getGroupMemberMgr().getGroupLeader().getUserId().equals(player.getUserId());
			return result;
		}
		return false;
	}

	public boolean isCheckTimesMatch(String[] arrCommandContents, Player player) {
		int checkTimes = Integer.parseInt(arrCommandContents[0]);
		if (_checkTimes.incrementAndGet() < checkTimes) {
			return false;
		} else {
			_checkTimes.set(0);
			return true;
		}
	}
}
