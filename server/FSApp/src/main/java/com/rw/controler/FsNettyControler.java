package com.rw.controler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import com.bm.login.AccoutBM;
import com.common.GameUtil;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.FSTraceLogger;
import com.log.GameLog;
import com.rw.fsutil.dao.cache.SimpleCache;
import com.rw.netty.UserSession;
import com.rw.netty.UserChannelMgr;
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
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;

public class FsNettyControler {

	private GameLoginHandler gameLoginHandler = new GameLoginHandler();
	// 容量需要做成配置
	private SimpleCache<String, PlayerMsgCache> msgCache = new SimpleCache<String, PlayerMsgCache>(2000);
	private Map<Command, FsService> commandMap;

	public void doMyService(Request exRequest, ChannelHandlerContext ctx) {
		long current = System.currentTimeMillis();
		RequestHeader header = exRequest.getHeader();
		final Command command = header.getCommand();
		//更新消息接收时间
		UserChannelMgr.updateSessionInfo(ctx, current, command);
//		GameLog.debug("msg:" + command);
		FSTraceLogger.logger("submit("+command+","+header.getSeqID()+")" + UserChannelMgr.getCtxInfo(ctx, false));
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

	public void sendErrorResponse(String userId, RequestHeader header, int exceptionCode) {
		ChannelHandlerContext ctx = UserChannelMgr.get(userId);
		sendResponse(userId, header, null, exceptionCode, ctx);
	}

	private void doPlatformGSMsg(Request exRequest, ChannelHandlerContext ctx) {
		ByteString resultContent = PlatformGSService.doTask(exRequest);
		sendResponse(null, exRequest.getHeader(), resultContent, 200, ctx);
	}

	public void sendResponse(String userId, RequestHeader header, ByteString resultContent, ChannelHandlerContext ctx) {
		sendResponse(userId, header, resultContent, 200, ctx);
	}

	public ChannelFuture sendResponse(RequestHeader header, ByteString resultContent, ChannelHandlerContext ctx) {
		return sendResponse(null, header, resultContent, 200, ctx);
	}

	public void sendResponse(String userId, RequestHeader header, ByteString resultContent, long sessionId) {
		if (userId == null) {
			return;
		}
		ChannelHandlerContext ctx = UserChannelMgr.get(userId);
		if (ctx != null && sessionId != UserChannelMgr.getUserSessionId(ctx)) {
			ctx = null;
		}
		sendResponse(userId, header, resultContent, 200, ctx);
	}

	/**
	 * <pre>
	 * 发送异步消息(指客户端不强制等待此消息，如同步数据变化)
	 * </pre>
	 * 
	 * @param userId
	 * @param ctx
	 * @param Cmd
	 * @param pBuffer
	 */
	public ChannelFuture sendAyncResponse(String userId, ChannelHandlerContext ctx, Command Cmd, ByteString pBuffer) {
		if (ctx == null) {
			return null;
		}
		Response.Builder builder = Response.newBuilder().setHeader(ResponseHeader.newBuilder().setCommand(Cmd).setToken("").setStatusCode(200));
		if (pBuffer != null) {
			builder.setSerializedContent(pBuffer);
		} else {
			builder.setSerializedContent(ByteString.EMPTY);
		}
		if (!GameUtil.checkMsgSize(builder, userId)) {
			return null;
		}
		Response response = builder.build();
		return ctx.channel().writeAndFlush(response);
	}

	public ChannelFuture sendResponse(String userId, RequestHeader header, ByteString resultContent, int statusCode, ChannelHandlerContext ctx) {
		boolean sendMsg = ctx != null;
		boolean saveMsg = userId != null;
		if (!sendMsg && !saveMsg) {
			return null;
		}
		Response.Builder builder = Response.newBuilder().setHeader(getResponseHeader(header, header.getCommand(), statusCode));
		if (resultContent != null) {
			builder.setSerializedContent(resultContent);
		} else {
			builder.setSerializedContent(ByteString.EMPTY);
		}
		Response result = builder.build();
		if (!GameUtil.checkMsgSize(result)) {
			return null;
		}
		if (saveMsg) {
			addResponse(userId, result);
		}
		if (sendMsg) {
			ChannelFuture future = ctx.channel().writeAndFlush(result);
			GameLog.debug("##发送消息" + "  " + result.getHeader().getCommand().toString() + "  Size:" + result.getSerializedContent().size());
			return future;
		}else{
			return null;
		}
	}

	public ResponseHeader getResponseHeader(RequestHeader header, Command command) {
		return getResponseHeader(header, command, 200);
	}

	public ResponseHeader getResponseHeader(RequestHeader header, Command command, int statusCode) {
		String token = header.getToken();
		int seqId = header.getSeqID();
		return ResponseHeader.newBuilder().setSeqID(seqId).setToken(token).setCommand(command).setStatusCode(statusCode).build();
	}

	public FsService getSerivice(Command command) {
		return commandMap.get(command);
	}

	public void setCommandMap(Map<Command, FsService> commandMap) {
		this.commandMap = commandMap;
	}

	public GameLoginHandler getGameLoginHandler() {
		return gameLoginHandler;
	}

	public void addResponse(String userId, Response response) {
		int seqId = response.getHeader().getSeqID();
		if (seqId == 0) {
			return;
		}
		PlayerMsgCache msg = msgCache.get(userId);
		if (msg == null) {
			// 消息容量也需要做成配置
			msg = new PlayerMsgCache(10);
			PlayerMsgCache old = msgCache.putIfAbsent(userId, msg);
			if (old != null) {
				msg = old;
			}
		}
		msg.add(seqId, response);
	}

	public Response getResponse(String userId, int seqId) {
		PlayerMsgCache msg = msgCache.get(userId);
		if (msg == null) {
			return null;
		}
		return msg.getResponse(seqId);
	}

	public void clearMsgCache(String userId) {
		PlayerMsgCache msg = msgCache.get(userId);
		if (msg == null) {
			return;
		}
		msg.clear();
	}
}
