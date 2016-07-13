package com.rw.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.rw.service.log.eLog.eBILogRegSubChannelToClientPlatForm;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;

public class UserChannelMgr {

	private static final AttributeKey<SessionInfo> USER_ID;
	private static final AttributeKey<SynDataInReqMgr> SYN_DATA;
	private static final long RECONNECT_TIME;
	private static final SessionInfo CLOSE_SESSION;
	private static ConcurrentHashMap<String, ChannelHandlerContext> userChannelMap;
	private static ConcurrentHashMap<String, Long> disconnectMap;
	private static AtomicLong seesionIdGenerator;

	static {
		USER_ID = AttributeKey.valueOf("userId");
		SYN_DATA = AttributeKey.valueOf("syn_data");
		RECONNECT_TIME = TimeUnit.MINUTES.toMillis(5);
		CLOSE_SESSION = new SessionInfo("close", 0);
		userChannelMap = new ConcurrentHashMap<String, ChannelHandlerContext>();
		disconnectMap = new ConcurrentHashMap<String, Long>();
		seesionIdGenerator = new AtomicLong();
	}

	private static void logger(SessionInfo oldSession) {
		if (oldSession == CLOSE_SESSION) {
			GameLog.info("UserChannelMgr", "#bindUserID", "bingding fail:session closed");
		} else if (oldSession != null) {
			GameLog.info("UserChannelMgr", "#bindUserID", "bingding fail:already bingding:" + oldSession);
		}
	}

	public static boolean bindUserID(String userId, ChannelHandlerContext ctx) {
		if (ctx == null || !StringUtils.isNotBlank(userId)) {
			return false;
		}
		GameLog.info("UserChannelMgr", "#bindUserID", "bingding connection:" + userId + "," + ctx);
		Attribute<SessionInfo> attrSession = ctx.channel().attr(USER_ID);
		SessionInfo oldSession = attrSession.get();
		if (oldSession != null) {
			logger(oldSession);
			return false;
		}
		SessionInfo newSession = new SessionInfo(userId, seesionIdGenerator.incrementAndGet());
		oldSession = attrSession.setIfAbsent(newSession);
		if (oldSession != null) {
			logger(oldSession);
			return false;
		}
		ChannelHandlerContext old = userChannelMap.put(userId, ctx);
		if (old != null) {
			// TODO 通过消息通知
//			old.close();
		}
		if (ctx.channel().attr(USER_ID) == CLOSE_SESSION) {
			userChannelMap.remove(userId, ctx);
			return false;
		}
		return true;
	}

	public static String getUserId(ChannelHandlerContext ctx) {
		Attribute<SessionInfo> userIdAttr = ctx.channel().attr(USER_ID);
		SessionInfo session = userIdAttr.get();
		return session == null ? null : session.getUserId();
	}

	public static SessionInfo getSession(ChannelHandlerContext ctx) {
		Attribute<SessionInfo> userIdAttr = ctx.channel().attr(USER_ID);
		return userIdAttr.get();
	}

	/**
	 * 获取当前玩家sessionId，还没登录返回-1
	 * 
	 * @param userId
	 * @return
	 */
	public static long getCurrentSessionId(String userId) {
		return getSessionId(userChannelMap.get(userId));
	}

	/**
	 * 获取指定{@link ChannelHandlerContext}的sessionId，指定ctx为null或者ctx还没绑定返回-1
	 * 
	 * @param userId
	 * @return
	 */
	public static long getSessionId(ChannelHandlerContext ctx) {
		if (ctx == null) {
			return -1;
		}
		Attribute<SessionInfo> attrSession = ctx.channel().attr(USER_ID);
		SessionInfo oldSession = attrSession.get();
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
		Attribute<SessionInfo> userIdAttr = ctx.channel().attr(USER_ID);
		for (;;) {
			SessionInfo oldSession = userIdAttr.get();
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

	public static String getCtxInfo(ChannelHandlerContext ctx) {
		Attribute<SessionInfo> userIdAttr = ctx.channel().attr(USER_ID);
		SessionInfo oldSession = userIdAttr.get();
		return oldSession == null ? "[not binding]" : oldSession.toString();
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

	/**
	 * 检查玩家是否重连
	 * 
	 * @param userId
	 * @return
	 */
	public static boolean clearAndCheckReconnect(String userId) {
		Long time = disconnectMap.remove(userId);
		return time != null && (System.currentTimeMillis() - time) < RECONNECT_TIME;
	}

	public static boolean checkReconnect(String userId) {
		Long time = disconnectMap.get(userId);
		return time != null && (System.currentTimeMillis() - time) < RECONNECT_TIME;
	}

	/**
	 * 清除断连时间
	 * 
	 * @param userId
	 */
	public static void clearDisConnectTime(String userId) {
		disconnectMap.remove(userId);
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
			it.remove();
			if (disconnectUserIds == null) {
				disconnectUserIds = new ArrayList<String>();
			}
			disconnectUserIds.add(entry.getKey());
		}
		return disconnectUserIds;
	}

	public static Set<String> getShortDisconnectIds() {
		return disconnectMap.keySet();
	}

}
