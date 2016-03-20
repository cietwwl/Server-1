package com.rw.handler.group;

import java.util.Random;

import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.handler.group.msg.GroupMemberMsgReceiver;
import com.rwproto.GroupCommonProto.GroupPost;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.GroupMemberMgrProto.GroupCancelNominatePostReqMsg;
import com.rwproto.GroupMemberMgrProto.GroupEmailForAllReqMsg;
import com.rwproto.GroupMemberMgrProto.GroupMemberMgrCommonReqMsg;
import com.rwproto.GroupMemberMgrProto.GroupMemberReceiveReqMsg;
import com.rwproto.GroupMemberMgrProto.GroupNominatePostReqMsg;
import com.rwproto.GroupMemberMgrProto.KickMemberReqMsg;
import com.rwproto.MsgDef.Command;

/*
 * @author HC
 * @date 2016年3月15日 下午5:17:56
 * @Description 帮派成员处理类
 */
public class GroupMemberHandler {

	private static final Random r = new Random();
	private static GroupMemberHandler handler = new GroupMemberHandler();

	public static GroupMemberHandler getHandler() {
		return handler;
	}

	private static final Command command = Command.MSG_GROUP_MEMBER_MANAGER;//
	private static final String functionName = "帮派成员管理";

	private GroupMemberHandler() {
	}

	/**
	 * 获取申请列表
	 * 
	 * @param client
	 * @return
	 */
	public boolean getApplyMemberList(Client client) {
		GroupMemberMgrCommonReqMsg.Builder commonReq = GroupMemberMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.GET_APPLY_MEMBER_LIST_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("获取申请列表"));
	}

	/**
	 * 帮派成员接受或者拒绝
	 * 
	 * @param client
	 * @return
	 */
	public boolean memberReceive(Client client) {
		GroupMemberMgrCommonReqMsg.Builder commonReq = GroupMemberMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.GROUP_MEMBER_RECEIVE_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		GroupMemberReceiveReqMsg.Builder req = GroupMemberReceiveReqMsg.newBuilder();
		req.setIsReceive(r.nextBoolean());// 接受或者拒绝
		boolean b = r.nextBoolean();
		if (!b) {// 接受或者拒绝个人
			req.setApplyMemberId(client.getApplyMemberHolder().getRandomMemberId(r));
		}

		commonReq.setGroupMemberReceiveReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("接受或者拒绝申请"));
	}

	private static final GroupPost[] post = new GroupPost[] { GroupPost.ASSISTANT_LEADER, GroupPost.LEADER, GroupPost.MEMBER, GroupPost.OFFICEHOLDER };

	/**
	 * 成员任命
	 * 
	 * @param client
	 * @return
	 */
	public boolean memberNominate(Client client) {
		GroupMemberMgrCommonReqMsg.Builder commonReq = GroupMemberMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.NOMINATE_POST_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		GroupNominatePostReqMsg.Builder req = GroupNominatePostReqMsg.newBuilder();
		req.setMemberId(client.getNormalMemberHolder().getRandomMemberId(r));
		req.setPost(post[r.nextInt(post.length)]);

		commonReq.setGroupNominatePostReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("任命官员"));
	}

	/**
	 * 取消任命
	 * 
	 * @param client
	 * @return
	 */
	public boolean memberCancelNominate(Client client) {
		GroupMemberMgrCommonReqMsg.Builder commonReq = GroupMemberMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.CANCEL_NOMINATE_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		GroupCancelNominatePostReqMsg.Builder req = GroupCancelNominatePostReqMsg.newBuilder();
		req.setMemberId(client.getNormalMemberHolder().getRandomMemberId(r));

		commonReq.setGroupCancelNominatePostReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("取消任命"));
	}

	/**
	 * 全员邮件
	 * 
	 * @param client
	 * @return
	 */
	public boolean groupEmailForAll(Client client) {
		GroupMemberMgrCommonReqMsg.Builder commonReq = GroupMemberMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.GROUP_EMAIL_FOR_ALL_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		GroupEmailForAllReqMsg.Builder req = GroupEmailForAllReqMsg.newBuilder();
		req.setEmailTitle("哈哈哈哈哈压测邮件");
		req.setEmailContent("压测邮件啷哩个浪，啷哩个浪！啊呀游，嘿嘿嘿嘿hi额hi额hi额嘿嘿嘿！！！");

		commonReq.setGroupEmailForAllReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("全员邮件"));
	}

	/**
	 * 踢出成员
	 * 
	 * @param client
	 * @return
	 */
	public boolean kickMember(Client client) {
		GroupMemberMgrCommonReqMsg.Builder commonReq = GroupMemberMgrCommonReqMsg.newBuilder();
		commonReq.setReqType(RequestType.KICK_MEMBER_TYPE);
		String groupVersion = client.getGroupVersion();
		if (groupVersion != null && !groupVersion.isEmpty()) {
			commonReq.setVersion(groupVersion);
		}

		KickMemberReqMsg.Builder req = KickMemberReqMsg.newBuilder();
		req.setMemberId(client.getNormalMemberHolder().getRandomMemberId(r));

		commonReq.setKickMemberReq(req);

		return client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), getMsgReciver("踢出帮派"));
	}

	/**
	 * 获取MsgReciver
	 * 
	 * @param protoType
	 * @return
	 */
	private MsgReciver getMsgReciver(String protoType) {
		return new GroupMemberMsgReceiver(command, functionName, protoType);
	}
}