package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.lang3.StringUtils;

import com.common.HPCUtil;
import com.log.FSTraceLogger;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerFreshHelper;
import com.playerdata.PlayerMgr;
import com.rw.dataaccess.GameOperationFactory;
import com.rw.dataaccess.PlayerParam;
import com.rw.fsutil.cacheDao.IdentityIdGenerator;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rw.netty.UserChannelMgr;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rw.service.log.template.BIActivityCode;
import com.rwbase.common.dirtyword.CharFilterFactory;
import com.rwbase.common.enu.ESex;
import com.rwbase.dao.dropitem.DropRecord;
import com.rwbase.dao.dropitem.DropRecordDAO;
import com.rwbase.dao.majorDatas.MajorDataCache;
import com.rwbase.dao.majorDatas.MajorDataCacheFactory;
import com.rwbase.dao.majorDatas.pojo.MajorData;
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
	private final long submitTime;

	public PlayerCreateTask(GameLoginRequest request, RequestHeader header, ChannelHandlerContext ctx, IdentityIdGenerator generator) {
		super();
		this.request = request;
		this.header = header;
		this.ctx = ctx;
		this.generator = generator;
		this.submitTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		if (!this.ctx.channel().isActive()) {
			GameLog.error("PlayerCreateTask", request.getAccountId(), "create player fail by disconnect:" + UserChannelMgr.getCtxInfo(ctx));
			return;
		}
		long executeTime = System.currentTimeMillis();
		int seqID = header.getSeqID();
		String nick = request.getNick();
		int sex = request.getSex();
		final int zoneId = request.getZoneId();
		final String accountId = request.getAccountId();
		String userId = nettyControler.getGameLoginHandler().getUserId(accountId, zoneId);
		FSTraceLogger.logger("run", executeTime - submitTime, "CREATE", seqID, userId, accountId, false);
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
			UserChannelMgr.sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		if (StringUtils.isBlank(nick)) {
			response.setResultType(eLoginResultType.FAIL);
			String reason = "昵称不能为空";
			response.setError(reason);
			UserChannelMgr.sendResponse(header, response.build().toByteString(), ctx);
			return;
		}
		// 注册昵称这里没有做多线程安全，会导致后续创建角色失败
		if (UserDataDao.getInstance().validateName(nick)) {
			response.setResultType(eLoginResultType.FAIL);
			String reason = "昵称已经被注册!";
			response.setError(reason);
			UserChannelMgr.sendResponse(header, response.build().toByteString(), ctx);
			return;
		}

		String clientInfoJson = request.getClientInfoJson();
		ZoneLoginInfo zoneLoginInfo = null;
		ClientInfo clientInfo = null;
		if (StringUtils.isNotBlank(clientInfoJson)) {
			clientInfo = ClientInfo.fromJson(clientInfoJson);
			zoneLoginInfo = ZoneLoginInfo.fromClientInfo(clientInfo);
		}
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
		
		MajorData majorData = new MajorData();
		majorData.setId(userId);
		majorData.setOwnerId(userId);
		MajorDataCacheFactory.getCache().update(majorData);
		
		final Player player = PlayerMgr.getInstance().newFreshPlayer(userId, zoneLoginInfo);
		player.setZoneLoginInfo(zoneLoginInfo);

		// 不知道为何，奖励这里也依赖到了任务的TaskMgr,只能初始化完之后再初始化奖励物品
		PlayerFreshHelper.initCreateItem(player);
		
		//记录任务
		BILogMgr.getInstance().logZoneReg(player);
		//临时处理，新角色创建时没有player，只能将创建时同时处理的新手在线礼包日志打印到这里
		BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.ACTIVITY_TIME_COUNT_PACKAGE,0,0);
		long current = System.currentTimeMillis();
		world.asyncExecute(userId, new PlayerLoginTask(ctx, header, request, false, current));
		// eGameLoginType
		FSTraceLogger.logger("run", current - executeTime, "CREATE", seqID, userId, accountId, true);

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
