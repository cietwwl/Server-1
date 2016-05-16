package com.rw.service.login.game;

import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.bm.login.AccoutBM;
import com.bm.serverStatus.ServerStatusMgr;
import com.common.HPCUtil;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.rw.dataaccess.GameOperationFactory;
import com.rw.dataaccess.PlayerParam;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.rw.fsutil.cacheDao.IdentityIdGenerator;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rw.netty.UserChannelMgr;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.common.dirtyword.CharFilterFactory;
import com.rwbase.common.enu.ESex;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
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

public class GameLoginHandler {

	private static GameLoginHandler instance = new GameLoginHandler();
	// 全服唯一id的生成器，完整的userId完成是serverId + generateId
	private final IdentityIdGenerator generator;
	private final UserIdCache userIdCache;

	protected GameLoginHandler() {
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		if (dataSource == null) {
			throw new ExceptionInInitializerError("获取dataSource失败");
		}
		this.generator = new IdentityIdGenerator("user_identifier", dataSource);
		this.userIdCache = new UserIdCache(dataSource);
	}

	public static GameLoginHandler getInstance() {
		return instance;
	}

	public ByteString gameServerLogin(GameLoginRequest request) {
		final GameLoginResponse.Builder response = GameLoginResponse.newBuilder();

		if (GameManager.isShutdownHook) {
			response.setError("停服维护中");
			response.setResultType(eLoginResultType.FAIL);
			return response.build().toByteString();
		}
		if (GameManager.isOnlineLimit()) {
			response.setError("该区人气火爆，请稍后尝试，或者选择推荐新区。");
			response.setResultType(eLoginResultType.ServerMainTain);
			return response.build().toByteString();
		}

		// author:lida 2015-08-24
		if (GameManager.isReloadconfig()) {
			response.setError("系统更新中");
			response.setResultType(eLoginResultType.FAIL);
			return response.build().toByteString();
		}

		final String accountId = request.getAccountId();
		final int zoneId = request.getZoneId();

		GameLog.debug("Game Login Start --> accountId:" + accountId + " , zoneId:" + zoneId);

		TableAccount userAccount = AccoutBM.getInstance().getByAccountId(accountId);
		if (userAccount == null) {
			response.setResultType(eLoginResultType.FAIL);
			response.setError("账号不存在");
			return response.build().toByteString();
		} else {
			String clientInfoJson = request.getClientInfoJson();
			ZoneLoginInfo zoneLoginInfo = null;
			ClientInfo clientInfo = null;
			if (StringUtils.isNotBlank(clientInfoJson)) {
				clientInfo = ClientInfo.fromJson(clientInfoJson);
				zoneLoginInfo = ZoneLoginInfo.fromClientInfo(clientInfo);

			}
			String userId = userIdCache.getUserId(accountId, zoneId);
			if (userId == null) {
				if (GameManager.isWhiteListLimit(accountId)) {
					response.setError("该区维护中，请稍后尝试，");
					response.setResultType(eLoginResultType.ServerMainTain);
					return response.build().toByteString();
				}
				response.setResultType(eLoginResultType.NO_ROLE);
				GameLog.debug("Create Role ...,accountId:" + accountId + " zoneId:" + zoneId);
				response.setVersion(((VersionConfig) VersionConfigDAO.getInstance().getCfgById("version")).getValue());
				return response.build().toByteString();
			}
			User user = UserDataDao.getInstance().getByUserId(userId);

			if (GameManager.isWhiteListLimit(user.getAccount())) {
				response.setError("该区维护中，请稍后尝试，");
				response.setResultType(eLoginResultType.ServerMainTain);
				return response.build().toByteString();
			} else if (user.isBlocked()) {
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
				return response.build().toByteString();
			} else if (user.isInKickOffCoolTime()) {
				response.setError("亲爱的用户，抱歉你已被强制下线，请5分钟后再次尝试登录。");
				response.setResultType(eLoginResultType.FAIL);
				return response.build().toByteString();
			} else {
				if (clientInfo != null) {
					user.setChannelId(clientInfo.getChannelId());
				}

				final String userId_ = userId;
				final ChannelHandlerContext ctx = UserChannelMgr.getThreadLocalCTX();
				// GameWorldFactory.getGameWorld().asyncExecute(userId_, new
				// PlayerTask() {
				//
				// @Override
				// public void run(Player player) {
				// Player player = checkIsPlayerOnLine(userId);
				Player player = PlayerMgr.getInstance().find(userId_);
				//检查发送版本更新 机器人clientInfo这个值为null
				if (clientInfo != null) {
					player.getUpgradeMgr().doCheckUpgrade(clientInfo.getClientVersion());
				}
				// --------------------------------------------------------START
				// TODO HC @Modify 2015-12-17
				/**
				 * <pre>
				 * 序章特殊剧情，当我创建完角色之后，登录数据推送完毕，我就直接把剧情设置一个假想值
				 * 保证不管角色当前是故意退出游戏跳过剧情，或者是出现意外退出，在下次进来都不会有剧情的重复问题
				 * </pre>
				 */
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
				// --------------------------------------------------------END

				if (player != null) {
					// modify@2015-12-28 by Jamaz 只断开非当前链接
					ChannelHandlerContext oldContext = UserChannelMgr.get(userId_);
					if (oldContext != null && oldContext != ctx) {
						GameLog.debug("Kick Player...,userId:" + userId_);
						player.KickOff("你的账号在另一处登录，请重新登录");
					}
				}
				// 直接登录游戏
				else {
					player = PlayerMgr.getInstance().find(userId_);
				}
				final Player p = player;
				GameWorldFactory.getGameWorld().asynExecute(new Runnable() {

					@Override
					public void run() {
						// author:lida 2015-09-21 通知登陆服务器更新账号信息
						notifyPlatformPlayerLogin(zoneId, accountId, p);
					}
				});

				long lastLoginTime = player.getUserGameDataMgr().getLastLoginTime();

				UserChannelMgr.bindUserID(userId_, ctx);
				// 增加清空重连时间
				UserChannelMgr.clearDisConnectTime(userId_);

				player.onLogin();

				if (StringUtils.isBlank(player.getUserName())) {
					response.setResultType(eLoginResultType.NO_ROLE);
					GameLog.debug("Create Role ...,userId:" + userId_);
				} else {
					response.setResultType(eLoginResultType.SUCCESS);
					GameLog.debug("Login Success ...,userId:" + userId_);
				}
				response.setUserId(userId_);
				GameLog.debug("Game Login Finish --> accountId:" + accountId + " , zoneId:" + zoneId);
				GameLog.debug("Game Login Finish --> userId:" + userId_);
				player.setZoneLoginInfo(zoneLoginInfo);
				BILogMgr.getInstance().logZoneLogin(player);
				// 通用活动数据同步,生成活动奖励空数据；应置于所有通用活动的统计之前；可后期放入初始化模块
				ActivityCountTypeMgr.getInstance().checkActivityOpen(player);
				ActivityTimeCardTypeMgr.getInstance().checkActivityOpen(player);
				ActivityRateTypeMgr.getInstance().checkActivityOpen(player);
				
				//判断需要用到最后次登陆 时间。保存在活动内而不是player
				UserEventMgr.getInstance().RoleLogin(player, lastLoginTime);

				// 补充进入主城需要同步的数据
				LoginSynDataHelper.setData(player, response);

				// player.SendMsg(Command.MSG_LOGIN_GAME,
				// response.build().toByteString());
				return response.build().toByteString();
				// }
				// });
			}
		}
		// return null;
	}

