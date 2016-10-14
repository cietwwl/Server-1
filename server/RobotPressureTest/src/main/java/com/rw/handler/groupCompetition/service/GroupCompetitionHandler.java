package com.rw.handler.groupCompetition.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.groupCompetition.data.onlinemember.GCompOnlineMember;
import com.rw.handler.groupCompetition.util.GCompUtil;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.MsgGMResponse;
import com.rwproto.GMServiceProtos.eGMResultType;
import com.rwproto.GMServiceProtos.eGMType;
import com.rwproto.GroupCompetitionProto.CommonGetDataReqMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GroupCompetitionProto.TeamMemberRequest;
import com.rwproto.GroupCompetitionProto.TeamRequest;
import com.rwproto.GroupCompetitionProto.TeamStatusRequest;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupCompetitionHandler {

	public static final int[] quizCountArr = {100000, 200000, 500000};
	
	private static GroupCompetitionHandler handler = new GroupCompetitionHandler();

	public static GroupCompetitionHandler getHandler() {
		return handler;
	}
	
	private List<String> groupNames = new ArrayList<String>(); // 可選的幫派名字
	private String leaderGroupName;
	private final Random random = new Random();
	private int checkTimesToOpen = 20; // 检查次数
	
	private GroupCompetitionHandler() {
		groupNames.add("亞洲");
		groupNames.add("北美洲");
		groupNames.add("大洋洲");
		groupNames.add("歐盟");
		groupNames.add("北歐理事會");
		groupNames.add("中歐代表");
		groupNames.add("石油輸出國");
		groupNames.add("國際貨幣基金");
		leaderGroupName = groupNames.get(4);
	}
	
	private boolean sendTeamRequestCommand(Client client, GCRequestType type, List<String> heroIds, MsgReciver msgReceiver) {
		TeamRequest.Builder builder = TeamRequest.newBuilder();
		builder.setReqType(type);
		if (heroIds != null && heroIds.size() > 0) {
			builder.addAllHeroId(heroIds);
		}
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_TEAM_REQ, builder.build().toByteString(), msgReceiver);
	}
	
	private boolean sendGMCommand(Client client, String content) {
		MsgGMRequest.Builder msgGMRequestBuilder = MsgGMRequest.newBuilder();
		msgGMRequestBuilder.setGMType(eGMType.GM_COMMAND);
		msgGMRequestBuilder.setContent(content);
		return client.getMsgHandler().sendMsg(Command.MSG_GM, msgGMRequestBuilder.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_GM;
			}

			@Override
			public boolean execute(Client client, Response response) {
				try {
					MsgGMResponse rsp = MsgGMResponse.parseFrom(response.getSerializedContent());
					return rsp.getEGMResultType() == eGMResultType.SUCCESS;
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("解析GM响应出错！");
				}
				return false;
			}
		});
	}
	
	private void sendGroupAction(Client client) {
		String groupId = client.getUserGroupDataHolder().getUserGroupData().getGroupId();
		if (groupId == null || groupId.length() == 0) {
			this.sendGMCommand(client, "* gcompgroupaction " + groupNames.get(random.nextInt(groupNames.size())));
		}
	}
	
	private void sendGCompCmd(Client client) {
		// 发送帮战的指令
		String groupName;
		if ((groupName = client.getUserGroupDataHolder().getUserGroupData().getGroupName()) != null && groupName.equals(leaderGroupName)) {
			// 特定的帮派会长发送指令
			if (this.sendGMCommand(client, "* gCompCheckIfLeader " + groupName)) { // 向服务器查询是否会长
				if (this.sendGMCommand(client, "* gCompCheckTimes " + this.checkTimesToOpen)) {
					int maxRuntTime = 5;
					do {
						this.sendGMCommand(client, "* mgcs 1");
						maxRuntTime--;
						try {
							TimeUnit.SECONDS.sleep(2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} while (!client.getGCompBaseInfoHolder().isEventsStart() && maxRuntTime > 0);
				}
			}
		}
	}
	
	private void getMatchData(Client client) {
		CommonGetDataReqMsg.Builder builder = CommonGetDataReqMsg.newBuilder();
		builder.setReqType(GCRequestType.GetMatchView);
		client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_GET_DATA, builder.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_COMPETITION_GET_DATA;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				return false;
			}
		});
	}
	
	public boolean testGroupCompetition(Client client) {
		this.sendGroupAction(client);
		if (client.getGCompBaseInfoHolder().isEventsStart()) {
			// 帮战阶段
			if (client.getGCompEventsDataHolder().isNull()) {
				this.getMatchData(client);
			} else if (client.getGCompOnlinememberHolder().getSizeOfOnlineMember() == 0) {
				if (GroupCompSameSceneHandler.getHandler().enterPrepareArea(client)) {
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (Exception e) {
						RobotLog.fail("", e);
					}
					GroupCompSameSceneHandler.getHandler().inPrepareArea(client);
				}
			} else {
				GroupCompSameSceneHandler.getHandler().informPreparePosition(client);
			}
		} else {
			this.sendGCompCmd(client);
		}
		return true;
	}
	
	/**
	 * 
	 * 发送准备消息
	 * 
	 * @param client
	 * @return
	 */
	boolean sendSetReadyMsg(Client client) {
		TeamStatusRequest.Builder builder = TeamStatusRequest.newBuilder();
		builder.setReqType(GCRequestType.SetTeamReady);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_TEAM_STATUS_REQ, builder.build().toByteString(), new GCompSetReadyMsgReceiver());
	}
	
	/**
	 * 
	 * 发送匹配请求
	 * 
	 * @param client
	 * @return
	 */
	public boolean sendStartMatching(Client client) {
		TeamStatusRequest.Builder builder = TeamStatusRequest.newBuilder();
		builder.setReqType(GCRequestType.StartMatching);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_TEAM_STATUS_REQ, builder.build().toByteString(), new GCompStartMatchingMsgReceiver());
	}
	
	/**
	 * 
	 * 帮派争霸：创建队伍
	 * 
	 * @param client
	 * @return
	 */
	public boolean createGCompTeam(Client client) {
		List<String> heroIds = GCompUtil.getTeamHeroIds(client);
		return this.sendTeamRequestCommand(client, GCRequestType.CreateTeam, heroIds, new GCompCreateTeamMsgReceiver());
	}
	
	/**
	 * 
	 * 请求随机匹配
	 * 
	 * @param client
	 * @return
	 */
	public boolean requestRandomMatching(Client client) {
		return this.sendTeamRequestCommand(client, GCRequestType.StartRandomMatching, null, new GCompRandomMatchingMsgReceiver());
	}
	
	/**
	 * 
	 * 请求个人匹配
	 * 
	 * @param client
	 * @return
	 */
	public boolean requestPersonalMatching(Client client) {
		return this.sendTeamRequestCommand(client, GCRequestType.PersonalMatching, null, new GCompPersonalMatchingMsgReceiver());
	}
	
	/**
	 * 
	 * 请求邀请成员
	 * 
	 * @param client
	 * @return
	 */
	public boolean requestInviteMember(Client client) {
		if (client.getGCompOnlinememberHolder().getSizeOfOnlineMember() > 1) {
			GCompOnlineMember target = null;
			for(int i = 0; i < 10; i++) {
				target = client.getGCompOnlinememberHolder().getRandomOnlineMember();
				if (!target.getUserId().equals(client.getUserId())) {
					break;
				} else {
					target = null;
				}
			}
			if (target != null) {
				TeamMemberRequest.Builder builder = TeamMemberRequest.newBuilder();
				builder.setReqType(GCRequestType.InviteMember);
				builder.setTargetUserId(target.getUserId());
				client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_TEAM_MEMBER_REQ, builder.build().toByteString(), new GCompInviteMemberMsgReceiver());
			} else {
				RobotLog.info("GroupCompetitionHandler#requestInviteMember，找不到合适的邀请对象！");
				return false;
			}
		} else {
			RobotLog.info("GroupCompetitionHandler#requestInviteMember，没有在线成员！");
		}
		return true;
	}
}