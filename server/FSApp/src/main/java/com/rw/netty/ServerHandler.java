package com.rw.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.log.GameLog;
import com.rw.controler.FsNettyControler;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	private static ConcurrentHashMap<Long, ChannelHandlerContext> channelMap;
	private static final AttributeKey<SessionInfo> SESSION_INFO;
	private static AtomicLong seesionIdGenerator;
	static {
		channelMap = new ConcurrentHashMap<Long, ChannelHandlerContext>(2048);
		SESSION_INFO = AttributeKey.valueOf("session");
		seesionIdGenerator = new AtomicLong();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			FsNettyControler controler = SpringContextUtil.getBean("fsNettyControler");

			Request request = (Request) msg;
			controler.doMyService(request, ctx);

		} catch (Exception e) {
			GameLog.error("ServerHandler", "ServerHandler[channelRead]", "", e);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().flush();

	};

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		GameLog.error("ServerHandler", "ServerHandler[exceptionCaught]", "", cause);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		String addressInfo = String.valueOf(ctx.channel().remoteAddress());
		long id = seesionIdGenerator.incrementAndGet();
		System.out.println("open connection=" + id + ":" + addressInfo);
		super.channelRegistered(ctx);
		Attribute<SessionInfo> attSession = ctx.channel().attr(SESSION_INFO);
		SessionInfo sessionInfo = ctx.channel().attr(SESSION_INFO).get();
		if (sessionInfo != null) {
			System.out.println("session already create! " + addressInfo);
		} else {
			sessionInfo = new SessionInfo(id);
			if (attSession.setIfAbsent(sessionInfo) != null) {
				System.out.println("multi thread create session! " + addressInfo);
			} else {
				ChannelHandlerContext old = channelMap.put(sessionInfo.getSessionId(), ctx);
				if (old != null) {
					GameLog.error("createSession", addressInfo, "duplidate session id channel:" + ctx + "," + old);
				}
			}
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}
		SessionInfo session = ctx.channel().attr(SESSION_INFO).get();
		if (session == null) {
			ctx.close();
			GameLog.error("session idle", ctx.channel().remoteAddress().toString(), "not create session!");
		} else {
			Command lastCommand = session.getLastCommand();
			if (lastCommand == null) {
				ctx.close();
				GameLog.error("session idle", ctx.channel().remoteAddress().toString(), "session has not command:" + getCtxInfo(ctx));
			} else if (lastCommand == Command.MSG_PLATFORMGS) {
				// 平台先不处理
				return;
			} else {
				long current = System.currentTimeMillis();
				if (current - session.getLastRecvMsgMillis() > UserChannelMgr.RECONNECT_TIME) {
					ctx.close();
					GameLog.info("session idle", ctx.channel().remoteAddress().toString(), "idle timeout:" + getCtxInfo(ctx));
				}
			}
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("close connection:" + getCtxInfo(ctx));
		super.channelUnregistered(ctx);
		Channel channel = ctx.channel();
		Attribute<SessionInfo> session = channel.attr(SESSION_INFO);
		SessionInfo sessionInfo = session.get();
		if (sessionInfo == null) {
			return;
		}
		Long sessionId = sessionInfo.getSessionId();
		if (sessionId == null) {
			System.out.println("remove session fail by session id is null:" + channel);
			return;
		}
		if (channelMap.remove(sessionId) == null) {
			System.out.println("remove channel fail:" + sessionId);
		} else {
			UserChannelMgr.unbindUserId(sessionId, ctx);
		}
	}

	public static ChannelHandlerContext getChannelHandlerContext(Long id) {
		return channelMap.get(id);
	}

	/**
	 * 获取指定{@link ChannelHandlerContext}的sessionId，指定ctx为null或者ctx还没绑定返回null
	 * @param ctx
	 * @return
	 */
	public static Long getSessionId(ChannelHandlerContext ctx) {
		SessionInfo session = ctx.channel().attr(SESSION_INFO).get();
		if (session == null) {
			return null;
		}
		return session.getSessionId();
	}

	public static void updateSessionInfo(ChannelHandlerContext ctx, long lastRecvMsgMillis, Command command) {
		SessionInfo sessionInfo = ctx.channel().attr(SESSION_INFO).get();
		if (sessionInfo == null) {
			GameLog.error("updateSessionInfo", ctx.channel().remoteAddress().toString(), "not set session info");
		} else {
			sessionInfo.setLastCommand(command);
			sessionInfo.setLastRecvMsgMillis(lastRecvMsgMillis);
		}
	}

	public static boolean isConnecting(Long sessionId) {
		return channelMap.containsKey(sessionId);
	}

	public static String getCtxInfo(Long sessionId) {
		ChannelHandlerContext ctx = channelMap.get(sessionId);
		if (ctx == null) {
			return "[not connecting]";
		}
		return getCtxInfo(ctx, true);
	}

	public static String getCtxInfo(Long sessionId, boolean addLastCommand) {
		ChannelHandlerContext ctx = channelMap.get(sessionId);
		if (ctx == null) {
			return "[not connecting]";
		}
		return getCtxInfo(ctx, addLastCommand);
	}

	public static String getCtxInfo(ChannelHandlerContext ctx) {
		return getCtxInfo(ctx, true);
	}

	public static String getCtxInfo(ChannelHandlerContext ctx, boolean addLastCommand) {
		try {
			SessionInfo info = ctx.channel().attr(SESSION_INFO).get();
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			if (info == null) {
				sb.append("not register");
			} else {
				sb.append(info.getSessionId());
			}
			sb.append('-');
			String userId = UserChannelMgr.getBoundUserId(ctx);

			if (userId != null) {
				sb.append(userId).append(']');
			} else {
				sb.append("not binding]");
			}
			if (info != null) {
				long current = System.currentTimeMillis();
				sb.append('(');
				if (addLastCommand) {
					sb.append(info.getLastCommand()).append(',');
				}
				sb.append((current - info.getCreateMillis()) / 1000).append(',');
				sb.append((current - info.getLastRecvMsgMillis()) / 1000).append(')');
			}
			return sb.toString();
		} catch (Throwable t) {
			t.printStackTrace();
			return "[exception]";
		}
	}
}