	public ByteString createRoleAndLogin(GameLoginRequest request) {
		GameLoginResponse.Builder response = GameLoginResponse.newBuilder();
		if (GameManager.isShutdownHook) {
			response.setError("停服维护中");
			response.setResultType(eLoginResultType.FAIL);
			return response.build().toByteString();
		}

		final String accountId = request.getAccountId();
		final int zoneId = request.getZoneId();
		if (GameManager.isWhiteListLimit(accountId)) {
			response.setError("该区维护中，请稍后尝试，");
			response.setResultType(eLoginResultType.ServerMainTain);
			return response.build().toByteString();
		}
		GameLog.debug("Game Create Role Start --> accountId:" + accountId + " , zoneId:" + zoneId);
		// author: lida 增加容错 如果已经创建角色则进入主城
		User user = UserDataDao.getInstance().getByAccoutAndZoneId(accountId, zoneId);
		if (user != null) {
			return notifyCreateRoleSuccess(response, user);
		}

		String clientInfoJson = request.getClientInfoJson();
		ZoneLoginInfo zoneLoginInfo = null;
		ClientInfo clientInfo = null;
		if (StringUtils.isNotBlank(clientInfoJson)) {
			clientInfo = ClientInfo.fromJson(clientInfoJson);
			zoneLoginInfo = ZoneLoginInfo.fromClientInfo(clientInfo);
		}

		String nick = request.getNick();
		int sex = request.getSex();
		if (CharFilterFactory.getCharFilter().checkWords(nick, true, true, true, true)) {
			response.setResultType(eLoginResultType.FAIL);
			String reason = "昵称不能包含屏蔽字或非法字符";
			response.setError(reason);
			return response.build().toByteString();
		} else if (StringUtils.isBlank(nick)) {
			response.setResultType(eLoginResultType.FAIL);
			String reason = "昵称不能为空";
			response.setError(reason);
			return response.build().toByteString();
		} else if (UserDataDao.getInstance().validateName(nick)) {
			response.setResultType(eLoginResultType.FAIL);
			String reason = "昵称已经被注册!";
			response.setError(reason);
			return response.build().toByteString();
		}

		// modify@2015-08-07 by Jamaz
		// 用serverId+identifier的方式生成userId
		String userId = newUserId();
		createUser(userId, zoneId, accountId, nick, sex, clientInfoJson);

		String headImage;
		String roleId;
		if (sex == ESex.Men.getOrder()) {
			headImage = "10001";
			roleId = "101001_1";
		} else {
			headImage = "10002";
			roleId = "100001_1";
		}

		RoleCfg playerCfg = RoleCfgDAO.getInstance().getConfig(roleId);
		PlayerParam param = new PlayerParam(accountId, userId, nick, zoneId, sex, System.currentTimeMillis(), playerCfg, headImage, clientInfoJson);
		if (GameOperationFactory.getCreatedOperation().execute(param)) {
			createUser(userId, zoneId, accountId, nick, sex, clientInfoJson);
		}

		// userAccount.addUserZoneInfo(zoneId);
		// accountBM.update(userAccount);
		final Player player = PlayerMgr.getInstance().newFreshPlayer(userId, zoneLoginInfo);
		player.setZoneLoginInfo(zoneLoginInfo);
		// author：lida 2015-09-21 通知登陆服务器更新账号信息 确保账号添加成功
		GameWorldFactory.getGameWorld().asynExecute(new Runnable() {

			@Override
			public void run() {
				notifyPlatformPlayerLogin(zoneId, accountId, player);
			}
		});

		UserChannelMgr.bindUserID(userId);
		long start = System.currentTimeMillis();

		long end = System.currentTimeMillis();
		System.out.println("-------------------" + (end - start));
		player.onLogin();
		long end1 = System.currentTimeMillis();
		System.out.println("-------------------" + (end1 - start));

		// 检查发送版本更新
		if (clientInfo != null) {
			player.getUpgradeMgr().doCheckUpgrade(clientInfo.getClientVersion());
		}

		// 检查发送gm邮件
		ServerStatusMgr.processGmMailWhenCreateRole(player);
		response.setResultType(eLoginResultType.SUCCESS);
		response.setUserId(userId);
		GameLog.debug("Create Role ...,userId:" + userId);
		GameLog.debug("Game Create Role Finish --> accountId:" + accountId + " , zoneId:" + zoneId);
		GameLog.debug("Game Create Role Finish --> userId:" + userId);
		BILogMgr.getInstance().logZoneReg(player);
		// 通用活动数据同步,生成活动奖励空数据；应置于所有通用活动的统计之前；可后期放入初始化模块
		ActivityCountTypeMgr.getInstance().checkActivityOpen(player);
		ActivityTimeCardTypeMgr.getInstance().checkActivityOpen(player);
		// 判断需要用到最后次登陆 时间。保存在活动内而不是player
		UserEventMgr.getInstance().RoleLogin(player, 0);
		LoginSynDataHelper.setData(player, response);

		response.setVersion(((VersionConfig) VersionConfigDAO.getInstance().getCfgById("version")).getValue());
		// 补充进入主城需要同步的数据
		return response.build().toByteString();
	}

