package com.rw.handler.groupCompetition.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rw.handler.groupCompetition.data.baseinfo.GCompBaseInfoHolder;
import com.rw.handler.groupCompetition.data.battle.GCompMatchBattleSynDataHolder;
import com.rw.handler.groupCompetition.data.events.GCompEventsDataHolder;
import com.rw.handler.groupCompetition.data.onlinemember.GCompOnlineMember;
import com.rw.handler.groupCompetition.data.onlinemember.GCompOnlineMemberHolder;
import com.rw.handler.groupCompetition.data.prepare.SameSceneSynDataHolder;
import com.rw.handler.groupCompetition.data.team.GCompTeam;
import com.rw.handler.groupCompetition.data.team.GCompTeamHolder;
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

public class GroupCompetitionHandler implements RandomMethodIF{
	
	private static final long inviteTimeoutMillis = 10000;
	
	public static final long maxTeamExistsTimemillis = 30000; // 队伍组不到人的最大存活时间
	
	public static final long maxPersonMatchingIntervalMillis = 30000;
	
	/**
	 * 帮派争霸跑的间隔
	 */
	public static final long runIntervalMillis = 2000;
	
	private static GroupCompetitionHandler handler = new GroupCompetitionHandler();

	public static GroupCompetitionHandler getHandler() {
		return handler;
	}
	
	private List<String> groupNames = new ArrayList<String>(); // 可選的幫派名字
	private String leaderGroupName;
	private final Random random = new Random();
	private int checkTimesToOpen = 20; // 检查次数
	
