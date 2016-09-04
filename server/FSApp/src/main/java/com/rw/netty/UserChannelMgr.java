package com.rw.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserDataMgr;
import com.playerdata.dataSyn.SynDataInReqMgr;
import com.rw.controler.FsNettyControler;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.eLog.eBILogRegSubChannelToClientPlatForm;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwproto.GameLoginProtos.GameLoginResponse;
import com.rwproto.GameLoginProtos.eLoginResultType;
import com.rwproto.MsgDef.Command;

public class UserChannelMgr {

	private static final AttributeKey<UserSession> USER_ID;
	private static final AttributeKey<SessionInfo> SESSION_INFO;
	private static final AttributeKey<SynDataInReqMgr> SYN_DATA;
	public static final long RECONNECT_TIME;
	private static final UserSession CLOSE_SESSION;
	private static ConcurrentHashMap<String, ChannelHandlerContext> userChannelMap;
	private static ConcurrentHashMap<String, Long> disconnectMap;
	private static AtomicLong seesionIdGenerator;

	static {
		USER_ID = AttributeKey.valueOf("userId");
		SESSION_INFO = AttributeKey.valueOf("session");
		SYN_DATA = AttributeKey.valueOf("syn_data");
		RECONNECT_TIME = TimeUnit.MINUTES.toMillis(5);
		CLOSE_SESSION = new UserSession("close", 0);
		userChannelMap = new ConcurrentHashMap<String, ChannelHandlerContext>();
		disconnectMap = new ConcurrentHashMap<String, Long>();
		seesionIdGenerator = new AtomicLong();
	}

	private static void logger(UserSession oldSession) {
		if (oldSession == CLOSE_SESSION) {
			GameLog.info("UserChannelMgr", "#bindUserID", "bingding fail:session closed");
		} else if (oldSession != null) {
			GameLog.info("UserChannelMgr", "#bindUserID", "bingding fail:already bingding:" + oldSession);
		}
	}

	public static boolean bindUserID(String userId, ChannelHandlerContext ctx, boolean recordLogin) {
		if (ctx == null || !StringUtils.isNotBlank(userId)) {
			return false;
		}
		GameLog.info("UserChannelMgr", "#bindUserID", "bingding connection:" + userId + "," + ctx);
		Attribute<UserSession> attrSession = ctx.channel().attr(USER_ID);
		UserSession oldSession = attrSession.get();
		if (oldSession != null) {
			logger(oldSession);
			return false;
		}
		UserSession newSession = new UserSession(userId, seesionIdGenerator.incrementAndGet());
		oldSession = attrSession.setIfAbsent(newSession);
		if (oldSession != null) {
			logger(oldSession);
			return false;
		}
		ChannelHandlerContext old = userChannelMap.put(userId, ctx);
		// if (old != null) {
		// // TODO 通过消息通知
		// old.close();
		// }
		if (ctx.channel().attr(USER_ID) == CLOSE_SESSION) {
			userChannelMap.remove(userId, ctx);
			return false;
		}
		// 这里缺少多线程保护，会导致已经断线的人时间被清空，后果是无法直接在游戏内重连
		if (disconnectMap.remove(userId) == null && recordLogin) {
			BILogMgr.getInstance().logZoneLogin(userId);
		}
		return true;
	}

	public static String getUserId(ChannelHandlerContext ctx) {
		Attribute<UserSession> userIdAttr = ctx.channel().attr(USER_ID);
		UserSession session = userIdAttr.get();
		return session == null ? null : session.getUserId();
	}

	public static UserSession getUserSession(ChannelHandlerContext ctx) {
		Attribute<UserSession> userIdAttr = ctx.channel().attr(USER_ID);
		return userIdAttr.get();
	}

	public static SessionInfo getSession(ChannelHandlerContext ctx) {
		return ctx.channel().attr(SESSION_INFO).get();
	}

	public static String getCtxInfo(ChannelHandlerContext ctx){
		return getCtxInfo(ctx, true);
	}
	
