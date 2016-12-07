package com.rw.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import com.common.GameUtil;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserDataMgr;
import com.playerdata.dataSyn.SynDataInReqMgr;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.controler.FsNettyControler;
import com.rw.controler.PlayerMsgCache;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.common.PairValue;
import com.rw.fsutil.dao.cache.SimpleCache;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.eLog.eBILogRegSubChannelToClientPlatForm;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerPredecessor;
import com.rwproto.GameLoginProtos.GameLoginResponse;
import com.rwproto.GameLoginProtos.eLoginResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.RequestHeader;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;

public class UserChannelMgr {

	private static final AttributeKey<String> USER_ID;
	private static final AttributeKey<SynDataInReqMgr> SYN_DATA;
	public static final long RECONNECT_TIME;
	private static long msgHoldMillis;
	private static final String CLOSE_SESSION;
	private static ConcurrentHashMap<String, Long> userChannelsMap;
	private static ConcurrentHashMap<String, Long> disconnectMap;
	// 容量需要做成配置
	private static SimpleCache<String, PlayerMsgCache> msgCache;
	private static ConcurrentHashMap<Command, PairValue<Command, AtomicLong>> purgeStat;

	static {
		USER_ID = AttributeKey.valueOf("userId");
		SYN_DATA = AttributeKey.valueOf("syn_data");
		RECONNECT_TIME = TimeUnit.MINUTES.toMillis(5);
		CLOSE_SESSION = "CLOSE";
		msgHoldMillis = TimeUnit.MINUTES.toMillis(10);
		userChannelsMap = new ConcurrentHashMap<String, Long>();
		disconnectMap = new ConcurrentHashMap<String, Long>();
		// TODO config capacity
		msgCache = new SimpleCache<String, PlayerMsgCache>(2000);
		purgeStat = new ConcurrentHashMap<Command, PairValue<Command, AtomicLong>>();
	}

	private static void logger(String oldSession) {
		if (oldSession == CLOSE_SESSION) {
			GameLog.info("UserChannelMgr", "#bindUserID", "bingding fail:session closed");
		} else if (oldSession != null) {
			GameLog.info("UserChannelMgr", "#bindUserID", "bingding fail:already bingding:" + oldSession);
		}
	}

	/**
	 * 绑定{@link Channel}与UserId
	 * @param userId
	 * @param sessionId
	 * @param recordLogin
	 * @return
	 */
	public static boolean bindUserId(String userId, Long sessionId, boolean recordLogin) {
		if (sessionId == null) {
			return false;
		}
		ChannelHandlerContext ctx = ServerHandler.getChannelHandlerContext(sessionId);
		if (ctx == null) {
			GameLog.error("bindUserID", "", "bindUserID fail by disconnet");
			return false;
		}
		Channel channel = ctx.channel();
		GameLog.info("bindUserID", "", "bing connection:" + userId + "," + ServerHandler.getCtxInfo(ctx));
		Attribute<String> attrSession = channel.attr(USER_ID);
		String oldSession = attrSession.get();
		if (oldSession != null) {
			logger(oldSession);
			return false;
		}
		oldSession = attrSession.setIfAbsent(userId);
		if (oldSession != null) {
			logger(oldSession);
			return false;
		}
		Long oldSessionId = userChannelsMap.put(userId, sessionId);
		if (oldSessionId != null) {
			GameLog.info("UserChannelMgr", "", "replace binding sessionId:" + oldSessionId + "," + userId);
		}
		// 这里缺少多线程保护，会导致已经断线的人时间被清空，后果是无法直接在游戏内重连
		if (disconnectMap.remove(userId) == null && recordLogin && oldSessionId == null) {
			BILogMgr.getInstance().logZoneLogin(userId);
		}
		return true;
	}

