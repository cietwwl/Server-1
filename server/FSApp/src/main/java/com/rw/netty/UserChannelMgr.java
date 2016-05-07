package com.rw.netty;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserDataMgr;
import com.rw.service.log.eLog.eBILogRegSubChannelToClientPlatForm;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;

public class UserChannelMgr {

	private static ConcurrentHashMap<ChannelHandlerContext, String> userChannelMap;
	private static ConcurrentHashMap<String, ChannelHandlerContext> userChannelMapCpy;
	private static ConcurrentHashMap<String, Long> disconnectMap;
	private static final long RECONNECT_TIME = TimeUnit.MINUTES.toMillis(5);

	static {
		userChannelMap = new ConcurrentHashMap<ChannelHandlerContext, String>();
		userChannelMapCpy = new ConcurrentHashMap<String, ChannelHandlerContext>();
		disconnectMap = new ConcurrentHashMap<String, Long>();
	}

	private final static ThreadLocal<ChannelHandlerContext> CTX_THREADLOCAL = new ThreadLocal<ChannelHandlerContext>();

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
		GameLog.error("绑定ctx:" + userId + "," + ctx);
		System.err.println("绑定ctx:" + userId + "," + ctx);
		if (ctx != null && StringUtils.isNotBlank(userId)) {
			userChannelMap.put(ctx, userId);
			userChannelMapCpy.put(userId, ctx);
		}
	}

	public static void bindUserID(String userId, ChannelHandlerContext ctx) {
		GameLog.error("绑定ctx:" + userId + "," + ctx);
		System.err.println("绑定ctx:" + userId + "," + ctx);
		if (ctx != null && StringUtils.isNotBlank(userId)) {
			userChannelMap.put(ctx, userId);
			userChannelMapCpy.put(userId, ctx);
		}
	}

	public static String getUserId() {
		ChannelHandlerContext ctx = CTX_THREADLOCAL.get();
		if (ctx != null) {
			return userChannelMap.get(ctx);
		}
		return null;
	}

	public static void disConnect(String userId) {
		ChannelHandlerContext ctx = UserChannelMgr.get(userId);
		if (ctx != null) {
			ctx.close();
			remove(ctx);
		}
	}

	public static void remove(ChannelHandlerContext ctx) {
		// GameLog.error("消息跟踪@移除ctx:"+ctx);
		// System.err.println("消息跟踪@移除ctx:"+ctx);
		String userId = userChannelMap.remove(ctx);
		if (userId != null) {
			GameLog.error("消息跟踪@移除ctx:" + userId + "," + ctx);
			userChannelMapCpy.remove(userId, ctx);
		}
	}

	/**
	 * 踢出玩家移除连接数，不主动断开连接
	 * 
	 * @param userId
	 */
	public static void kickoffDisconnect(String userId) {
		ChannelHandlerContext ctx = UserChannelMgr.get(userId);
		GameLog.error("消息跟踪@移除ctx:" + userId + "," + ctx);
		userChannelMapCpy.remove(userId, ctx);
	}

	public static void exit(ChannelHandlerContext ctx) {
		// modify by Jamaz修正错误移除其他ctx的问题
		String userId = userChannelMap.remove(ctx);
		if (userId != null) {
			boolean removeResult = userChannelMapCpy.remove(userId, ctx);
			disconnectMap.put(userId, System.currentTimeMillis());
			System.out.println("连接关闭移除(" + (removeResult ? "成功" : "失败") + ") = " + userId + "," + ctx);
		}
	}

	public static ChannelHandlerContext get(String userId) {
		if (StringUtils.isNotBlank(userId) && userChannelMap.containsValue(userId)) {
			return userChannelMapCpy.get(userId);
		}
		return null;
	}

	public static int getCount() {

		return userChannelMapCpy.size();
	}

	public static Map<String, eBILogRegSubChannelToClientPlatForm> getSubChannelCount() {

		Map<String, eBILogRegSubChannelToClientPlatForm> countMap = new HashMap<String, eBILogRegSubChannelToClientPlatForm>();
		Enumeration<String> keys1 = userChannelMapCpy.keys();
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
		return userChannelMapCpy.keySet();
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

	public static Set<String> getShortDisconnectIds(){
		return disconnectMap.keySet();
	}

}
