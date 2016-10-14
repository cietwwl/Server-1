package com.rw.handler.groupCompetition.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.groupCompetition.data.guess.GCQuizEventItem;
import com.rw.handler.groupCompetition.data.guess.GCQuizEventItemHolder;
import com.rw.handler.groupCompetition.data.onlinemember.GCompOnlineMember;
import com.rw.handler.groupCompetition.data.onlinemember.GCompOnlineMemberHolder;
import com.rw.handler.groupCompetition.data.team.GCompTeam;
import com.rw.handler.groupCompetition.data.team.GCompTeamHolder;
import com.rw.handler.groupCompetition.util.GCompUtil;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.MsgGMResponse;
import com.rwproto.GMServiceProtos.eGMResultType;
import com.rwproto.GMServiceProtos.eGMType;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GroupCompetitionProto.CommonGetDataReqMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.GroupCompetitionProto.ReqAllGuessInfo;
import com.rwproto.GroupCompetitionProto.ReqNewGuess;
import com.rwproto.GroupCompetitionProto.RspAllGuessInfo;
import com.rwproto.GroupCompetitionProto.RsqNewGuess;
import com.rwproto.GroupCompetitionProto.TeamMemberRequest;
import com.rwproto.GroupCompetitionProto.TeamRequest;
import com.rwproto.GroupCompetitionProto.TeamStatusRequest;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupCompetitionHandler {

	public static final int[] quizCountArr = {100000, 200000, 500000};
	
	private static final long inviteTimeoutMillis = 10000;
	
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
	

	public boolean groupCompQuiz(Client client){
		boolean result = getCanQuizMatch(client);
		if (!result) {
			RobotLog.fail("groupCompQuiz[send]争霸赛获取可竞猜的项目失败=" + result);
			return result;
		}
		return quizForCompetion(client);
	}
	
	/**
	 * 获取当前的可竞猜项目
	 * @param player
	 * @param gcRsp
	 */
	private boolean getCanQuizMatch(Client client) {
		ReqAllGuessInfo.Builder req = ReqAllGuessInfo.newBuilder();
		req.setReqType(GCRequestType.GetCanGuessMatch);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_QUIZ_SYN, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_COMPETITION_QUIZ_SYN;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					RspAllGuessInfo rsp = RspAllGuessInfo.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GroupCompetitionHandler[send] getCanQuizMatch转换响应消息为null");
						return false;
					}
					GCResultType result = rsp.getRstType();
					if (!result.equals(GFResultType.SUCCESS)) {
						RobotLog.info("GroupCompetitionHandler[send] getCanQuizMatch服务器返回不成功 ");
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupCompetitionHandler[send] getCanQuizMatch失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
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
	
	private void processTeamEvents(Client client) {
		GCompTeam team;
		if((team = client.getGCompTeamHolder().getTeam()) != null) {
			if (team.getLeaderId().equals(client.getUserId())) {
				if(team.getMemberSize() < 3) {
					this.requestInviteMember(client);
				} else {
					this.sendStartMatching(client);
				}
			} else {
				this.sendSetReadyMsg(client);
			}
		} else {
			this.createGCompTeam(client);
		}
	}
	
	private void processPersonEvents(Client client) {
		GCompTeamHolder teamHolder = client.getGCompTeamHolder();
		if (teamHolder.getPersonalMatchingTimeOut() < System.currentTimeMillis()) {
			if (this.requestPersonalMatching(client)) {
				teamHolder.setPersonalMatchingTimeOut(System.currentTimeMillis() + 30000);
			}
		}
	}
	
	public boolean testGroupCompetition(Client client) {
		this.sendGroupAction(client); // 尝试创建或加入帮派
		if (client.getGCompBaseInfoHolder().isEventsStart()) {
			// 帮战阶段
			if (client.getGCompEventsDataHolder().isNull()) {
				// 没有matchView数据
				this.getMatchData(client);
			} else if (client.getGCompOnlinememberHolder().getSizeOfOnlineMember() == 0) { // 未进入备战区
				// 进入备战区
				if (GroupCompSameSceneHandler.getHandler().enterPrepareArea(client)) {
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (Exception e) {
						RobotLog.fail("进入备战区成功，但是暂停被打断！", e);
					}
					GroupCompSameSceneHandler.getHandler().inPrepareArea(client);
				}
			} else { // 已经在备战区
				GroupCompSameSceneHandler.getHandler().informPreparePosition(client);
				switch (client.getGCompBaseInfoHolder().getGCompBaseInfo().getEventStatus()) {
				case TEAM_EVENTS:
					processTeamEvents(client);
					break;
				case PERSONAL_EVENTS:
					processPersonEvents(client);
					break;
				default:
					break;
				}
			}
		} else {
			this.sendGCompCmd(client); // 尝试发送帮战开始的指令
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
		return this.sendTeamRequestCommand(client, GCRequestType.PersonalMatching, Arrays.asList(client.getUserId()), new GCompPersonalMatchingMsgReceiver());
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
	

	private boolean quizForCompetion(Client client){
		GCQuizEventItem canQuizItem = GCQuizEventItemHolder.getInstance().getCanQuizItem();
		if(null == canQuizItem){
			return true;
		}
		Random rd = new Random();
		String quizGroupId = canQuizItem.getGroupA().getGroupId();
		if(1 == rd.nextInt(2)){
			quizGroupId = canQuizItem.getGroupB().getGroupId();
		}
		ReqNewGuess.Builder req = ReqNewGuess.newBuilder();
		req.setReqType(GCRequestType.NewGuess);
		req.setCoin(quizCountArr[rd.nextInt(quizCountArr.length)]);
		req.setGroupId(quizGroupId);
		req.setMatchId(canQuizItem.getMatchId());
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_QUIZ, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_COMPETITION_QUIZ;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					RsqNewGuess rsp = RsqNewGuess.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GroupCompetitionHandler[send] quizForCompetion转换响应消息为null");
						return false;
					}
					GCResultType result = rsp.getRstType();
					if (!result.equals(GCResultType.SUCCESS)) {
						RobotLog.info("GroupCompetitionHandler[send] quizForCompetion服务器返回不成功 ");
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupCompetitionHandler[send] quizForCompetion失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
}