	/**
	 * 接触{@link Channel}与UserId的绑定
	 * @param ctx
	 */
	public static void unbindUserId(final Long sessionId, ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		Attribute<String> userIdAttr = ctx.channel().attr(USER_ID);
		final String userId = userIdAttr.get();
		if (userId == CLOSE_SESSION) {
			doubleErrorLog("closeSession", "", "alreay remove userId:" + channel);
			return;
		}
		boolean closeResult = userIdAttr.compareAndSet(userId, CLOSE_SESSION);
		if (!closeResult) {
			doubleErrorLog("closeSession", "", "alreay remove userId:" + channel + "," + userIdAttr.get());
			return;
		}
		if (userId != null) {
			final long disConnectTime = DateUtils.getSecondLevelMillis();
			final String channelToString = ServerHandler.getCtxInfo(ctx);
			// 线程安全地完成userChannel绑定关系的移除和断线时间的记录
			GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerPredecessor() {

				@Override
				public void run(String e) {
					boolean result = userChannelsMap.remove(userId, sessionId);
					if (result) {
						disconnectMap.put(userId, disConnectTime);
					}
					GameLog.info("bindUserID", "", "unbind:" + sessionId + '-' + userId + ',' + result + ',' + channelToString);
				}
			});
		}
	}

	private static void doubleErrorLog(String module, String id, String errorReason) {
		GameLog.error(module, id, errorReason);
		FSUtilLogger.error(errorReason);
	}

	public static String getUserId(ChannelHandlerContext ctx) {
		String userId = ctx.channel().attr(USER_ID).get();
		if (userId != CLOSE_SESSION) {
			return userId;
		} else {
			return null;
		}
	}

	public static String getBoundUserId(ChannelHandlerContext ctx) {
		return ctx.channel().attr(USER_ID).get();
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
		ChannelHandlerContext ctx = getChannelHandlerContext(userId);
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

	public static ChannelHandlerContext getChannelHandlerContext(String userId) {
		Long sessionId = userChannelsMap.get(userId);
		if (sessionId == null) {
			return null;
		}
		return ServerHandler.getChannelHandlerContext(sessionId);
	}

	public static Long getSessionId(String userId) {
		return userChannelsMap.get(userId);
	}

	/**
	 * 检测指定userId处于否连接中
	 * @param userId
	 * @return
	 */
	public static boolean isConnecting(String userId) {
		return userChannelsMap.containsKey(userId);
	}

	public static ByteString getDataOnBSEnd(String userId, Object recordKey) {
		ChannelHandlerContext ctx = getChannelHandlerContext(userId);
		if (ctx == null) {
			return null;
		}

		SynDataInReqMgr synData = getSynDataInReqMgr(ctx);
		if (synData == null) {
			return null;
		}
		return synData.getSynData(ctx, userId, recordKey);
	}

	/**
	 * 踢出玩家移除连接数，不主动断开连接
	 * 
	 * @param userId
	 */
	public static void kickoffDisconnect(String userId) {
		Long sessionId = userChannelsMap.get(userId);
		if (sessionId == null) {
			return;
		}
		GameLog.info("UserChannelMgr", "#kickoffDisconnect", "消息跟踪@移除ctx:" + userId + "," + ServerHandler.getCtxInfo(sessionId));
	}

	/**
	 * 获取在线列表
	 * 
	 * @return
	 */
	public static List<String> getOnlineList() {
		return new ArrayList<String>(userChannelsMap.keySet());
	}

	public static int getCount() {
		return userChannelsMap.size();
	}

	public static Map<String, eBILogRegSubChannelToClientPlatForm> getSubChannelCount() {
		Map<String, eBILogRegSubChannelToClientPlatForm> countMap = new HashMap<String, eBILogRegSubChannelToClientPlatForm>();
		Enumeration<String> keys1 = userChannelsMap.keys();
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
			ZoneLoginInfo zoneLoginInfo = userDataMgr.getZoneLoginInfo();
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
		return userChannelsMap.keySet();
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

	public static void KickOffPlayer(final Long oldSessionId, FsNettyControler nettyControler, String userId) {
		GameLoginResponse.Builder loginResponse = GameLoginResponse.newBuilder();
		loginResponse.setResultType(eLoginResultType.SUCCESS);
		loginResponse.setError("你的账号在另一处登录，请重新登录");

		ChannelFuture future = sendAyncResponse(userId, oldSessionId, Command.MSG_PLAYER_OFF_LINE, null, loginResponse.build().toByteString());
		if (future == null) {
			return;
		}
		future.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				ChannelHandlerContext oldContext = ServerHandler.getChannelHandlerContext(oldSessionId);
				if (oldContext == null) {
					return;
				}
				oldContext.executor().schedule(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						ChannelHandlerContext oldContext_ = ServerHandler.getChannelHandlerContext(oldSessionId);
						if (oldContext_ != null) {
							oldContext_.close();
						}
						return null;
					}
				}, 300, TimeUnit.MILLISECONDS);
			}
		});

	}

	/**
	 * 对在线玩家广播消息
	 * @param command		
	 * @param subCommand	
	 * @param byteString	
	 */
	public static void broadcastMsg(Command command, Object subCommand, ByteString byteString) {
		for (Entry<String, Long> entry : userChannelsMap.entrySet()) {
			Long sessionId = entry.getValue();
			sendAyncResponse(null, sessionId, command, subCommand, byteString);
		}
	}

	/**
	 * 对在线玩家广播消息
	 * @param command		
	 * @param subCommand	
	 * @param byteString	
	 */
	public static void broadcastMsgForMainMsg(Command command, Object subCommand, ByteString byteString) {
		CfgOpenLevelLimitDAO limitDAO = CfgOpenLevelLimitDAO.getInstance();
		for (Entry<String, Long> entry : userChannelsMap.entrySet()) {
			Long sessionId = entry.getValue();
			String userId = entry.getKey();
			Hero hero = FSHeroMgr.getInstance().getMainRoleHero(userId);
			if (hero == null) {
				continue;
			}
			CfgOpenLevelLimit cfg = limitDAO.getCfgById(eOpenLevelType.MainMsg.getOrderString());
			if (cfg != null) {
				int level = hero.getLevel();
				if (level < cfg.getMinLevel() || level > cfg.getMaxLevel()) {
					continue;
				}
			}
			sendAyncResponse(null, sessionId, command, subCommand, byteString);
		}
	}

	public static void sendErrorResponse(String userId, RequestHeader header, Object subCmd, int exceptionCode) {
		Long sessionId = userChannelsMap.get(userId);
		sendSyncResponse(userId, header, subCmd, null, exceptionCode, sessionId, null);
	}

	public static ChannelFuture sendSyncResponse(String userId, RequestHeader header, Object subCmd, ByteString resultContent, Long sessionId, ByteString synData) {
		return sendSyncResponse(userId, header, subCmd, resultContent, 200, sessionId, synData);
	}

	/**
	 * 发送同步消息，但不会进行记录，由{@link RequestHeader}构造
	 * @param header
	 * @param resultContent
	 * @param sessionId
	 */
	public static void sendSyncResponse(RequestHeader header, Object subCmd, ByteString resultContent, Long sessionId) {
		sendSyncResponse(null, header, subCmd, resultContent, 200, sessionId, null);
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
	public static ChannelFuture sendAyncResponse(String userId, Long sessionId, Command Cmd, Object subCommand, ByteString pBuffer) {
		if (sessionId == null) {
			return null;
		}
		ChannelHandlerContext ctx = ServerHandler.getChannelHandlerContext(sessionId);
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
		ChannelFuture future = ctx.channel().writeAndFlush(response);
		future.addListener(new MsgSendTimesListener(Cmd, subCommand, response));
		return future;
	}

	/**
	 * <pre>
	 * 发送异步消息，若指定userId的玩家不在线，不会执行发送操作(发送失败)
	 * 对
	 * {@link #sendAyncResponse(String, ChannelHandlerContext, Command, ByteString)}的简单封装
	 * 
	 * <pre>
	 * @param userId
	 * @param Cmd
	 * @param pBuffer
	 * @return
	 */
	public static ChannelFuture sendAyncResponse(String userId, Command Cmd, Object subCmd, ByteString pBuffer) {
		Long sessionId = userChannelsMap.get(userId);
		return sendAyncResponse(userId, sessionId, Cmd, subCmd, pBuffer);
	}

	public static ChannelFuture sendSyncResponse(String userId, RequestHeader header, Object subCmd, ByteString resultContent, int statusCode, Long sessionId, ByteString synData) {
		ChannelHandlerContext ctx;
		if (sessionId == null) {
			ctx = null;
		} else {
			ctx = ServerHandler.getChannelHandlerContext(sessionId);
		}
		return sendResponse(userId, header, subCmd, resultContent, statusCode, ctx, synData);
	}

	/**
	 * 发送消息回应道指定的{@link ChannelHandlerContext}
	 * 若userId不会null，会对消息进行记录，用于玩家断线重连时重发
	 * @param userId
	 * @param header
	 * @param resultContent
	 * @param statusCode
	 * @param ctx
	 * @param synData
	 * @return
	 */
	public static ChannelFuture sendResponse(String userId, RequestHeader header, Object subCmd, ByteString resultContent, int statusCode, ChannelHandlerContext ctx, ByteString synData) {
		boolean sendMsg = ctx != null;
		boolean saveMsg = userId != null;
		if (!sendMsg && !saveMsg) {
			return null;
		}
		// build header
		String token = header.getToken();
		int seqId = header.getSeqID();
		Command command = header.getCommand();
		ResponseHeader.Builder headerBuilder = ResponseHeader.newBuilder().setSeqID(seqId).setToken(token).setCommand(command).setStatusCode(statusCode);
		if (synData != null) {
			headerBuilder.setSynData(synData);
		}
		ResponseHeader responseHeader = headerBuilder.build();
		// build response
		Response.Builder builder = Response.newBuilder().setHeader(responseHeader);
		if (resultContent != null) {
			builder.setSerializedContent(resultContent);
		} else {
			builder.setSerializedContent(ByteString.EMPTY);
		}
		Response result = builder.build();
		if (!GameUtil.checkMsgSize(result)) {
			return null;
		}
		if (saveMsg && resultContent != null) {
			addResponse(userId, command, seqId, resultContent);
		}
		if (sendMsg) {
			ChannelFuture future = ctx.channel().writeAndFlush(result);
			future.addListener(new MsgSendTimesListener(command, subCmd, result));
			GameLog.debug("#发送消息" + "  " + command + "  size:" + result.getSerializedContent().size());
			return future;
		} else {
			return null;
		}
	}

	public static void addResponse(String userId, Command command, int seqId, ByteString response) {
		if (seqId == 0) {
			return;
		}
		PlayerMsgCache msg = msgCache.get(userId);
		if (msg == null) {
			// 消息容量也需要做成配置
			msg = new PlayerMsgCache(10, purgeStat);
			PlayerMsgCache old = msgCache.putIfAbsent(userId, msg);
			if (old != null) {
				msg = old;
			}
		}
		msg.add(command, seqId, response);
	}

	public static ByteString getResponse(String userId, int seqId) {
		PlayerMsgCache msg = msgCache.get(userId);
		if (msg == null) {
			return null;
		}
		return msg.getResponse(seqId);
	}

	public static void clearMsgCache(String userId) {
		PlayerMsgCache msg = msgCache.get(userId);
		if (msg == null) {
			return;
		}
		msg.clear();
	}

	public static void purgeMsgRecord() {
		long purgeTime = DateUtils.getSecondLevelMillis() - msgHoldMillis;
		List<PlayerMsgCache> msgCaches = msgCache.values();
		for (int i = msgCaches.size(); --i >= 0;) {
			msgCaches.get(i).purge(purgeTime);
		}
	}

	public static Enumeration<PairValue<Command, AtomicLong>> getPurgeCount() {
		return purgeStat.elements();
	}
	
	public static boolean isLogout(String userId){
		return !userChannelsMap.containsKey(userId)&&!disconnectMap.containsKey(userId);
	}
}
