package com.rw.handler.groupCompetition.service;

import java.util.List;
import java.util.Random;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.groupCompetition.data.guess.GCQuizEventItem;
import com.rw.handler.groupCompetition.data.guess.GCQuizEventItemHolder;
import com.rw.handler.groupCompetition.data.onlinemember.GCompOnlineMember;
import com.rw.handler.groupCompetition.util.GCompUtil;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.GroupCompetitionProto.ReqAllGuessInfo;
import com.rwproto.GroupCompetitionProto.ReqNewGuess;
import com.rwproto.GroupCompetitionProto.RspAllGuessInfo;
import com.rwproto.GroupCompetitionProto.RsqNewGuess;
import com.rwproto.GroupCompetitionProto.TeamMemberRequest;
import com.rwproto.GroupCompetitionProto.TeamRequest;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupCompetitionHandler {

	public static final int[] quizCountArr = {100000, 200000, 500000};
	
	private static GroupCompetitionHandler handler = new GroupCompetitionHandler();

	public static GroupCompetitionHandler getHandler() {
		return handler;
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
		builder.setReqType(GCRequestType.CreateTeam);
		if (heroIds != null && heroIds.size() > 0) {
			builder.addAllHeroId(heroIds);
		}
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION_TEAM_REQ, builder.build().toByteString(), msgReceiver);
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
	
	public boolean requestRandomMatching(Client client) {
		return this.sendTeamRequestCommand(client, GCRequestType.StartRandomMatching, null, new GCompRandomMatchingMsgReceiver());
	}
	
	public boolean requestPersonalMatching(Client client) {
		return this.sendTeamRequestCommand(client, GCRequestType.PersonalMatching, null, new GCompPersonalMatchingMsgReceiver());
	}
	
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
					if (!result.equals(GFResultType.SUCCESS)) {
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