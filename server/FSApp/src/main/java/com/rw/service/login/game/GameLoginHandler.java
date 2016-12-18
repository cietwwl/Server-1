package com.rw.service.login.game;

import io.netty.channel.ChannelHandlerContext;

import com.alibaba.druid.pool.DruidDataSource;
import com.bm.group.GroupBM;
import com.bm.login.AccoutBM;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.controler.PlayerCreateTask;
import com.rw.controler.PlayerLoginTask;
import com.rw.fsutil.cacheDao.IdentityIdGenerator;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rw.netty.MsgResultType;
import com.rw.netty.ServerHandler;
import com.rw.netty.UserChannelMgr;
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
	private final LoginProdecessor loginProdecessor;

	public GameLoginHandler() {
		String mainDsName = DataAccessStaticSupport.getMainDataSourceName();
		DruidDataSource dataSource = SpringContextUtil.getBean(mainDsName);
		if (dataSource == null) {
			throw new ExceptionInInitializerError("获取dataSource失败");
		}
		this.generator = new IdentityIdGenerator("user_identifier", dataSource);
		this.userIdCache = new UserIdCache(mainDsName, dataSource);
		GroupBM.init(mainDsName, dataSource);
		this.loginProdecessor = new LoginProdecessor();
	}

	public void gameServerLogin(GameLoginRequest request, ChannelHandlerContext ctx, RequestHeader header) {
		final GameLoginResponse.Builder response = GameLoginResponse.newBuilder();
		if (GameManager.isShutdownHook) {
			response.setError("停服维护中");
			response.setResultType(eLoginResultType.FAIL);
			UserChannelMgr.sendResponse(null, header, MsgResultType.SHUTDOWN, response.build().toByteString(), 200, ctx, null);
			return;
		}
		if (GameManager.isOnlineLimit()) {
			response.setError("该区人气火爆，请稍后尝试，或者选择推荐新区。");
			response.setResultType(eLoginResultType.ServerMainTain);
			UserChannelMgr.sendResponse(null, header, MsgResultType.ONLINE_LIMIT, response.build().toByteString(), 200, ctx, null);
			return;
		}

		final String accountId = request.getAccountId();
		final int zoneId = request.getZoneId();

		GameLog.debug("Game Login Start --> accountId:" + accountId + ",zoneId:" + zoneId);
		TableAccount userAccount = AccoutBM.getInstance().getByAccountId(accountId);
		if (userAccount == null) {
			response.setResultType(eLoginResultType.FAIL);
			response.setError("账号不存在");
			UserChannelMgr.sendResponse(null, header, MsgResultType.ACCOUNT_NOT_EXIST, response.build().toByteString(), 200, ctx, null);
			return;
		}
		// 不判断白名单 @ 2016-12-09
//		// 检测白名单 by lida
//		if (GameManager.isWhiteListLimit(userAccount.getOpenAccount())) {
//			response.setError("该区维护中，请稍后尝试，");
//			response.setResultType(eLoginResultType.ServerMainTain);
//			UserChannelMgr.sendResponse(null, header, MsgResultType.SERVER_MAINTAIN, response.build().toByteString(), 200, ctx, null);
//			return;
//		}
		String userId = userIdCache.getUserId(accountId, zoneId);
		if (userId == null) {
			response.setResultType(eLoginResultType.NO_ROLE);
			GameLog.debug("Create Role ...,accountId:" + accountId + " zoneId:" + zoneId);
			response.setVersion(((VersionConfig) VersionConfigDAO.getInstance().getCfgById("version")).getValue());
			UserChannelMgr.sendResponse(null, header, MsgResultType.ROLE_NOT_EXIST, response.build().toByteString(), 200, ctx, null);
		} else {
			Long sessionId = ServerHandler.getSessionId(ctx);
			if (sessionId == null) {
				GameLog.error("GameLoginServer", "", "login fail by not exist session id:" + ctx);
			} else {
				// 线程安全地执行角色登录操作
				GameWorldFactory.getGameWorld().asyncExecute(userId, loginProdecessor, new PlayerLoginTask(sessionId, header, request, true));
			}
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
			UserChannelMgr.sendResponse(null, header, MsgResultType.SHUTDOWN, createLoginResponse("停服维护中", eLoginResultType.FAIL), 200, ctx, null);
			return;
		}
		Long sessionId = ServerHandler.getSessionId(ctx);
		if (sessionId == null) {
			GameLog.error("GameLoginServer", "", "create fail by not exist session id:" + ctx);
			return;
		}
		final String accountId = request.getAccountId();
		final int zoneId = request.getZoneId();
		GameLog.debug("Game Create Role Start --> accountId:" + accountId + " , zoneId:" + zoneId);
		GameWorldFactory.getGameWorld().executeAccountTask(accountId, new PlayerCreateTask(request, header, sessionId, generator));
	}

	public void addUserZoneInfo(int zoneId, UserZoneInfo ZoneInfo, Player player) {
		ZoneInfo.setZoneId(zoneId);
		ZoneInfo.setLevel(player.getLevel());
		ZoneInfo.setVipLevel(player.getVip());
		ZoneInfo.setHeadImage(player.getHeadImage());
		ZoneInfo.setLastLoginMillis(player.getLastLoginTime());
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

}
