package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import com.bm.login.AccoutBM;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ProtocolMessageEnum;
import com.log.FSTraceLogger;
import com.log.GameLog;
import com.playerdata.randomname.RandomNameService;
import com.rw.netty.MsgResultType;
import com.rw.netty.ServerHandler;
import com.rw.netty.UserChannelMgr;
import com.rw.service.FsService;
import com.rw.service.login.game.GameLoginHandler;
import com.rw.service.platformgs.PlatformGSService;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.MsgDef.Command;
import com.rwproto.MsgExtensionProtos.MsgExtensionRequest;
import com.rwproto.ReConnectionProtos.ReConnectRequest;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestBody;
import com.rwproto.RequestProtos.RequestHeader;

public class FsNettyControler {

	private GameLoginHandler gameLoginHandler = new GameLoginHandler();
	private Map<Command, FsService<GeneratedMessage, ProtocolMessageEnum>> commandMap;

		long current = System.currentTimeMillis();
		public void doMyService(Request exRequest, ChannelHandlerContext ctx) {
		RequestHeader header = exRequest.getHeader();
		final Command command = header.getCommand();
		Command extCommand = command;// 需要處理的協議類型
		// 更新消息接收时间
		ServerHandler.updateSessionInfo(ctx, current, command);
		// GameLog.debug("msg:" + command);
		FSTraceLogger.logger("submit(" + command + "," + header.getSeqID() + ")" + ServerHandler.getCtxInfo(ctx, false));
		if (command == Command.MSG_LOGIN_GAME) {
			doGameLogin(exRequest, ctx);
		} else if (command == Command.MSG_RECONNECT) {
			ReConnect(exRequest, ctx);
		} else if (command == Command.MSG_PLATFORMGS) {
			doPlatformGSMsg(exRequest, ctx);
		} else if (command == Command.MSG_ACTIVITY_TIME) {
			// doPlatformActivityTime(exRequest, ctx);
		} else if (command == Command.MSG_RANDOM_NAME) {
			doGetRandomName(exRequest, ctx);
		} else {
			Long sessionId = ServerHandler.getSessionId(ctx);
			if (sessionId == null) {
				return;
			}
			// 还木有登录不处理逻辑与心跳
			String userId = UserChannelMgr.getBoundUserId(ctx);
			if (userId == null) {
				return;
			}
			if (command == Command.MSG_Rs_DATA) {
				try {
					MsgExtensionRequest msgExtReq = MsgExtensionRequest.parseFrom(exRequest.getBody().getSerializedContent());
					Request.Builder newBuilder = Request.newBuilder();
					RequestBody.Builder requestBody = RequestBody.newBuilder();
					requestBody.setSerializedContent(msgExtReq.getBody());
					newBuilder.setBody(requestBody.build());
					RequestHeader.Builder requestHeader = RequestHeader.newBuilder();
					requestHeader.mergeFrom(header);
					newBuilder.setHeader(requestHeader);
					exRequest = newBuilder.build();

					extCommand = msgExtReq.getCommandId();
				} catch (Exception e) {
					e.printStackTrace();
					UserChannelMgr.sendErrorResponse(userId, header, "", 500);
					return;
				}
			}

			// HeartBeat可以归到service做
			if (command == Command.MSG_HeartBeat) {
				GameWorldFactory.getGameWorld().asyncExecute(userId, new HeartBeatTask(sessionId, exRequest));
			} else {
				GameWorldFactory.getGameWorld().asyncExecute(userId, new GameLogicTask(sessionId, exRequest, extCommand));
			}
		}
	}

	private void ReConnect(Request request, ChannelHandlerContext ctx) {
		try {
			ReConnectRequest reconnectRequest = ReConnectRequest.parseFrom(request.getBody().getSerializedContent());
			String accountId = reconnectRequest.getAccountId();
			int zoneId = reconnectRequest.getZoneId();
			TableAccount userAccount = AccoutBM.getInstance().getByAccountId(accountId);
			if (userAccount == null) {
				GameLog.error("FsNettyControler", "#ReConnect", "find account fail on reconnecting:" + accountId + "," + zoneId);
				ReconnectCommon.getInstance().reLoginGame(this, ctx, request);
				return;
			}
			Long sessionId = ServerHandler.getSessionId(ctx);
			if (sessionId == null) {
				GameLog.error("FsNettyControler", "", "reconnet fail by disconnet:" + ctx.channel());
				return;
			}
			GameWorldFactory.getGameWorld().executeAccountTask(accountId, new ReconnectFilterTask(request, reconnectRequest, sessionId));
		} catch (Exception ex) {
			GameLog.error("PlayerReconnectTask", "#run()", "parse reconnect protocol exception:", ex);
			ReconnectCommon.getInstance().reLoginGame(this, ctx, request);
			return;
		}
	}

	private void doGameLogin(Request exRequest, ChannelHandlerContext ctx) {
		try {
			GameLoginRequest loginRequest = GameLoginRequest.parseFrom(exRequest.getBody().getSerializedContent());
			switch (loginRequest.getLoginType()) {
			case GAME_LOGIN:
				gameLoginHandler.gameServerLogin(loginRequest, ctx, exRequest.getHeader());
				break;
			case CREATE_ROLE:
				gameLoginHandler.createRoleAndLogin(loginRequest, ctx, exRequest.getHeader());
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	private void doPlatformGSMsg(Request exRequest, ChannelHandlerContext ctx) {
		PlatformGSService.doTask(exRequest, ctx);
	}

	private void doGetRandomName(Request exRequest, ChannelHandlerContext ctx) {
		RandomNameService.getInstance().processGetRandomName(exRequest, ctx);
	}

	public FsService<GeneratedMessage, ProtocolMessageEnum> getSerivice(Command command) {
		return commandMap.get(command);
	}

	public void setCommandMap(Map<Command, FsService<GeneratedMessage, ProtocolMessageEnum>> commandMap) {
		this.commandMap = commandMap;
	}

	public GameLoginHandler getGameLoginHandler() {
		return gameLoginHandler;
	}

	public void functionNotOpen(String userId, RequestHeader header) {
		UserChannelMgr.sendErrorResponse(userId, header, MsgResultType.FUNCTION_NOT_OPEN, 403);
	}
}
