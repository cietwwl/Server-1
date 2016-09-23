package com.rw.handler.groupCompetition.service;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.dataSyn.DataSynHelper;
import com.rw.handler.battle.army.CurAttrData;
import com.rw.handler.group.data.UserGroupData;
import com.rw.handler.groupFight.data.GFDefendArmyItem;
import com.rw.handler.groupFight.data.GFDefendArmyItemHolder;
import com.rw.handler.groupFight.data.GFightOnlineGroupData;
import com.rw.handler.groupFight.data.GFightOnlineGroupHolder;
import com.rw.handler.groupFight.data.GFightOnlineResourceData;
import com.rw.handler.groupFight.data.GFightOnlineResourceHolder;
import com.rw.handler.groupFight.data.UserGFightOnlineData;
import com.rw.handler.groupFight.data.UserGFightOnlineHolder;
import com.rw.handler.groupFight.dataForClient.DefendArmyHerosInfo;
import com.rw.handler.groupFight.dataForClient.GFightResult;
import com.rw.handler.groupFight.dataForRank.GFBidRankHolder;
import com.rw.handler.groupFight.dataForRank.GFGroupBiddingItem;
import com.rw.handler.hero.TableUserHero;
import com.rwproto.GrouFightOnlineProto.GFRequestType;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineReqMsg;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.GroupCompetitionProto.ReqAllGuessInfo;
import com.rwproto.GroupCompetitionProto.ReqNewGuess;
import com.rwproto.GroupCompetitionProto.RspAllGuessInfo;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupCompetitionHandler {

	private static GroupCompetitionHandler handler = new GroupCompetitionHandler();

	public static GroupCompetitionHandler getHandler() {
		return handler;
	}
	
	public boolean gcompQuiz(Client client){
		boolean result = synGroupFight(client);
		if (!result) {
			RobotLog.fail("playGroupFight[send]在线帮战同步资源点信息反馈结果=" + result);
			return result;
		}
		GFightOnlineResourceData gfResData = GFightOnlineResourceHolder.getInstance().getUserGFData(RESOURCE_ID);
		switch(gfResData.getState()){
		case 1://休战
			RobotLog.fail("playGroupFight[send]在线帮战资源点" + RESOURCE_ID + "正在休战中");
			return true;
		case 2://竞标阶段
			return playGroupFightBid(client);
		case 3://备战阶段
			return playGroupFightPrepare(client);
		case 4://开战阶段
			return playGFStartFight(client);
		default:
			return true;
		}
	}
	
	/**
	 * 获取当前的可竞猜项目
	 * @param player
	 * @param gcRsp
	 */
	public boolean getCanQuizMatch(Client client) {
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
						RobotLog.fail("GroupCompetitionHandler[send] getCanQuizMatch服务器返回不成功 ");
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupCompetitionHandler[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	private void quizForCompetion(Client client){
		ReqNewGuess.Builder req = ReqNewGuess.newBuilder();
		req.setReqType(GCRequestType.NewGuess);
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
						RobotLog.fail("GroupCompetitionHandler[send] getCanQuizMatch服务器返回不成功 ");
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupCompetitionHandler[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
}