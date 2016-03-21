package com.rw.handler.group;

import java.util.List;
import java.util.Random;

import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.group.data.UserGroupData;
import com.rw.handler.group.msg.GroupPersonalMsgReceiver;
import com.rwproto.GroupCommonProto.GroupRecommentType;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.GroupPersonalProto.ApplyJoinGroupReqMsg;
import com.rwproto.GroupPersonalProto.FindGroupReqMsg;
import com.rwproto.GroupPersonalProto.GroupDonateReqMsg;
import com.rwproto.GroupPersonalProto.GroupPersonalCommonReqMsg;
import com.rwproto.GroupPersonalProto.GroupRecommentReqMsg;
import com.rwproto.GroupPersonalProto.GroupSimpleInfo;
import com.rwproto.GroupPersonalProto.TransferGroupLeaderPostReqMsg;
import com.rwproto.MsgDef.Command;

/*
 * @author HC
 * @date 2016年3月15日 下午5:18:09
 * @Description 帮派个人处理类
 */
public class GroupPersonalHandler {
	private static final Random r = new Random();
	private static GroupPersonalHandler handler = new GroupPersonalHandler();

	public static GroupPersonalHandler getHandler() {
		return handler;
	}

	private static final Command command = Command.MSG_GROUP_PERSONAL;//
	private static final String functionName = "帮派个人请求";

	private GroupPersonalHandler() {
	}

	/**
	 * 获取推荐帮派数据
	 * 
	 * @param client
	 * @return
	 */
	public boolean getRecommendGroup(Client client) {
		GroupPersonalCommonReqMsg.Builder commonReq = GroupPersonalCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.GROUP_RECOMMENT_TYPE);

		GroupRecommentReqMsg.Builder req = GroupRecommentReqMsg.newBuilder();
		boolean b = r.nextBoolean();
		if (b) {
			req.setRecommentType(GroupRecommentType.RANK_RECOMMENT);
		} else {
			req.setRecommentType(GroupRecommentType.RANDOM_RECOMMENT);
		}

		commonReq.setGroupRecommentReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("推荐帮派"));
	}

	/**
	 * 获取帮派数据
	 * 
	 * @param client
	 * @return
	 */
	public boolean getGroupInfo(Client client) {
		GroupPersonalCommonReqMsg.Builder commonReq = GroupPersonalCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.GET_GROUP_INFO_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("获取帮派数据"));
	}

	/**
	 * 获取帮派排行榜数据
	 * 
	 * @param client
	 * @return
	 */
	public boolean getGroupRankInfo(Client client) {
		GroupPersonalCommonReqMsg.Builder commonReq = GroupPersonalCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.GET_GROUP_RANK_INFO_TYPE);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("获取帮派排行榜"));
	}

	/**
	 * 查找帮派数据
	 * 
	 * @param client
	 * @param groupId
	 * @return
	 */
	public boolean fingGroup(Client client, String groupId) {
		GroupPersonalCommonReqMsg.Builder commonReq = GroupPersonalCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.FIND_GROUP_TYPE);

		FindGroupReqMsg.Builder req = FindGroupReqMsg.newBuilder();
		req.setGroupId(groupId);

		commonReq.setFindGroupReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("获取帮派排行榜"));
	}

	/**
	 * 申请加入帮派
	 * 
	 * @param client
	 * @return
	 */
	public boolean applyJoinGroup(Client client, String groupId) {
		UserGroupData userGroupData = client.getUserGroupDataHolder().getUserGroupData();
		String hasGroup = userGroupData.getGroupId();
		if (hasGroup != null && !hasGroup.isEmpty()) {
			RobotLog.info("客户端记录到用户用帮派，不用再申请");
			return false;
		}

		if (groupId == null || groupId.isEmpty()) {
			List<GroupSimpleInfo> simpleInfoList = client.getGroupCacheData().getSimpleInfoList();
			if (simpleInfoList == null || simpleInfoList.isEmpty()) {
				RobotLog.info("申请加入帮派没有找到随机的帮派数据");
				return false;
			}
			int index = r.nextInt(simpleInfoList.size());
			groupId = simpleInfoList.get(index).getGroupId();
		}

		GroupPersonalCommonReqMsg.Builder commonReq = GroupPersonalCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.APPLY_JOIN_GROUP_TYPE);

		ApplyJoinGroupReqMsg.Builder req = ApplyJoinGroupReqMsg.newBuilder();
		req.setGroupId(groupId);

		commonReq.setApplyJoinGroupReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("申请加入帮派"));
	}

	private static final int[] donateId = new int[] { 1, 2, 3, 4 };

	/**
	 * 帮派捐献
	 * 
	 * @param client
	 * @return
	 */
	public boolean groupDonate(Client client) {
		GroupPersonalCommonReqMsg.Builder commonReq = GroupPersonalCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.GROUP_DONATE_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		GroupDonateReqMsg.Builder req = GroupDonateReqMsg.newBuilder();
		req.setDonateId(donateId[r.nextInt(donateId.length)]);

		commonReq.setGroupDonateReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("帮派捐献"));
	}

	/**
	 * 转让帮主
	 * 
	 * @param client
	 * @return
	 */
	public boolean transferGroupLeader(Client client) {
		// 随机一个帮派成员
		String memberId = client.getNormalMemberHolder().getRandomMemberId(r, r.nextBoolean());

		GroupPersonalCommonReqMsg.Builder commonReq = GroupPersonalCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.TRANSFER_LEADER_POST_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		TransferGroupLeaderPostReqMsg.Builder req = TransferGroupLeaderPostReqMsg.newBuilder();
		req.setMemberId(memberId);

		commonReq.setTransferLeaderPostReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("转让帮主"));
	}

	/**
	 * 退出帮派
	 * 
	 * @param client
	 * @return
	 */
	public boolean quitGroup(Client client) {
		GroupPersonalCommonReqMsg.Builder commonReq = GroupPersonalCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.QUIT_GROUP_TYPE);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("退出帮派"));
	}

	/**
	 * 获取MsgReciver
	 * 
	 * @param protoType
	 * @return
	 */
	private MsgReciver getMsgReciver(String protoType) {
		return new GroupPersonalMsgReceiver(command, functionName, protoType);
	}
}