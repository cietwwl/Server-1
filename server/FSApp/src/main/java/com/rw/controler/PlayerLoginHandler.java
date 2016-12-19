package com.rw.controler;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.log.FSTraceLogger;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.MsgResultType;
import com.rw.netty.ServerHandler;
import com.rw.netty.UserChannelMgr;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.login.game.LoginSynDataHelper;
import com.rw.service.redpoint.RedPointManager;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.GameLoginProtos.GameLoginResponse;
import com.rwproto.GameLoginProtos.eGameLoginType;
import com.rwproto.GameLoginProtos.eLoginResultType;
import com.rwproto.RequestProtos.RequestHeader;

public class PlayerLoginHandler {

	private static PlayerLoginHandler handler = new PlayerLoginHandler();
	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private static String LOGIN = "LOGIN";

	public static PlayerLoginHandler getHandler() {
		return handler;
	}

	public void run(Player player, Long sessionId, RequestHeader header, GameLoginRequest request, boolean savePlot, final long submitTime) {
		if (!ServerHandler.isConnecting(sessionId)) {
			GameLog.error("PlayerLoginTask", player.getUserId(), "login fail by disconnect:" + sessionId);
			return;
		}
		final int seqID = header.getSeqID();
		final long executeTime = System.currentTimeMillis();
		FSTraceLogger.logger("run", executeTime - submitTime, "LOGIN_DELAY", seqID, player != null ? player.getUserId() : null, null, true);
		GameLoginResponse.Builder response = GameLoginResponse.newBuilder();
		if (player == null) {
			response.setError("服务器繁忙，请稍后再次尝试登录。");
			response.setResultType(eLoginResultType.FAIL);
			UserChannelMgr.sendSyncResponse(header, MsgResultType.NO_PLAYER, response.build().toByteString(), sessionId);
			return;
		}
		final String userId = player.getUserId();
		String clientInfoJson = request.getClientInfoJson();
		ZoneLoginInfo zoneLoginInfo = null;
		ClientInfo clientInfo = null;
		if (StringUtils.isNotBlank(clientInfoJson)) {
			clientInfo = ClientInfo.fromJson(clientInfoJson);
			zoneLoginInfo = ZoneLoginInfo.fromClientInfo(clientInfo);
		}
		User user = UserDataDao.getInstance().getByUserId(userId);
		if (user.isBlocked()) {
			String error = "亲爱的用户，抱歉你已被封号。请联系我们的客服。";
			if (user.getBlockReason() != null) {
				error = user.getBlockReason();
			}
			error = "封号原因:" + error;
			long blockCoolTime = user.getBlockCoolTime();
			String releaseTime;
			if (blockCoolTime > 0) {
				releaseTime = "解封时间:" + DateUtils.getDateTimeFormatString(blockCoolTime, "yyyy-MM-dd HH:mm");
			} else {
				releaseTime = "解封时间:永久封号!";
			}
			response.setError(error + "\n" + releaseTime);
			response.setResultType(eLoginResultType.FAIL);
			UserChannelMgr.sendSyncResponse(header, MsgResultType.ACCOUNT_BLOCK, response.build().toByteString(), sessionId);
			return;
		}
		if (user.isInKickOffCoolTime()) {
			response.setError("亲爱的用户，抱歉你已被强制下线，请5分钟后再次尝试登录。");
			response.setResultType(eLoginResultType.FAIL);
			UserChannelMgr.sendSyncResponse(header, MsgResultType.KICK_OFF, response.build().toByteString(), sessionId);
			return;
		}
		if (clientInfo != null) {
			user.setChannelId(clientInfo.getChannelId());
		}

		if (player != null) {
			// 断开非当前链接
			Long oldSessionId = UserChannelMgr.getSessionId(userId);
			if (oldSessionId != null && oldSessionId.longValue() != sessionId.longValue()) {
				FSTraceLogger.logger("displace", 0, "DISPLACE", seqID, userId, null, false);
				UserChannelMgr.KickOffPlayer(oldSessionId, nettyControler, userId);
			}
		}
		// 检查发送版本更新
		if (clientInfo != null) {
			player.getUpgradeMgr().doCheckUpgrade(clientInfo.getClientVersion());
		}
		// TODO HC @Modify 2015-12-17
		/**
		 * <pre>
		 * 序章特殊剧情，当我创建完角色之后，登录数据推送完毕，我就直接把剧情设置一个假想值
		 * 保证不管角色当前是故意退出游戏跳过剧情，或者是出现意外退出，在下次进来都不会有剧情的重复问题
		 * </pre>
		 */
		if (savePlot) {
			PlotProgressDAO dao = PlotProgressDAO.getInstance();
			UserPlotProgress userPlotProgress = dao.get(player.getUserId());
			if (userPlotProgress == null) {
				userPlotProgress = new UserPlotProgress();
				userPlotProgress.setUserId(userId);
			}
			Integer hasValue = userPlotProgress.getProgressMap().putIfAbsent("0", -1);
			if (hasValue == null) {
				dao.update(userPlotProgress);
			}
			response.setLoginType(eGameLoginType.GAME_LOGIN);
		} else {
			response.setLoginType(eGameLoginType.CREATE_ROLE);
		}
		long createTime = user.getCreateTime();
		response.setCreateTime(createTime);

		final Player p = player;
		final int zoneId = request.getZoneId();
		final String accountId = request.getAccountId();
		GameWorldFactory.getGameWorld().asynExecute(new Runnable() {

			@Override
			public void run() {
				// author:lida 2015-09-21 通知登陆服务器更新账号信息
				nettyControler.getGameLoginHandler().notifyPlatformPlayerLogin(zoneId, accountId, p);
			}
		});

		long lastLoginTime = player.getLastLoginTime();
		user.setZoneLoginInfo(zoneLoginInfo);
		UserChannelMgr.bindUserId(userId, sessionId, true);
		// 通知玩家登录，Player onLogin太乱，方法后面需要整理
		ByteString loginSynData = player.onLogin(LOGIN);
		if (StringUtils.isBlank(player.getUserName())) {
			response.setResultType(eLoginResultType.NO_ROLE);
			GameLog.debug("Create Role ...,userId:" + userId);
		} else {
			response.setResultType(eLoginResultType.SUCCESS);
			GameLog.debug("Login Success ...,userId:" + userId);
		}
		response.setUserId(userId);
		GameLog.debug("Game Login Finish --> accountId:" + accountId + ",zoneId:" + zoneId + ",userId:" + userId);

		// 补充进入主城需要同步的数据
		LoginSynDataHelper.setData(player, response);

		// clear操作有风险
		UserChannelMgr.clearMsgCache(userId);
		FSTraceLogger.logger("run end", System.currentTimeMillis() - executeTime, LOGIN, seqID, userId, null, true);

		ChannelFuture future = UserChannelMgr.sendSyncResponse(userId, header, null, response.build().toByteString(), sessionId, loginSynData);
		if (future == null) {
			return;
		}
		// 判断需要用到最后次登陆 时间。保存在活动内而不是player;和future依赖关系
		UserEventMgr.getInstance().RoleLogin(player, lastLoginTime);
		// 触发红点
		int redPointVersion = header.getRedpointVersion();
		if (redPointVersion >= 0) {
			RedPointManager.getRedPointManager().checkRedPointVersion(player, redPointVersion);
		}

		future.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				long current = System.currentTimeMillis();
				FSTraceLogger.loggerSendAndSubmit("send", current - submitTime, current - executeTime, LOGIN, null, seqID, userId, null);
			}
		});

	}
}
