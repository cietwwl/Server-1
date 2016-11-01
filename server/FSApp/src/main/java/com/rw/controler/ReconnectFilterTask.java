package com.rw.controler;

import com.log.FSTraceLogger;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.ServerHandler;
import com.rw.netty.UserChannelMgr;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.ReConnectionProtos.ReConnectRequest;
import com.rwproto.ReConnectionProtos.ReConnectResponse;
import com.rwproto.ReConnectionProtos.ReConnectResultType;
import com.rwproto.RequestProtos.Request;

public class ReconnectFilterTask implements Runnable {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private Request request;
	private ReConnectRequest reconnectRequest;
	private Long sessionId;

	public ReconnectFilterTask(Request request, ReConnectRequest reconnectRequest, Long sessionId) {
		super();
		this.request = request;
		this.reconnectRequest = reconnectRequest;
		this.sessionId = sessionId;
	}

	@Override
	public void run() {
		ReConnectResponse.Builder responseBuilder = ReConnectResponse.newBuilder();
		String accountId = reconnectRequest.getAccountId();
		int zoneId = reconnectRequest.getZoneId();
		// 如果获取ID失败，直接退出登录界面，因为创建角色与处理账号重连是互斥的
		String userId = nettyControler.getGameLoginHandler().getUserId(accountId, zoneId);
		if (userId == null) {
			GameLog.error("FsNettyControler", "#ReConnect()", "find userId fail on reconnecting:" + accountId + "," + zoneId);
			reLoginGame(responseBuilder);
			return;
		}
		User user = UserDataDao.getInstance().getByUserId(userId);
		if (user == null) {
			GameLog.error("PlayerReconnectTask", "#ReConnect()", "get user fail on reconnecting:" + userId);
			reLoginGame(responseBuilder);
			return;
		}
		if (user.isBlocked()) {
			GameLog.info("FsNettyControler", "#ReConnect()", "ReConnect 用户封号中 ，userId:" + user.getUserId() + "," + zoneId);
			reLoginGame(responseBuilder);
			return;
		}
		if (user.isInKickOffCoolTime()) {
			GameLog.info("FsNettyControler", "#ReConnect()", "ReConnect 用户踢出冷却中 ，userId:" + user.getUserId() + "," + zoneId);
			reLoginGame(responseBuilder);
			return;
		}

		Player player = PlayerMgr.getInstance().findPlayerFromMemory(userId);
		if (player == null) {
			reLoginGame(responseBuilder);
			return;
		}

		Long disconnectTime = UserChannelMgr.getDisconnectTime(userId);
		if (disconnectTime != null && (System.currentTimeMillis() - disconnectTime) > UserChannelMgr.RECONNECT_TIME) {
			reLoginGame(responseBuilder);
			return;
		}
		// 真正处理逻辑
		FSTraceLogger.logger("reconnect(" + request.getHeader().getSeqID() + ")" + ServerHandler.getCtxInfo(sessionId, false));
		GameWorldFactory.getGameWorld().asyncExecute(userId, new ReconnectSecondaryTreatment(request, sessionId, reconnectRequest, userId));
	}

	private void reLoginGame(ReConnectResponse.Builder builder) {
		returnReconnectRequest(builder, request, ReConnectResultType.RETURN_GAME_LOGIN);
	}

	private void returnReconnectRequest(ReConnectResponse.Builder b, Request request, ReConnectResultType resultType) {
		b.setResultType(resultType);
		UserChannelMgr.sendSyncResponse(null, request.getHeader(), resultType, b.build().toByteString(), sessionId, null);
	}
}
