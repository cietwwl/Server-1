package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.lang3.StringUtils;

import com.common.HPCUtil;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.dataaccess.GameOperationFactory;
import com.rw.dataaccess.PlayerParam;
import com.rw.fsutil.cacheDao.IdentityIdGenerator;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.common.dirtyword.CharFilterFactory;
import com.rwbase.common.enu.ESex;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.GameWorld;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.GameLoginProtos.GameLoginRequest;
import com.rwproto.GameLoginProtos.GameLoginResponse;
import com.rwproto.GameLoginProtos.eLoginResultType;
import com.rwproto.RequestProtos.RequestHeader;

public class PlayerCreateTask implements Runnable {

	private static FsNettyControler nettyControler = SpringContextUtil.getBean("fsNettyControler");

	private final GameLoginRequest request;
	private final RequestHeader header;
	private final ChannelHandlerContext ctx;
	private final IdentityIdGenerator generator;

	public PlayerCreateTask(GameLoginRequest request, RequestHeader header, ChannelHandlerContext ctx, IdentityIdGenerator generator) {
		super();
		this.request = request;
		this.header = header;
		this.ctx = ctx;
		this.generator = generator;
	}

	@Override
	public void run() {
		String nick = request.getNick();
		int sex = request.getSex();
		final int zoneId = request.getZoneId();
		final String accountId = request.getAccountId();
		String userId = nettyControler.getGameLoginHandler().getUserId(accountId, zoneId);
		GameWorld world = GameWorldFactory.getGameWorld();
		if (userId != null) {
			// author: lida 增加容错 如果已经创建角色则进入主城
			world.asyncExecute(userId, new PlayerLoginTask(ctx, header, request, false));
			return;
		}

		GameLoginResponse.Builder response = GameLoginResponse.newBuilder();
		// 检查昵称
		if (CharFilterFactory.getCharFilter().checkWords(nick, true, true, true, true)) {
			response.setResultType(eLoginResultType.FAIL);
			String reason = "昵称不能包含屏蔽字或非法字符";
			response.setError(reason);
			nettyControler.sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		if (StringUtils.isBlank(nick)) {
			response.setResultType(eLoginResultType.FAIL);
			String reason = "昵称不能为空";
			response.setError(reason);
			nettyControler.sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		// 注册昵称这里没有做多线程安全，会导致后续创建角色失败
		if (UserDataDao.getInstance().validateName(nick)) {
			response.setResultType(eLoginResultType.FAIL);
			String reason = "昵称已经被注册!";
			response.setError(reason);
			nettyControler.sendResponse(header, response.build().toByteString(), ctx);
			return;
		}

		String clientInfoJson = request.getClientInfoJson();
		ZoneLoginInfo zoneLoginInfo = null;
		ClientInfo clientInfo = null;
		if (StringUtils.isNotBlank(clientInfoJson)) {
			clientInfo = ClientInfo.fromJson(clientInfoJson);
			zoneLoginInfo = ZoneLoginInfo.fromClientInfo(clientInfo);
		}
		// modify@2015-08-07 by Jamaz
		// 用serverId+identifier的方式生成userId
		userId = newUserId();
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
		GameOperationFactory.getCreatedOperation().execute(param);

		final Player player = PlayerMgr.getInstance().newFreshPlayer(userId, zoneLoginInfo);
		player.setZoneLoginInfo(zoneLoginInfo);
		// author：lida 2015-09-21 通知登陆服务器更新账号信息 确保账号添加成功
		world.asynExecute(new Runnable() {

			@Override
			public void run() {
				nettyControler.getGameLoginHandler().notifyPlatformPlayerLogin(zoneId, accountId, player);
			}
		});
		world.asyncExecute(userId, new PlayerLoginTask(ctx, header, request,false));
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
		UserDataDao.getInstance().saveOrUpdate(baseInfo);
	}

	private String newUserId() {
		StringBuilder sb = new StringBuilder(GameManager.getGenerateTotalNumber());
		sb.append(GameManager.getServerId());
		sb.append(HPCUtil.fillZero(generator.generateId(), GameManager.getGenerateIdNumber()));
		return sb.toString();
	}

}
