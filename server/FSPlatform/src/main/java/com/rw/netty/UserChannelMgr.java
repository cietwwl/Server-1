package com.rw.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.log.PlatformLog;
import com.rw.controler.PlayerMsgCache;
import com.rw.fsutil.common.FastPair;
import com.rw.fsutil.dao.cache.SimpleCache;
import com.rw.fsutil.util.DateUtils;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.RequestHeader;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;
import com.rwproto.ResponseProtos.ResponseHeader.Builder;

public class UserChannelMgr {

	private static final AttributeKey<UserSession> USER_ID;
	private static final AttributeKey<SessionInfo> SESSION_INFO;
	private static final UserSession CLOSE_SESSION;
	private static long msgHoldMillis;
	private static ConcurrentHashMap<String, ChannelHandlerContext> userChannelMap;
	private static AtomicLong seesionIdGenerator;

	// 容量需要做成配置
	private static SimpleCache<String, PlayerMsgCache> msgCache;
	private static ConcurrentHashMap<Command, FastPair<Command, AtomicLong>> purgeStat;

	static {
		USER_ID = AttributeKey.valueOf("userId");
		SESSION_INFO = AttributeKey.valueOf("session");
		userChannelMap = new ConcurrentHashMap<String, ChannelHandlerContext>();
		seesionIdGenerator = new AtomicLong();
		msgHoldMillis = TimeUnit.MINUTES.toMillis(10);
		msgCache = new SimpleCache<String, PlayerMsgCache>(2000);
		purgeStat = new ConcurrentHashMap<Command, FastPair<Command, AtomicLong>>();
		CLOSE_SESSION = new UserSession("close", 0);
	}

	private final static ThreadLocal<ChannelHandlerContext> CTX_THREADLOCAL = new ThreadLocal<ChannelHandlerContext>();

	private static void logger(UserSession oldSession) {
		if (oldSession == CLOSE_SESSION) {
			PlatformLog.info("UserChannelMgr", "#bindUserID",
					"bingding fail:session closed");
		} else if (oldSession != null) {
			PlatformLog.info("UserChannelMgr", "#bindUserID",
					"bingding fail:already bingding:" + oldSession);
		}
	}

	public static void setThreadLocalCTX(ChannelHandlerContext ctx) {
		CTX_THREADLOCAL.set(ctx);
	}

	public static void removeThreadLocalCTX() {
		CTX_THREADLOCAL.remove();
	}

	public static ChannelHandlerContext getThreadLocalCTX() {
		return CTX_THREADLOCAL.get();
	}

	public static void bindUserID(String userId) {
		ChannelHandlerContext ctx = CTX_THREADLOCAL.get();
		if (ctx != null && StringUtils.isNotBlank(userId)) {
			userChannelMap.put(userId, ctx);
		}
	}

	public static void createSession(ChannelHandlerContext ctx) {
		Attribute<SessionInfo> attSession = ctx.channel().attr(SESSION_INFO);
		SessionInfo sessionInfo = ctx.channel().attr(SESSION_INFO).get();
		if (sessionInfo != null) {
			PlatformLog.error("updateSessionInfo", ctx.channel()
					.remoteAddress().toString(), "session already create!");
		} else {
			sessionInfo = new SessionInfo();
			if (attSession.setIfAbsent(sessionInfo) != null) {
				PlatformLog.error("updateSessionInfo", ctx.channel()
						.remoteAddress().toString(),
						"multi thread create session!");
			}
		}

	}

	public static void updateSessionInfo(ChannelHandlerContext ctx,
			long lastRecvMsgMillis, Command command) {
		SessionInfo sessionInfo = ctx.channel().attr(SESSION_INFO).get();
		if (sessionInfo == null) {
			PlatformLog.error("updateSessionInfo", ctx.channel()
					.remoteAddress().toString(), "not set session info");
		} else {
			sessionInfo.setLastCommand(command);
			sessionInfo.setLastRecvMsgMillis(lastRecvMsgMillis);
		}
	}

