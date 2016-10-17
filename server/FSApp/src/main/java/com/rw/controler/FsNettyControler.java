package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import com.bm.login.AccoutBM;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ProtocolMessageEnum;
import com.log.FSTraceLogger;
import com.log.GameLog;
import com.rw.netty.UserChannelMgr;
import com.rw.netty.UserSession;
import com.rw.service.FsService;
import com.rw.service.login.game.GameLoginHandler;
import com.rw.service.platformgs.PlatformGSService;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.MsgDef.Command;
import com.rwproto.ReConnectionProtos.ReConnectRequest;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestHeader;

public class FsNettyControler {

	private GameLoginHandler gameLoginHandler = new GameLoginHandler();
	private Map<Command, FsService<GeneratedMessage, ProtocolMessageEnum>> commandMap;

	public void doMyService(Request exRequest, ChannelHandlerContext ctx) {
		long current = System.currentTimeMillis();
		RequestHeader header = exRequest.getHeader();
		final Command command = header.getCommand();
		// 更新消息接收时间
		UserChannelMgr.updateSessionInfo(ctx, current, command);
		// GameLog.debug("msg:" + command);
		FSTraceLogger.logger("submit(" + command + "," + header.getSeqID() + ")" + UserChannelMgr.getCtxInfo(ctx, false));
		if (command == Command.MSG_LOGIN_GAME) {
			doGameLogin(exRequest, ctx);
		} else if (command == Command.MSG_RECONNECT) {
			ReConnect(exRequest, ctx);
		} else if (command == Command.MSG_PLATFORMGS) {
			doPlatformGSMsg(exRequest, ctx);
		} else {
			UserSession session = UserChannelMgr.getUserSession(ctx);
			if (session == null) {
				return;
			}
			// HeartBeat可以归到service做
			if (command == Command.MSG_HeartBeat) {
				GameWorldFactory.getGameWorld().asyncExecute(session.getUserId(), new HeartBeatTask(session, exRequest));
			} else {
				GameWorldFactory.getGameWorld().asyncExecute(session.getUserId(), new GameLogicTask(session, exRequest));
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
				GameLog.error("FsNettyControler", "#ReConnect()", "find account fail on reconnecting:" + accountId + "," + zoneId);
				ReconnectCommon.getInstance().reLoginGame(this, ctx, request);
				return;
			}
			GameWorldFactory.getGameWorld().executeAccountTask(accountId, new ReconnectFilterTask(request, reconnectRequest, ctx));
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
		ByteString resultContent = PlatformGSService.doTask(exRequest);
		UserChannelMgr.sendResponse(null, exRequest.getHeader(), resultContent, 200, ctx);
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
		UserChannelMgr.sendErrorResponse(userId, header, 403);
	}
}
