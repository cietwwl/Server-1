package com.rw.service.login.game;

import io.netty.channel.ChannelHandlerContext;

import com.alibaba.druid.pool.DruidDataSource;
import com.bm.login.AccoutBM;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.controler.FsNettyControler;
import com.rw.controler.PlayerCreateTask;
import com.rw.controler.PlayerLoginTask;
import com.rw.fsutil.cacheDao.IdentityIdGenerator;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rwbase.dao.user.UserIdCache;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.user.accountInfo.UserZoneInfo;
import com.rwbase.dao.user.loginInfo.TableAccountLoginRecord;
import com.rwbase.dao.user.loginInfo.TableAccountLoginRecordDAO;
import com.rwbase.dao.version.VersionConfigDAO;
import com.rwbase.dao.version.pojo.VersionConfig;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.GameLoginProtos.GameLoginResponse;
import com.rwproto.GameLoginProtos.eLoginResultType;
import com.rwproto.RequestProtos.RequestHeader;

public class GameLoginHandler {

	// 全服唯一id的生成器，完整的userId完成是serverId + generateId
	private final IdentityIdGenerator generator;
	private final UserIdCache userIdCache;
	private FsNettyControler nettyControler;

	public GameLoginHandler() {
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		if (dataSource == null) {
			throw new ExceptionInInitializerError("获取dataSource失败");
		}
		this.generator = new IdentityIdGenerator("user_identifier", dataSource);
		this.userIdCache = new UserIdCache(dataSource);
	}

	public void gameServerLogin(GameLoginRequest request, ChannelHandlerContext ctx, RequestHeader header) {
		final GameLoginResponse.Builder response = GameLoginResponse.newBuilder();
		if (GameManager.isShutdownHook) {
			response.setError("停服维护中");
			response.setResultType(eLoginResultType.FAIL);
			sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		if (GameManager.isOnlineLimit()) {
			response.setError("该区人气火爆，请稍后尝试，或者选择推荐新区。");
			response.setResultType(eLoginResultType.ServerMainTain);
			sendResponse(header, response.build().toByteString(), ctx);
			return;
		}

		final String accountId = request.getAccountId();
		final int zoneId = request.getZoneId();
		// 检测白名单 by lida
		if (GameManager.isWhiteListLimit(accountId)) {
			response.setError("该区维护中，请稍后尝试，");
			response.setResultType(eLoginResultType.ServerMainTain);
			sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		GameLog.debug("Game Login Start --> accountId:" + accountId + ",zoneId:" + zoneId);
		TableAccount userAccount = AccoutBM.getInstance().getByAccountId(accountId);
		if (userAccount == null) {
			response.setResultType(eLoginResultType.FAIL);
			response.setError("账号不存在");
			sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		String userId = userIdCache.getUserId(accountId, zoneId);
		if (userId == null) {
			response.setResultType(eLoginResultType.NO_ROLE);
			GameLog.debug("Create Role ...,accountId:" + accountId + " zoneId:" + zoneId);
			response.setVersion(((VersionConfig) VersionConfigDAO.getInstance().getCfgById("version")).getValue());
			sendResponse(header, response.build().toByteString(), ctx);
		} else {
			// 线程安全地执行角色登录操作
			GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerLoginTask(ctx, header, request));
		}
	}

	private ByteString createLoginResponse(String error, eLoginResultType type) {
		GameLoginResponse.Builder response = GameLoginResponse.newBuilder();
		response.setError(error);
		response.setResultType(type);
		return response.build().toByteString();
	}

	public void createRoleAndLogin(GameLoginRequest request, ChannelHandlerContext ctx, RequestHeader header) {
		if (GameManager.isShutdownHook) {
			sendResponse(header, createLoginResponse("停服维护中", eLoginResultType.FAIL), ctx);
			return;
		}

		final String accountId = request.getAccountId();
		final int zoneId = request.getZoneId();
		if (GameManager.isWhiteListLimit(accountId)) {
			sendResponse(header, createLoginResponse("该区维护中，请稍后尝试，", eLoginResultType.ServerMainTain), ctx);
			return;
		}
		GameLog.debug("Game Create Role Start --> accountId:" + accountId + " , zoneId:" + zoneId);
		GameWorldFactory.getGameWorld().executeAccountTask(accountId, new PlayerCreateTask(request, header, ctx, generator));
	}

	public void addUserZoneInfo(int zoneId, UserZoneInfo ZoneInfo, Player player) {
		ZoneInfo.setZoneId(zoneId);
		ZoneInfo.setLevel(player.getLevel());
		ZoneInfo.setVipLevel(player.getVip());
		ZoneInfo.setHeadImage(player.getHeadImage());
		ZoneInfo.setLastLoginMillis(player.getUserGameDataMgr().getLastLoginTime());
		ZoneInfo.setCareer(player.getCareer());
		ZoneInfo.setUserName(player.getUserName());
	}

	public void notifyPlatformPlayerLogin(int zoneId, String accountId, Player player) {
		TableAccountLoginRecord record = TableAccountLoginRecordDAO.getInstance().get(accountId);
		boolean blnInsert = false;
		if (record == null) {
			record = new TableAccountLoginRecord();
			blnInsert = true;
		}
		record.setZoneId(zoneId);
		record.setAccountId(accountId);
		record.setUserId(player.getUserId());
		record.setLoginTime(System.currentTimeMillis());
		TableAccountLoginRecordDAO.getInstance().update(record, blnInsert);
	}

	/**
	 * 通过账号与区获取角色ID
	 * 
	 * @param accountId
	 * @param zoneId
	 * @return
	 */
	public String getUserId(String accountId, int zoneId) {
		return userIdCache.getUserId(accountId, zoneId);
	}

	public void sendResponse(RequestHeader header, ByteString resultContent, ChannelHandlerContext ctx) {
		if (this.nettyControler == null) {
			nettyControler = SpringContextUtil.getBean("fsNettyControler");
		}
		nettyControler.sendResponse(header, resultContent, ctx);
	}

}