	private ByteString notifyCreateRoleSuccess(GameLoginResponse.Builder response, User user) {
		String userId = user.getUserId();
		Player player = PlayerMgr.getInstance().find(userId);
		UserChannelMgr.bindUserID(userId);

		long end = System.currentTimeMillis();
		player.onLogin();
		long end1 = System.currentTimeMillis();

		response.setResultType(eLoginResultType.SUCCESS);
		response.setUserId(userId);

		LoginSynDataHelper.setData(player, response);

		response.setVersion(((VersionConfig) VersionConfigDAO.getInstance().getCfgById("version")).getValue());
		// 补充进入主城需要同步的数据
		return response.build().toByteString();
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

	private void notifyPlatformPlayerLogin(int zoneId, String accountId, Player player) {
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

	private String newUserId() {
		String userId;
		StringBuilder sb = new StringBuilder(GameManager.getGenerateTotalNumber());
		sb.append(GameManager.getServerId());
		sb.append(HPCUtil.fillZero(generator.generateId(), GameManager.getGenerateIdNumber()));
		userId = sb.toString();
		return userId;
	}

	private void createUser(String userId, int zoneId, String accountId, String nick, int sex, String clientInfoJson) {
		User baseInfo = new User();
		baseInfo.setUserId(userId);
		baseInfo.setAccount(accountId);
		baseInfo.setZoneId(zoneId);
		baseInfo.setExp(0);
		baseInfo.setLevel(1);
		baseInfo.setUserName(nick);
		baseInfo.setSex(sex);
		baseInfo.setCreateTime(System.currentTimeMillis()); // 记录创建角色的时间
		// 设置默认头像
		if (sex == ESex.Men.getOrder()) {
			baseInfo.setHeadImage("10001");
		} else {
			baseInfo.setHeadImage("10002");
		}
		if (StringUtils.isNotBlank(clientInfoJson)) {
			ClientInfo clienInfo = ClientInfo.fromJson(clientInfoJson);
			if (clienInfo != null) {
				baseInfo.setChannelId(clienInfo.getChannelId());
			}
			baseInfo.setZoneRegInfo(ZoneRegInfo.fromClientInfo(clienInfo, accountId));

		}
		// baseInfo.setCareer(0);
		UserDataDao.getInstance().saveOrUpdate(baseInfo);
	}

}