	/**
	 * 获取当前玩家sessionId，还没登录返回-1
	 * 
	 * @param userId
	 * @return
	 */
	public static long getCurrentSessionId(String userId) {
		return getUserSessionId(userChannelMap.get(userId));
	}

	/**
	 * 获取指定{@link ChannelHandlerContext}的sessionId，指定ctx为null或者ctx还没绑定返回-1
	 * 
	 * @param userId
	 * @return
	 */
	public static long getUserSessionId(ChannelHandlerContext ctx) {
		if (ctx == null) {
			return -1;
		}
		Attribute<UserSession> attrSession = ctx.channel().attr(USER_ID);
		UserSession oldSession = attrSession.get();
		if (oldSession == null) {
			return -1;
		}
		return oldSession.getSessionId();
	}

	/**
	 * 关闭连接
	 * 
	 * @param ctx
	 */
	public static void closeSession(ChannelHandlerContext ctx) {
		Attribute<UserSession> userIdAttr = ctx.channel().attr(USER_ID);
		for (;;) {
			UserSession oldSession = userIdAttr.get();
			if (oldSession == CLOSE_SESSION) {
				break;
			}
			if (!userIdAttr.compareAndSet(oldSession, CLOSE_SESSION)) {
				continue;
			}
			if (oldSession != null) {
				String userId = oldSession.getUserId();
				boolean result = userChannelMap.remove(userId, ctx);

				PlatformLog
						.info("ChannelHandlerContext", "#exitcloseSession",
								"close connection:" + result + "," + userId
										+ "," + ctx);
			}
			break;
		}
	}

	public static boolean bindUserID(String userId, ChannelHandlerContext ctx) {
		if (ctx == null || !StringUtils.isNotBlank(userId)) {
			return false;
		}
		PlatformLog.info("UserChannelMgr", "#bindUserID",
				"bingding connection:" + userId + "," + ctx);
		Attribute<UserSession> attrSession = ctx.channel().attr(USER_ID);
		UserSession oldSession = attrSession.get();
		if (oldSession != null) {
			logger(oldSession);
			return false;
		}
		UserSession newSession = new UserSession(userId,
				seesionIdGenerator.incrementAndGet());
		oldSession = attrSession.setIfAbsent(newSession);
		if (oldSession != null) {
			logger(oldSession);
			return false;
		}
		ChannelHandlerContext old = userChannelMap.put(userId, ctx);

		if (ctx.channel().attr(USER_ID) == CLOSE_SESSION) {
			userChannelMap.remove(userId, ctx);
			return false;
		}
		return true;
	}

	public static String getUserId() {
		ChannelHandlerContext ctx = CTX_THREADLOCAL.get();
		if (ctx != null) {
			return getUserId(ctx);
		}
		return null;
	}

	public static String getUserId(ChannelHandlerContext ctx) {
		Attribute<UserSession> attr = ctx.channel().attr(USER_ID);
		UserSession userSession = attr.get();
		if (userSession != null) {
			return userSession.getUserId();
		} else {
			return null;
		}
	}

	public static void remove(ChannelHandlerContext ctx) {
		String userId = getUserId(ctx);
		if (null != userId) {
			userChannelMap.remove(ctx);

		}
	}

	public static ChannelHandlerContext get(String userId) {
		if (StringUtils.isNotBlank(userId)) {
			return userChannelMap.get(userId);
		}
		return null;
	}

	public static ChannelFuture sendResponse(String userId,
			RequestHeader header, ByteString resultContent,
			ChannelHandlerContext ctx, ByteString synData) {
		return sendResponse(userId, header, resultContent, 200, ctx, synData);
	}

