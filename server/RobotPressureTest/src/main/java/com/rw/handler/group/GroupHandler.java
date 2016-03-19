//package com.rw.handler.group;
//
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.rw.Client;
//import com.rw.PrintMsg;
//import com.rw.common.MsgLog;
//import com.rw.common.MsgReciver;
//import com.rwproto.GroupBaseMgrProto.CreateGroupReqMsg;
//import com.rwproto.GroupBaseMgrProto.CreateGroupRspMsg;
//import com.rwproto.GroupBaseMgrProto.GroupSettingReqMsg;
//import com.rwproto.GroupBaseMgrProto.ModifyAnnouncementReqMsg;
//import com.rwproto.GroupBaseMgrProto.ModifyGroupNameReqMsg;
//import com.rwproto.GroupCommonProto.GroupPost;
//import com.rwproto.GroupCommonProto.GroupRecommentType;
//import com.rwproto.GroupCommonProto.GroupValidateType;
//import com.rwproto.GroupCommonProto.RequestType;
//import com.rwproto.GroupMemberMgrProto.GroupCancelNominatePostReqMsg;
//import com.rwproto.GroupMemberMgrProto.GroupEmailForAllReqMsg;
//import com.rwproto.GroupMemberMgrProto.GroupMemberReceiveReqMsg;
//import com.rwproto.GroupMemberMgrProto.GroupNominatePostReqMsg;
//import com.rwproto.GroupMemberMgrProto.KickMemberReqMsg;
//import com.rwproto.GroupPersonalProto.ApplyJoinGroupReqMsg;
//import com.rwproto.GroupPersonalProto.FindGroupReqMsg;
//import com.rwproto.GroupPersonalProto.FindGroupRspMsg;
//import com.rwproto.GroupPersonalProto.GetGroupInfoRspMsg;
//import com.rwproto.GroupPersonalProto.GetGroupRankRspMsg;
//import com.rwproto.GroupPersonalProto.GroupDonateReqMsg;
//import com.rwproto.GroupPersonalProto.GroupDonateRspMsg;
//import com.rwproto.GroupPersonalProto.GroupRecommentReqMsg;
//import com.rwproto.GroupPersonalProto.GroupRecommentRspMsg;
//import com.rwproto.GroupPersonalProto.OpenDonateViewRspMsg;
//import com.rwproto.GroupPersonalProto.TransferGroupLeaderPostReqMsg;
//import com.rwproto.MsgDef.Command;
//import com.rwproto.ResponseProtos.Response;
//
///*
// * @author HC
// * @date 2016年1月28日 下午7:44:02
// * @Description 
// */
//public class GroupHandler {
//	private Client client;
//
//	public GroupHandler(Client client) {
//		this.client = client;
//	}
//
//	public boolean recommentGroup() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupRecommentReqMsg.Builder req = GroupRecommentReqMsg.newBuilder();
//		req.setRecommentType(GroupRecommentType.RANDOM_RECOMMENT);
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.GROUP_RECOMMENT_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					GroupRecommentRspMsg rsp = GroupRecommentRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean createGroup() {
//		if (client == null) {
//			return false;
//		}
//
//		CreateGroupReqMsg.Builder req = CreateGroupReqMsg.newBuilder();
//		req.setGroupName("HC的帮派");
//		req.setIcon("1");
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.CREATE_GROUP_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					CreateGroupRspMsg rsp = CreateGroupRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean getGroupInfo() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.GET_GROUP_INFO_TYPE);
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					GetGroupInfoRspMsg rsp = GetGroupInfoRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean findGroupIndo() {
//		if (client == null) {
//			return false;
//		}
//
//		FindGroupReqMsg.Builder req = FindGroupReqMsg.newBuilder();
//		req.setGroupId("100110001");
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.FIND_GROUP_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					FindGroupRspMsg rsp = FindGroupRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean modifyGroupAnnouncement() {
//		if (client == null) {
//			return false;
//		}
//
//		ModifyAnnouncementReqMsg.Builder req = ModifyAnnouncementReqMsg.newBuilder();
//		req.setAnnouncement("这是我测试的第二个公告了啊，看看行不行！");
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.MODIFY_ANNOUNCEMENT_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					ModifyAnnouncementRspMsg rsp = ModifyAnnouncementRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean modifyGroupName() {
//		if (client == null) {
//			return false;
//		}
//
//		ModifyGroupNameReqMsg.Builder req = ModifyGroupNameReqMsg.newBuilder();
//		req.setGroupName("再次修改名字");
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.MODIFY_GROUP_NAME_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean openDonateView() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.OPEN_DONATE_VIEW_TYPE);
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					OpenDonateViewRspMsg rsp = OpenDonateViewRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean donate() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupDonateReqMsg.Builder req = GroupDonateReqMsg.newBuilder();
//		req.setDonateId(3);
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.GROUP_DONATE_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					GroupDonateRspMsg rsp = GroupDonateRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean groupSetting() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupSettingReqMsg.Builder req = GroupSettingReqMsg.newBuilder();
//		req.setApplyLevel(20);
//		req.setDeclaration("帮派只加大牛");
//		req.setGroupIcon("2");
//		req.setValidateType(GroupValidateType.JOIN_REFUSED);
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.GROUP_SETTING_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean applyJoinGroup() {
//		if (client == null) {
//			return false;
//		}
//
//		ApplyJoinGroupReqMsg.Builder req = ApplyJoinGroupReqMsg.newBuilder();
//		req.setGroupId("100110001");
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.APPLY_JOIN_GROUP_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean memberReceive() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupMemberReceiveReqMsg.Builder req = GroupMemberReceiveReqMsg.newBuilder();
//		// req.setApplyMemberId("100100001430");
//		req.setIsReceive(true);
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.GROUP_MEMBER_RECEIVE_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					GroupRecommentRspMsg rsp = GroupRecommentRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean nominatePost() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupNominatePostReqMsg.Builder req = GroupNominatePostReqMsg.newBuilder();
//		req.setMemberId("100100001435");
//		req.setPost(GroupPost.ASSISTANT_LEADER);
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.NOMINATE_POST_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean cancelNominate() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCancelNominatePostReqMsg.Builder req = GroupCancelNominatePostReqMsg.newBuilder();
//		req.setMemberId("100100001435");
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.CANCEL_NOMINATE_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean transferLeader() {
//		if (client == null) {
//			return false;
//		}
//
//		TransferGroupLeaderPostReqMsg.Builder req = TransferGroupLeaderPostReqMsg.newBuilder();
//		req.setMemberId("100100001436");
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.TRANSFER_LEADER_POST_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean groupEmailAll() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupEmailForAllReqMsg.Builder req = GroupEmailForAllReqMsg.newBuilder();
//		req.setEmailTitle("得瑟标题");
//		req.setEmailContent("这是我的第一封全民邮件，请多多支持啊啊，哈哈哈哈哈哈哈！");
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.GROUP_EMAIL_FOR_ALL_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean quitGroup() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.QUIT_GROUP_TYPE);
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//		return true;
//	}
//
//	public boolean kickMember() {
//		if (client == null) {
//			return false;
//		}
//
//		KickMemberReqMsg.Builder req = KickMemberReqMsg.newBuilder();
//		req.setMemberId("100100001435");
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.KICK_MEMBER_TYPE);
//		commonReq.setReqBody(req.build().toByteString());
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean dismissGroup() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.DISMISS_THE_GROUP_TYPE);
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean cancelDismissGroup() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.CANCEL_DISMISS_THE_GROUP_TYPE);
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(null));
//
//		return true;
//	}
//
//	public boolean getApplyMemberList() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.GET_APPLY_MEMBER_LIST_TYPE);
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					GetGroupApplyMemberListRspMsg rsp = GetGroupApplyMemberListRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean getGroupRank() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.GET_GROUP_RANK_INFO_TYPE);
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					GetGroupRankRspMsg rsp = GetGroupRankRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean checkGroupData() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.CHECK_GROUP_DATA_TYPE);
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					CheckGroupDataRspMsg rsp = CheckGroupDataRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	public boolean getGroupLog() {
//		if (client == null) {
//			return false;
//		}
//
//		GroupCommonReqMsg.Builder commonReq = GroupCommonReqMsg.newBuilder();
//		commonReq.setReqType(RequestType.THE_LOG_OF_GROUP_TYPE);
//
//		client.getMsgHandler().sendMsg(Command.MSG_GROUP, commonReq.build().toByteString(), new Receiver(new PrintMsg<GroupCommonRspMsg>() {
//
//			@Override
//			public void print(GroupCommonRspMsg commonRsp) {
//				try {
//					GetLogOfGroupRspMsg rsp = GetLogOfGroupRspMsg.parseFrom(commonRsp.getRspBody());
//					System.err.println(rsp.toBuilder().build());
//				} catch (InvalidProtocolBufferException e) {
//					MsgLog.error("PrintMsg===================================", e);
//				}
//			}
//		}));
//
//		return true;
//	}
//
//	class Receiver implements MsgReciver {
//
//		private PrintMsg<GroupCommonRspMsg> msg;
//
//		public Receiver(PrintMsg<GroupCommonRspMsg> msg) {
//			this.msg = msg;
//		}
//
//		@Override
//		public Command getCmd() {
//			return Command.MSG_GROUP;
//		}
//
//		@SuppressWarnings("finally")
//		@Override
//		public boolean execute(Client client, Response response) {
//			try {
//				GroupCommonRspMsg commonRsp = GroupCommonRspMsg.parseFrom(response.getSerializedContent());
//				boolean isSuccess = commonRsp.getIsSuccess();
//				System.err.println(isSuccess);
//				System.err.println(commonRsp.getReqType());
//				System.err.println(commonRsp.getTipMsg());
//				if (!isSuccess) {
//					return false;
//				}
//
//				if (msg != null) {
//					msg.print(commonRsp);
//				} else {
//					System.err.println(commonRsp.toBuilder().build());
//				}
//			} catch (InvalidProtocolBufferException e) {
//				MsgLog.error("Execute===================================", e);
//			} finally {
//				return true;
//			}
//		}
//	}
//}