	private GroupCompetitionHandler() {
//		groupNames.add("12301");
//		groupNames.add("12302");
//		groupNames.add("12309");
//		groupNames.add("12311");
//		groupNames.add("12314");
//		groupNames.add("12317");
//		groupNames.add("12320");
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
//			RobotLog.info("没有加入帮派，尝试加入或创建帮派，userId：" + client.getUserId());
			GCompBaseInfoHolder holder = client.getGCompBaseInfoHolder();
			if(holder.getLastRequestGroupTime() < System.currentTimeMillis()) {
				holder.setLastRequestGroupTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(random.nextInt(15) + 15));
				this.sendGMCommand(client, "* gcompgroupaction " + groupNames.get(random.nextInt(groupNames.size())));
			}
		}
	}
	
	private void sendGCompCmd(Client client) {
		// 发送帮战的指令
		String groupName;
		if ((groupName = client.getUserGroupDataHolder().getUserGroupData().getGroupName()) != null && groupName.equals(leaderGroupName)) {
			// 特定的帮派会长发送指令
			RobotLog.info("目标帮派发送测试指令，userId：" + client.getUserId());
			if (this.sendGMCommand(client, "* gCompCheckIfLeader " + groupName)) { // 向服务器查询是否会长
				if (this.sendGMCommand(client, "* gCompCheckTimes " + this.checkTimesToOpen)) {
//					int maxRuntTime = 5;
//					do {
//						this.sendGMCommand(client, "* mgcs 1");
//						maxRuntTime--;
//						try {
//							TimeUnit.SECONDS.sleep(2);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					} while (!client.getGCompBaseInfoHolder().isEventsStart() && maxRuntTime > 0);
					this.sendGMCommand(client, "* mgcs 1");
					RobotLog.info("发送测试开启帮战的GM指令，userId：" + client.getUserId());
				}
			}
		}
	}
	
	private boolean getMatchData(Client client) {
		RobotLog.info("获取对阵信息，userId：" + client.getUserId());
		CommonGetDataReqMsg.Builder builder = CommonGetDataReqMsg.newBuilder();
		builder.setReqType(GCRequestType.GetMatchView);
		if (client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_GET_DATA, builder.build().toByteString(), null)) {
//			try {
//				TimeUnit.SECONDS.sleep(2);
//			} catch (InterruptedException e) {
//				RobotLog.fail("发送获取对阵列表的消息成功，但是等待抛了异常！", e);
//			}
//			return !client.getGCompEventsDataHolder().isNull();
			return true;
		} else {
			RobotLog.fail("发送获取对阵列表的消息不成功！");
			return false;
		}
	}
	
	private boolean requestLeaveTeam(Client client) {
		TeamStatusRequest.Builder builder = TeamStatusRequest.newBuilder();
		builder.setReqType(GCRequestType.LeaveTeam);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_TEAM_STATUS_REQ, builder.build().toByteString(), new GCompLeaveTeamMsgReceiver());
	}
	
	private boolean processTeamEvents(Client client) {
		GCompMatchBattleSynDataHolder matchDataHolder = client.getgCompMatchBattleSynDataHolder();
		if (matchDataHolder.isInitBattle() || matchDataHolder.isRandomMatching()) {
//			RobotLog.info("等待" + (matchDataHolder.isInitBattle() ? "战斗" : "随机匹配") + "中，userId：" + client.getUserId());
			return true;
		}
		GCompTeam team;
		GCompTeamHolder teamHolder = client.getGCompTeamHolder();
		if ((team = teamHolder.getTeam()) != null) {
			if (teamHolder.getTeamWaitingTimeout() < System.currentTimeMillis()) {
				// 队伍超时
				RobotLog.info("队伍超过30秒没有组齐人，请求解散队伍，userId:" + client.getUserId());
				return requestLeaveTeam(client);
			} else {
				if (team.getLeaderId().equals(client.getUserId())) {
					if (team.getMemberSize() < 3) {
//						RobotLog.info("尝试邀请组队，userId:" + client.getUserId());
						return this.requestInviteMember(client);
					} else {
						RobotLog.info("请求队伍匹配，userId:" + client.getUserId());
						return this.sendStartMatching(client);
					}
				} else {
//					RobotLog.info("发送准备状态，userId:" + client.getUserId());
					if (teamHolder.getLastSendReadyTime() < System.currentTimeMillis()) {
						teamHolder.setLastSendReadyTime(System.currentTimeMillis() + 10);
						return this.sendSetReadyMsg(client);
					} else {
						return true;
					}
				}
			}
		} else {
			if(teamHolder.getTeamWaitingTimeout() > 0 || random.nextBoolean()) {
				teamHolder.setTeamWaitingTimeout(0);
				matchDataHolder.setRandomMatching(true);
				RobotLog.info("请求随机匹配，userId:" + client.getUserId());
				return requestRandomMatching(client);
			} else {
				RobotLog.info("请求创建队伍，userId:" + client.getUserId());
				return createGCompTeam(client);
			}
		}
	}
	
	// 参与个人战
	private boolean processPersonEvents(Client client) {
		if (client.getgCompMatchBattleSynDataHolder().isInitBattle()) {
//			RobotLog.info("等待个人战斗中，userId：" + client.getUserId());
			return true;
		}
		GCompTeamHolder teamHolder = client.getGCompTeamHolder();
		if (teamHolder.getPersonalMatchingTimeOut() < System.currentTimeMillis()) {
			RobotLog.info("请求个人匹配，userId：" + client.getUserId());
			if (this.requestPersonalMatching(client)) {
				RobotLog.info("请求个人匹配成功！userId：" + client.getUserId());
				teamHolder.setPersonalMatchingTimeOut(System.currentTimeMillis() + maxPersonMatchingIntervalMillis);
				return true;
			} else {
				RobotLog.fail("请求个人匹配失败！userId：" + client.getUserId());
				return false;
			}
		} else {
			return true;
		}
	}
	
	// 模拟进入备战区
	private boolean processEnterPrepareArea(Client client) {
		if (GroupCompSameSceneHandler.getHandler().enterPrepareArea(client)) {
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (Exception e) {
				RobotLog.fail("进入备战区成功，但是暂停被打断！", e);
			}
			return GroupCompSameSceneHandler.getHandler().inPrepareArea(client);
		} else {
			RobotLog.fail("进入备战区不成功！userId:" + client.getUserId());
			return false;
		}
	}
	
	// 帮派战已经开始，并且已经在备战区
	private boolean processEventsBehavior(Client client) {
		boolean result = true;
		if (!client.getgCompMatchBattleSynDataHolder().isInitBattle()) {
			RobotLog.info("随机移动，userId：" + client.getUserId());
			SameSceneSynDataHolder sameSceneSynDataHolder = client.getSameSceneSynDataHolder();
			if (sameSceneSynDataHolder.getLastMoveTime() < System.currentTimeMillis()) {
				result = GroupCompSameSceneHandler.getHandler().informPreparePosition(client);
				sameSceneSynDataHolder.setLastMoveTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(random.nextInt(5) + 5));
			}
		}
		if (result) {
			switch (client.getGCompBaseInfoHolder().getGCompBaseInfo().getEventStatus()) {
			case TEAM_EVENTS:
				result &= processTeamEvents(client);
				break;
			case PERSONAL_EVENTS:
				result &= processPersonEvents(client);
				break;
			default:
				break;
			}
		} else {
			RobotLog.fail("GroupCompSameSceneHandler#informPreparePosition失败！userId：" + client.getUserId());
		}
		return result;
	}
	
	private boolean processBehaviorWhenEventsNotStart(Client client) {
		if (client.getGCompOnlinememberHolder().getSizeOfOnlineMember() > 0) {
			if (GroupCompSameSceneHandler.getHandler().leavePreareArea(client)) {
				client.getGCompOnlinememberHolder().clearOnlineMembers();
			}
			client.getGCompEventsDataHolder().clear();
//			RobotLog.info("帮战结束，退出备战区，userId:" + client.getUserId());
			return true;
		} else {
//			RobotLog.info("帮战未开始，尝试发送GM指令，userId:" + client.getUserId());
			this.sendGCompCmd(client); // 尝试发送帮战开始的指令
			return true;
		}
	}
	
	public boolean testGroupCompetition(Client client) {
		if(System.currentTimeMillis() - client.getGCompBaseInfoHolder().getLastRunGCompTime() < runIntervalMillis) {
			return true;
		}
		client.getGCompBaseInfoHolder().setLastRunGCompTime(System.currentTimeMillis());
		this.sendGroupAction(client); // 尝试创建或加入帮派
		if (client.getGCompBaseInfoHolder().isEventsStart()) {
			// 帮战阶段
			GCompEventsDataHolder eventsDaaHolder = client.getGCompEventsDataHolder();
			if (client.getGCompEventsDataHolder().isNull()) {
				// 没有matchView数据
				if (eventsDaaHolder.getWaitingTimeout() == 0) {
					// 随机等待一下，防止扎堆请求数据
					eventsDaaHolder.setWaitingTimeout(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(random.nextInt(15) + 15));
					return true;
				} else if (eventsDaaHolder.getWaitingTimeout() < System.currentTimeMillis()) {
					// 等待超时
					return this.getMatchData(client);
				} else {
					// 等待未超时
					return true;
				}
			} else if (client.getGCompOnlinememberHolder().getSizeOfOnlineMember() == 0) { // 未进入备战区
				// 进入备战区
				if (client.getGCompOnlinememberHolder().getLastTryEnterPrepareTime() < System.currentTimeMillis()) {
					client.getGCompOnlinememberHolder().setLastTryEnterPrepareTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(random.nextInt(15) + 15)); // 如果进入不了，等30秒再进入
					return this.processEnterPrepareArea(client);
				} else {
					return true;
				}
			} else { // 已经在备战区
				return this.processEventsBehavior(client);
			}
		} else {
			return this.processBehaviorWhenEventsNotStart(client);
		}
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
		List<String> heroIds = GCompUtil.getTeamHeroIds(client);
		return this.sendTeamRequestCommand(client, GCRequestType.StartRandomMatching, heroIds, new GCompRandomMatchingMsgReceiver());
	}
	
	/**
	 * 
	 * 请求个人匹配
	 * 
	 * @param client
	 * @return
	 */
	public boolean requestPersonalMatching(Client client) {
		List<String> heroIds = GCompUtil.getTeamHeroIds(client);
		return this.sendTeamRequestCommand(client, GCRequestType.PersonalMatching, heroIds, new GCompPersonalMatchingMsgReceiver());
	}
	
	/**
	 * 
	 * 请求邀请成员
	 * 
	 * @param client
	 * @return
	 */
	public boolean requestInviteMember(Client client) {
		GCompOnlineMemberHolder onlineMemberHolder = client.getGCompOnlinememberHolder();
		if (client.getGCompOnlinememberHolder().getSizeOfOnlineMember() > 1) {
			GCompTeam team = client.getGCompTeamHolder().getTeam();
			GCompOnlineMember target = null;
			for(int i = 0; i < 10; i++) {
				// 尝试10次
				target = client.getGCompOnlinememberHolder().getRandomOnlineMember();
				if (!target.getUserId().equals(client.getUserId()) && team.getTeamMember(target.getUserId()) == null) {
					Long time = onlineMemberHolder.getInviteTimeout(target.getUserId());
					if (time == null || time < System.currentTimeMillis()) {
						onlineMemberHolder.setInviteTimeout(target.getUserId(), System.currentTimeMillis() + inviteTimeoutMillis);
						break;
					} else  {
						target = null;
					}
				} else {
					target = null;
				}
			}
			if (target != null) {
				TeamMemberRequest.Builder builder = TeamMemberRequest.newBuilder();
				builder.setReqType(GCRequestType.InviteMember);
				builder.setTargetUserId(target.getUserId());
				return client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_TEAM_MEMBER_REQ, builder.build().toByteString(), new GCompInviteMemberMsgReceiver());
			} else {
				RobotLog.info("GroupCompetitionHandler#requestInviteMember，找不到合适的邀请对象！");
				return true;
			}
		} else {
			RobotLog.info("GroupCompetitionHandler#requestInviteMember，没有在线成员！");
		}
		return true;
	}

	@Override
	public boolean executeMethod(Client client) {
		return testGroupCompetition(client);
	}
}