	public static ChannelFuture sendResponse(String userId,
			RequestHeader header, ByteString resultContent, int statusCode,
			ChannelHandlerContext ctx, ByteString synData) {
		boolean sendMsg = ctx != null;
		boolean saveMsg = userId != null;
		if (!sendMsg && !saveMsg) {
			return null;
		}
		ResponseHeader responseHeader = getResponseHeader(header,
				header.getCommand(), statusCode, synData);

		Response.Builder builder = Response.newBuilder().setHeader(
				responseHeader);
		if (resultContent != null) {
			builder.setSerializedContent(resultContent);
		} else {
			builder.setSerializedContent(ByteString.EMPTY);
		}
		Response result = builder.build();
		if (!checkMsgSize(result)) {
			return null;
		}
		if (saveMsg) {
			addResponse(userId, result);
		}
		if (sendMsg) {
			ChannelFuture future = ctx.channel().writeAndFlush(result);
			final Command cmd = result.getHeader().getCommand();
			final int size = result.getSerializedContent().size();
			final int seqId = result.getHeader().getSeqID();
			future.addListener(new GenericFutureListener<Future<Void>>() {

				@Override
				public void operationComplete(Future<Void> future) throws Exception {
					PlatformLog.debug("#发送消息:" + cmd + ",size=" + size + ",seqId=" + seqId + "," + future.isSuccess());
				}
			});
			return future;
		} else {
			return null;
		}
	}

	public static ResponseHeader getResponseHeader(RequestHeader header,
			Command command, int statusCode, ByteString synData) {
		String token = header.getToken();
		int seqId = header.getSeqID();
		Builder headerBuilder = ResponseHeader.newBuilder().setSeqID(seqId)
				.setToken(token).setCommand(command).setStatusCode(statusCode);
		if (synData != null) {
			headerBuilder.setSynData(synData);
		}
		return headerBuilder.build();
	}

	public static void addResponse(String userId, Response response) {
		int seqId = response.getHeader().getSeqID();
		if (seqId == 0) {
			return;
		}
		PlayerMsgCache msg = msgCache.get(userId);
		if (msg == null) {
			// 消息容量也需要做成配置
			msg = new PlayerMsgCache(1, purgeStat);
			PlayerMsgCache old = msgCache.putIfAbsent(userId, msg);
			if (old != null) {
				msg = old;
			}
		}
		msg.add(seqId, response);
	}

	/*** 发送消息的最大字节数 **/
	private static int maxMsgSize = 30000;
	private static int baseMsgSize = 5000;

	public static boolean checkMsgSize(Response response) {

		if (response.getSerializedContent().size() >= baseMsgSize) {
			String errorReason = "返回消息"
					+ response.getHeader().getCommand().toString() + "长度大于"
					+ (maxMsgSize / 1000) + "K";
			if (response.getSerializedContent().size() >= maxMsgSize) {
				PlatformLog.error("Platform", " GameUtil[checkMsgSize]",
						errorReason);
			}

		}

		return true;
	}

	public static String getCtxInfo(ChannelHandlerContext ctx) {
		return getCtxInfo(ctx, true);
	}

	public static String getCtxInfo(ChannelHandlerContext ctx,
			boolean addLastCommand) {
		try {
			StringBuilder sb = new StringBuilder();
			Channel channel = ctx.channel();
			UserSession userSession = channel.attr(USER_ID).get();
			if (userSession != null) {
				sb.append('[').append(userSession.getUserId()).append(',')
						.append(userSession.getSessionId()).append(']');
			} else {
				sb.append("[not binding]");
			}
			SessionInfo info = channel.attr(SESSION_INFO).get();
			if (info != null) {
				long current = System.currentTimeMillis();
				sb.append('(');
				if (addLastCommand) {
					sb.append(info.getLastCommand()).append(',');
				}
				sb.append((current - info.getCreateMillis()) / 1000)
						.append(',');
				sb.append((current - info.getLastRecvMsgMillis()) / 1000)
						.append(')');
			}
			return sb.toString();
		} catch (Throwable t) {
			t.printStackTrace();
			return "[exception]";
		}
	}

	public static void purgeMsgRecord() {
		long purgeTime = DateUtils.getSecondLevelMillis() - msgHoldMillis;
		List<PlayerMsgCache> msgCaches = msgCache.values();
		for (int i = msgCaches.size(); --i >= 0;) {
			msgCaches.get(i).purge(purgeTime);
		}
	}
}