	public static String getCtxInfo(ChannelHandlerContext ctx, boolean addLastCommand) {
		try {
			StringBuilder sb = new StringBuilder();
			Channel channel = ctx.channel();
			UserSession userSession = channel.attr(USER_ID).get();
			if (userSession != null) {
				sb.append('[').append(userSession.getUserId()).append(',').append(userSession.getSessionId()).append(']');
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
				sb.append((current - info.getCreateMillis()) / 1000).append(',');
				sb.append((current - info.getLastRecvMsgMillis()) / 1000).append(')');
			}
			return sb.toString();
		} catch (Throwable t) {
			t.printStackTrace();
			return "[exception]";
		}
	}

	public static void createSession(ChannelHandlerContext ctx) {
		Attribute<SessionInfo> attSession = ctx.channel().attr(SESSION_INFO);
		SessionInfo sessionInfo = ctx.channel().attr(SESSION_INFO).get();
		if (sessionInfo != null) {
			GameLog.error("updateSessionInfo", ctx.channel().remoteAddress().toString(), "session already create!");
		} else {
			sessionInfo = new SessionInfo();
			if (attSession.setIfAbsent(sessionInfo) != null) {
				GameLog.error("updateSessionInfo", ctx.channel().remoteAddress().toString(), "multi thread create session!");
			}
		}

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
	 * <pre>
	 * 获取{@link SynDataInReqMgr}，若玩家不在线，返回null
	 * </pre>
	 * 
	 * @param userId
	 * @return
	 */
	public static SynDataInReqMgr getSynDataInReqMgr(String userId) {
		ChannelHandlerContext ctx = userChannelMap.get(userId);
		if (ctx == null) {
			return null;
		}
		return getSynDataInReqMgr(ctx);
	}

	private static SynDataInReqMgr getSynDataInReqMgr(ChannelHandlerContext ctx) {
		Attribute<SynDataInReqMgr> synDataAttr = ctx.channel().attr(SYN_DATA);
		SynDataInReqMgr synData = synDataAttr.get();
		if (synData != null) {
			return synData;
		}
		synData = new SynDataInReqMgr();
		SynDataInReqMgr oldSynData = synDataAttr.setIfAbsent(synData);
		return oldSynData == null ? synData : oldSynData;
	}

	public static boolean onBSBegin(String userId) {
		SynDataInReqMgr synData = getSynDataInReqMgr(userId);
		if (synData == null) {
			return false;
		}
		return synData.setInReq();
	}

	public static boolean onBSEnd(String userId) {
		ChannelHandlerContext ctx = userChannelMap.get(userId);
		if (ctx == null) {
			return false;
		}

		SynDataInReqMgr synData = getSynDataInReqMgr(ctx);
		if (synData == null) {
			return false;
		}
		return synData.doSyn(ctx, userId);
	}

	/**
	 * 踢出玩家移除连接数，不主动断开连接
	 * 
	 * @param userId
	 */
	public static void kickoffDisconnect(String userId) {
		ChannelHandlerContext ctx = UserChannelMgr.get(userId);
		GameLog.info("UserChannelMgr", "#kickoffDisconnect", "消息跟踪@移除ctx:" + userId + "," + ctx);
		// userChannelMapCpy.remove(userId, ctx);
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
				if (result) {
					disconnectMap.put(userId, System.currentTimeMillis());
				}
				GameLog.info("ChannelHandlerContext", "#exitcloseSession", "close connection:" + result + "," + userId + "," + ctx);
			}
			break;
		}
	}

	public static ChannelHandlerContext get(String userId) {
		if (StringUtils.isNotBlank(userId)) {
			return userChannelMap.get(userId);
		}
		return null;
	}

	/**
	 * 获取在线列表
	 * 
	 * @return
	 */
	public static List<String> getOnlineList() {
		List<String> onlineList = new ArrayList<String>();
		Collection<String> values = userChannelMap.keySet();
		onlineList.addAll(values);
		return onlineList;
	}

	public static int getCount() {
		return userChannelMap.size();
	}

	public static Map<String, eBILogRegSubChannelToClientPlatForm> getSubChannelCount() {
		Map<String, eBILogRegSubChannelToClientPlatForm> countMap = new HashMap<String, eBILogRegSubChannelToClientPlatForm>();
		Enumeration<String> keys1 = userChannelMap.keys();
		while (keys1.hasMoreElements()) {
			String nextkey = keys1.nextElement();
			Player nextOnline = PlayerMgr.getInstance().find(nextkey);
			if (nextOnline != null) {
				doCount(countMap, nextOnline);
			}
		}
		Enumeration<String> keyDisconnet = disconnectMap.keys();
		while (keyDisconnet.hasMoreElements()) {
			String nextkey = keyDisconnet.nextElement();
			Player nextOnline = PlayerMgr.getInstance().find(nextkey);
			if (nextOnline != null) {
				doCount(countMap, nextOnline);
			}

		}

		return countMap;
	}

	private static void doCount(Map<String, eBILogRegSubChannelToClientPlatForm> countMap, Player nextOnline) {
		UserDataMgr userDataMgr = nextOnline.getUserDataMgr();
		if (userDataMgr != null) {
			ZoneRegInfo zoneRegInfo = userDataMgr.getZoneRegInfo();
			ZoneLoginInfo zoneLoginInfo = nextOnline.getZoneLoginInfo();
			if (zoneRegInfo != null && zoneLoginInfo != null) {
				String regSubChannelId = zoneRegInfo.getRegSubChannelId();
				String clientPlayForm = zoneLoginInfo.getLoginClientPlatForm();
				if (StringUtils.isBlank(regSubChannelId)) {
					regSubChannelId = "-2";
				}
				if (StringUtils.isBlank(clientPlayForm)) {
					clientPlayForm = "-2";
				}

				String str = new StringBuffer().append(regSubChannelId).append("平台").append(clientPlayForm).toString();
				eBILogRegSubChannelToClientPlatForm newregsubtoclient = countMap.get(str);

				if (newregsubtoclient == null) {
					newregsubtoclient = new eBILogRegSubChannelToClientPlatForm();
					newregsubtoclient.setCount(new AtomicInteger(1));
					newregsubtoclient.setclientPlayForm(clientPlayForm);
					newregsubtoclient.setregSubChannelId(regSubChannelId);
					countMap.put(str, newregsubtoclient);
				} else {
					newregsubtoclient.getcount().incrementAndGet();
				}
			}
		}
	}

	/**
	 * 获取所有在线角色的id列表
	 * 
	 * @return
	 */
	public static Set<String> getOnlinePlayerIdSet() {
		return userChannelMap.keySet();
	}

	public static Long getDisconnectTime(String userId) {
		return disconnectMap.get(userId);
	}

	/**
	 * 提取登出玩家id列表
	 * 
	 * @return
	 */
	public static List<String> extractLogoutUserIdList() {
		ArrayList<String> disconnectUserIds = null;
		long current = System.currentTimeMillis();
		for (Iterator<Map.Entry<String, Long>> it = disconnectMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Long> entry = it.next();
			if (current - entry.getValue() < RECONNECT_TIME) {
				continue;
			}
			String userId = entry.getKey();
			if (disconnectMap.remove(userId) == null) {
				continue;
			}
			if (disconnectUserIds == null) {
				disconnectUserIds = new ArrayList<String>();
			}
			disconnectUserIds.add(userId);
		}
		return disconnectUserIds;
	}

	public static Set<String> getShortDisconnectIds() {
		return disconnectMap.keySet();
	}

	public static void KickOffPlayer(final ChannelHandlerContext oldContext, FsNettyControler nettyControler, String userId){
		GameLoginResponse.Builder loginResponse = GameLoginResponse.newBuilder();
		loginResponse.setResultType(eLoginResultType.SUCCESS);
		loginResponse.setError("你的账号在另一处登录，请重新登录");
		
		ChannelFuture f = nettyControler.sendAyncResponse(userId, oldContext, Command.MSG_PLAYER_OFF_LINE, loginResponse.build().toByteString());
		f.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				oldContext.executor().schedule(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						oldContext.close();
						return null;
					}
				}, 300, TimeUnit.MILLISECONDS);
			}
		});
		
	}
}
