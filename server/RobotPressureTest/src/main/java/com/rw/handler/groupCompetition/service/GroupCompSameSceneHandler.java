package com.rw.handler.groupCompetition.service;

import java.util.Random;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GroupCompetitionProto.AreaPosition;
import com.rwproto.GroupCompetitionProto.CommonReqMsg;
import com.rwproto.GroupCompetitionProto.CommonRspMsg;
import com.rwproto.GroupCompetitionProto.GCRequestType;
import com.rwproto.GroupCompetitionProto.GCResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupCompSameSceneHandler {

	public static final float pxMin = -0.7f;
	public static final float pxMax = 11.5f;
	public static final float pyMin = -6f;
	public static final float pyMax = -1f;

	private static GroupCompSameSceneHandler handler = new GroupCompSameSceneHandler();

	public static GroupCompSameSceneHandler getHandler() {
		return handler;
	}

	private Random random = new Random();

	private AreaPosition getRandomPosition() {
		AreaPosition.Builder areaBuilder = AreaPosition.newBuilder();
		areaBuilder.setX((pxMax - pxMin) * random.nextFloat() + pxMin);
		areaBuilder.setY((pyMax - pyMin) * random.nextFloat() + pyMin);
		return areaBuilder.build();
	}

	boolean leavePreareArea(Client client) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(GCRequestType.LeavePrepareArea);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION, req.build().toByteString(), null);
		return success;
	}

	boolean inPrepareArea(Client client) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(GCRequestType.InPrepareArea);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION, req.build().toByteString(), null);
		return success;
	}

	boolean enterPrepareArea(Client client) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(GCRequestType.EnterPrepareArea);
		req.setPosition(getRandomPosition());
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_COMPETITION;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					CommonRspMsg rsp = CommonRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GroupCompSameSceneHandler[send] enterPreparePosition转换响应消息为null");
						return false;
					}
					GCResultType result = rsp.getRstType();
					if (!result.equals(GCResultType.SUCCESS)) {
						RobotLog.info("GroupCompSameSceneHandler[send] enterPreparePosition服务器返回不成功:" + rsp.getTipMsg());
						return true;
					} else {
						RobotLog.info("GroupCompSameSceneHandler[send] enterPreparePosition成功！");
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupCompSameSceneHandler[send] enterPreparePosition失败", e);
					return false;
				}
			}
		});
		return success;
	}

	/**
	 * 备战区内走动
	 * 
	 * @param player
	 * @param gcRsp
	 */
	public boolean informPreparePosition(Client client) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(GCRequestType.InformPreparePosition);
		req.setPosition(getRandomPosition());
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_GROUP_COMPETITION, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_GROUP_COMPETITION;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					CommonRspMsg rsp = CommonRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GroupCompSameSceneHandler[send] informPreparePosition转换响应消息为null");
						return false;
					}
					GCResultType result = rsp.getRstType();
					if (!result.equals(GCResultType.SUCCESS)) {
						RobotLog.info("GroupCompSameSceneHandler[send] informPreparePosition服务器返回不成功:" + rsp.getTipMsg());
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("GroupCompSameSceneHandler[send] informPreparePosition失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
}