package com.rw.controler;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.UserChannelMgr;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.login.game.LoginSynDataHelper;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.GameLoginProtos.GameLoginResponse;
import com.rwproto.GameLoginProtos.eGameLoginType;
import com.rwproto.GameLoginProtos.eLoginResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.RequestHeader;
import com.rwproto.ResponseProtos.ResponseHeader;

public class PlayerLoginTask implements PlayerTask {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");
	private final ChannelHandlerContext ctx;
	private final GameLoginRequest request;
	private final RequestHeader header;
	private final boolean savePlot;

	public PlayerLoginTask(ChannelHandlerContext ctx, RequestHeader header, GameLoginRequest request) {
		this(ctx, header, request, true);
	}

	public PlayerLoginTask(ChannelHandlerContext ctx, RequestHeader header, GameLoginRequest request, boolean savePlot) {
		this.ctx = ctx;
		this.header = header;
		this.request = request;
		this.savePlot = savePlot;
	}

	@Override
	public void run(Player player) {
		GameLoginResponse.Builder response = GameLoginResponse.newBuilder();
		if (player == null) {
			response.setError("服务器繁忙，请稍后再次尝试登录。");
			response.setResultType(eLoginResultType.FAIL);
			nettyControler.sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		String userId = player.getUserId();
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
			nettyControler.sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		if (user.isInKickOffCoolTime()) {
			response.setError("亲爱的用户，抱歉你已被强制下线，请5分钟后再次尝试登录。");
			response.setResultType(eLoginResultType.FAIL);
			nettyControler.sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		if (clientInfo != null) {
			user.setChannelId(clientInfo.getChannelId());
		}

		if (player != null) {
			// 断开非当前链接
			final ChannelHandlerContext oldContext = UserChannelMgr.get(userId);
			if (oldContext != null && oldContext != ctx) {
				GameLog.debug("Kick Player...,userId:" + userId);
//				player.KickOff("你的账号在另一处登录，请重新登录");
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
		}else{
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

		long lastLoginTime = player.getUserGameDataMgr().getLastLoginTime();
		UserChannelMgr.bindUserID(userId, ctx);
		// 通知玩家登录，Player onLogin太乱，方法后面需要整理
		player.onLogin();
		if (StringUtils.isBlank(player.getUserName())) {
			response.setResultType(eLoginResultType.NO_ROLE);
			GameLog.debug("Create Role ...,userId:" + userId);
		} else {
			response.setResultType(eLoginResultType.SUCCESS);
			GameLog.debug("Login Success ...,userId:" + userId);
		}
		response.setUserId(userId);
		GameLog.debug("Game Login Finish --> accountId:" + accountId + ",zoneId:" + zoneId + ",userId:" + userId);
		player.setZoneLoginInfo(zoneLoginInfo);
//		BILogMgr.getInstance().logZoneLogin(player);
		
		// 判断需要用到最后次登陆 时间。保存在活动内而不是player
		UserEventMgr.getInstance().RoleLogin(player, lastLoginTime);

		// 补充进入主城需要同步的数据
		LoginSynDataHelper.setData(player, response);
		// clear操作有风险
		nettyControler.clearMsgCache(userId);
		nettyControler.sendResponse(userId, header, response.build().toByteString(), ctx);
	}
	

}
