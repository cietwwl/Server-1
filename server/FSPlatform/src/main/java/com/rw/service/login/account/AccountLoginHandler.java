package com.rw.service.login.account;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.bm.login.AccoutBM;
import com.google.protobuf.ByteString;
import com.log.PlatformLog;
import com.rw.account.Account;
import com.rw.account.ZoneInfoCache;
import com.rw.fsutil.concurrent.ConcurrentRunnable;
import com.rw.fsutil.util.TextUtil;
import com.rw.netty.UserChannelMgr;
import com.rw.platform.PlatformFactory;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.ILog;
import com.rw.service.log.LogService;
import com.rw.service.log.RegLog;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rwbase.common.enu.EServerStatus;
import com.rwbase.dao.serverPage.TableServerPage;
import com.rwbase.dao.serverPage.TableServerPageDAO;
import com.rwbase.dao.user.accountInfo.AccountLoginRecord;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.user.accountInfo.UserZoneInfo;
import com.rwbase.dao.user.loginInfo.TableAccountLoginRecord;
import com.rwbase.dao.user.loginInfo.TableAccountLoginRecordDAO;
import com.rwproto.AccountLoginProtos.AccountInfo;
import com.rwproto.AccountLoginProtos.AccountLoginRequest;
import com.rwproto.AccountLoginProtos.AccountLoginResponse;
import com.rwproto.AccountLoginProtos.ServerPageInfo;
import com.rwproto.AccountLoginProtos.UserInfo;
import com.rwproto.AccountLoginProtos.ZoneInfo;
import com.rwproto.AccountLoginProtos.eAccountLoginType;
import com.rwproto.AccountLoginProtos.eLoginResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.PlatformGSMsg.UserInfoRequest;
import com.rwproto.PlatformGSMsg.ePlatformGSMsgType;

public class AccountLoginHandler {

	private static AccountLoginHandler instance;

	private AccountLoginHandler() {
	};

	public static AccountLoginHandler getInstance() {
		if (instance == null) {
			instance = new AccountLoginHandler();
		}
		return instance;
	}

	private static ConcurrentRunnable<String> concurrentRunnable;

	static {
		concurrentRunnable = new ConcurrentRunnable<String>();
	}

	public ByteString accountLogin(AccountLoginRequest request, Account account) {
		AccountLoginResponse.Builder response = AccountLoginResponse.newBuilder();
		response.setLoginType(eAccountLoginType.ACCOUNT_LOGIN);

		try {
			handelLogin(request, account, response);
		} catch (Exception e) {
			PlatformLog.error("AccountLoginHandler", "AccountLoginHandler[accountLogin]", "", e);
			response.setResultType(eLoginResultType.FAIL);
			response.setError("服务器繁忙，请稍后尝试.");
		}

		return response.build().toByteString();
	}

	private void handelLogin(final AccountLoginRequest request, final Account account, final AccountLoginResponse.Builder response) {
		Runnable task = new Runnable() {

			@Override
			public void run() {
				AccountInfo accountInfo = request.getAccount();
				String accountId = accountInfo.getAccountId();
				String password = accountInfo.getPassword();
				String openAccountId = accountInfo.getOpenAccountId();
				int logType = accountInfo.getLogType();
				String phoneInfo = accountInfo.getPhoneInfo();
				String clientInfoJson = accountInfo.getClientInfoJson();
				PlatformLog.info("AccountLoginHandler", accountId, "Account Login Start --> accountId:" + accountId + "; openAccountId:" + openAccountId);

				if (accountId == null || accountId.isEmpty()) {
					TableAccount tableAccount = AccoutBM.getInstance().getByOpenAccount(openAccountId);
					if (tableAccount != null) {
						accountId = tableAccount.getAccount();
						PlatformLog.info("Query by openAccount", "openAccountId", "accountId=" + accountId + ",openAccountId=" + openAccountId);
					}
				}
				// 快速注册账号
				if (accountId.equals("")) {
					accountId = handelRandomAccount(account, response, accountId, openAccountId, logType, phoneInfo, clientInfoJson);
				} else {
					// 注册账号
					TableAccount tableAccount = AccoutBM.getInstance().getByAccountId(accountId);
					if (tableAccount == null) {
						handleRegByAccountId(account, response, accountInfo, accountId, password, openAccountId, logType, phoneInfo, clientInfoJson);
					} else {
						// 验证账号
						handleAccountLogin(account, response, accountInfo, accountId, logType, phoneInfo, clientInfoJson);
					}
				}
				addAccount(account, accountId);
			}
		};
		String openAccountId = request.getAccount().getOpenAccountId();
		if (openAccountId != null) {
			concurrentRunnable.executeTask(openAccountId, task);
		} else {
			task.run();
		}
	}

	/** 验证账号 */
	private void handleAccountLogin(Account account, AccountLoginResponse.Builder response, AccountInfo accountInfo, String accountId, int logType, String phoneInfo, String clientInfoJson) {
		response.setResultType(eLoginResultType.SUCCESS);
		response.setAccount(accountInfo);
		TableAccount userAccount = AccoutBM.getInstance().getByAccountId(accountId);
		TableAccountLoginRecord record = TableAccountLoginRecordDAO.getInstance().get(accountId);
		if (userAccount != null) {
			account.setAccountId(accountId);
			ZoneInfoCache zoneInfo = null;
			if (record != null) {
				zoneInfo = PlatformFactory.getPlatformService().getZoneInfo(record.getZoneId());
				userAccount.setLastLogin(false, zoneInfo.getZoneId());
			}
			if (zoneInfo == null) {
				zoneInfo = PlatformFactory.getPlatformService().getLastZoneCfg(account.isWhiteList());
			}
			if (zoneInfo != null) {
				response.setLastZone(getZoneInfo(zoneInfo, account.isWhiteList()));
			}
		} else {
			ZoneInfoCache zoneInfo = PlatformFactory.getPlatformService().getLastZoneCfg(account.isWhiteList());
			response.setLastZone(getZoneInfo(zoneInfo, account.isWhiteList()));
		}

		try {
			ClientInfo clientInfo = ClientInfo.fromJson(clientInfoJson, accountId);
			ILog log = LogService.getInstance().getLogByType(logType);
			log.parseLog(phoneInfo);
			if (log != null) {
				log.fillInfoToClientInfo(clientInfo);
			}
			BILogMgr.getInstance().logAccountLogin(clientInfo, userAccount.getRegisterTime(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 检查顶号
		if (record != null && record.getUserId() != null) {
			AccountLoginRecord cacheRecord = userAccount.getRecord();
			if (cacheRecord == null) {
				cacheRecord = new AccountLoginRecord();
				userAccount.setRecord(cacheRecord);
				cacheRecord.setAccountId(accountId);
			}
			processKickOnlinePlayer(record.getUserId(), record, accountId);
			if (cacheRecord.getLoginTime() != record.getLoginTime()) {
				cacheRecord.setUserId(record.getUserId());
				cacheRecord.setZoneId(record.getZoneId());
				cacheRecord.setLoginTime(record.getLoginTime());

				UserInfoRequest.Builder gsRequest = UserInfoRequest.newBuilder();
				gsRequest.setPlatformGSMsgType(ePlatformGSMsgType.USER_INFO);
				gsRequest.setUserId(record.getUserId());
				gsRequest.setAccountId(accountId);
				ZoneInfoCache zone = PlatformFactory.getPlatformService().getZoneInfo(record.getZoneId());
				PlatformFactory.clientManager.submitReqeust(zone.getServerIp(), Integer.parseInt(zone.getPort()), gsRequest.build().toByteString(), Command.MSG_PLATFORMGS, accountId);
			}

		}

		PlatformLog.info("AccountLoginHandler", accountId, "Account Login Finish --> accountId:" + accountId);
	}

	/** 注册账号 */
	private void handleRegByAccountId(Account account, AccountLoginResponse.Builder response, AccountInfo accountInfo, String accountId, String password, String openAccountId, int logType, String phoneInfo, String clientInfoJson) {
		if (isIllegalAccount(accountId)) {
			response.setError("注册失败，账户名不符合规范");
			response.setResultType(eLoginResultType.FAIL);
		} else if (StringUtils.isNotBlank(accountId) && StringUtils.isNotBlank(password)) {
			TableAccount newAccount = AccoutBM.getInstance().createAccount(accountId, password, openAccountId);
			if (newAccount != null) {
				response.setResultType(eLoginResultType.SUCCESS);
				ClientInfo clientInfo = ClientInfo.fromJson(clientInfoJson, accountId);
				RegLog regLog = RegLog.fromJson(phoneInfo);
				newAccount.setChannelId(clientInfo.getChannelId());
				ILog log = processRegLog(logType, phoneInfo, accountId, clientInfo);
				response.setAccount(accountInfo);
				account.setAccountId(accountId);
				ZoneInfoCache lastZoneCfg = PlatformFactory.getPlatformService().getLastZoneCfg(account.isWhiteList());
				response.setLastZone(getZoneInfo(lastZoneCfg, account.isWhiteList()));

				if (log != null) {
					log.fillInfoToClientInfo(clientInfo);
				}
				BILogMgr.getInstance().logAccountReg(clientInfo, newAccount.getRegisterTime(), regLog, true);
			} else {
				ClientInfo clientInfo = ClientInfo.fromJson(clientInfoJson, accountId);
				RegLog regLog = RegLog.fromJson(phoneInfo);
				BILogMgr.getInstance().logAccountReg(clientInfo, System.currentTimeMillis(), regLog, false);
			}
		} else {
			response.setError("未知错误，请重新注册！");
			response.setResultType(eLoginResultType.FAIL);

			ClientInfo clientInfo = ClientInfo.fromJson(clientInfoJson, accountId);
			RegLog regLog = RegLog.fromJson(phoneInfo);
			BILogMgr.getInstance().logAccountReg(clientInfo, System.currentTimeMillis(), regLog, false);
		}
	}

	/** 快速注册账号 */
	private String handelRandomAccount(Account account, AccountLoginResponse.Builder response, String accountId, String openAccountId, int logType, String phoneInfo, String clientInfoJson) {
		String password;
		TableAccount newAccount = AccoutBM.getInstance().createRandomAccount(openAccountId);
		if (newAccount != null) {
			accountId = newAccount.getAccount();
			account.setAccountId(accountId);
			password = newAccount.getPassword();
			ClientInfo clientInfo = ClientInfo.fromJson(clientInfoJson, accountId);
			RegLog regLog = RegLog.fromJson(phoneInfo);

			newAccount.setChannelId(clientInfo.getChannelId());
			ILog log = processRegLog(logType, phoneInfo, accountId, clientInfo);
			AccountInfo.Builder newAccountInfo = AccountInfo.newBuilder();
			newAccountInfo.setAccountId(accountId);
			newAccountInfo.setPassword(password);
			response.setResultType(eLoginResultType.SUCCESS);
			response.setAccount(newAccountInfo.build());
			account.setAccountId(accountId);
			ZoneInfoCache lastZoneCfg = PlatformFactory.getPlatformService().getLastZoneCfg(account.isWhiteList());
			if (lastZoneCfg != null) {
				response.setLastZone(getZoneInfo(lastZoneCfg, account.isWhiteList()));
			}

			if (log != null) {
				log.fillInfoToClientInfo(clientInfo);
			}
			BILogMgr.getInstance().logAccountReg(clientInfo, newAccount.getRegisterTime(), regLog, true);
		} else {
			response.setError("生成账号失败，请重新注册！");
			response.setResultType(eLoginResultType.FAIL);

			ClientInfo clientInfo = ClientInfo.fromJson(clientInfoJson, accountId);
			RegLog regLog = RegLog.fromJson(phoneInfo);
			BILogMgr.getInstance().logAccountReg(clientInfo, System.currentTimeMillis(), regLog, false);
		}
		return accountId;
	}

	private ZoneInfo getZoneInfo(ZoneInfoCache zone, boolean isWhite) {
		ZoneInfo.Builder zoneInfo = ZoneInfo.newBuilder();
		zoneInfo.setZoneId(zone.getZoneId());
		zoneInfo.setZoneName(zone.getZoneName());
		zoneInfo.setServerIp(zone.getServerIp());
		zoneInfo.setPort(zone.getPort());
		String status = zone.getStatusDesc();
		if (status.equals("维护")) {
			if (isWhite) {
				status = "普通";
			}
		}
		EServerStatus eServerStatus = EServerStatus.getStatus(status);
		zoneInfo.setStatus(status);
		zoneInfo.setTips(getServerStatusTips(zone, status));
		zoneInfo.setRecommand(zone.getRecommand());
		zoneInfo.setIsOpen(zone.getIsOpen(eServerStatus.getStatusId()) ? 1 : 0);
		zoneInfo.setColor(zone.getStatusColor(eServerStatus.getStatusId()));

		return zoneInfo.build();
	}

	private String getServerStatusTips(ZoneInfoCache zone, String status) {
		String closeTips = zone.getCloseTips();
		if (StringUtils.isEmpty(closeTips)) {
			return "服务器" + status + "中...";
		} else {
			return closeTips;
		}
	}

	private boolean isIllegalAccount(String accountId) {
		boolean illegal = false;
		if (NumberUtils.isNumber(accountId) && accountId.length() > 10) {
			illegal = true;
		}
		if (TextUtil.isContainsChinese(accountId)) {
			illegal = true;
		}
		return illegal;
	}

	public ByteString getZoneList(AccountLoginRequest request, Account account) {

		AccountLoginResponse.Builder response = AccountLoginResponse.newBuilder();

		String accountId = request.getAccount().getAccountId();
		if (account == null) {
			response.setLoginType(eAccountLoginType.ZONE_LIST);
			response.setResultType(eLoginResultType.FAIL);
			response.setError("服务器繁忙，请稍候尝试。");
			return response.build().toByteString();
		}
		if (StringUtils.isEmpty(account.getAccountId())) {
			account.setAccountId(accountId);
		}
		try {
			handleZoneList(account, response, accountId);
		} catch (Exception e) {
			response.setLoginType(eAccountLoginType.ZONE_LIST);
			PlatformLog.error("AccountLoginHandler", accountId, "", e);
			response.setResultType(eLoginResultType.FAIL);
			response.setError("服务器繁忙，请稍候尝试。");
		}
		return response.build().toByteString();

	}

	public ByteString refreshZoneInfo(AccountLoginRequest request, Account account) {
		AccountLoginResponse.Builder response = AccountLoginResponse.newBuilder();
		String accountId = request.getAccount().getAccountId();
		if (account == null) {
			response.setResultType(eLoginResultType.FAIL);
			response.setError("服务器繁忙，请稍候尝试。");
			return response.build().toByteString();
		}
		if (StringUtils.isEmpty(account.getAccountId())) {
			account.setAccountId(accountId);
		}
		try {
			ZoneInfo zone = request.getZone();
			handleRefreshZoneInfo(account, response, accountId, zone);
		} catch (Exception e) {
			PlatformLog.error("AccountLoginHandler", accountId, "", e);
			response.setResultType(eLoginResultType.FAIL);
			response.setError("服务器繁忙，请稍候尝试。");
		}
		return response.build().toByteString();
	}

	private void handleZoneList(Account account, AccountLoginResponse.Builder response, String accountId) {
		TableAccount userAccount = account.getTableAccount();
		if (userAccount == null) {
			//TODO 增加通过OpenAccountId获取的容错
			String openAccountId = response.getAccount().getOpenAccountId();
			if(openAccountId != null && !openAccountId.isEmpty()){
				userAccount = AccoutBM.getInstance().getByOpenAccount(openAccountId);
			}else{
				userAccount = AccoutBM.getInstance().getByAccountId(accountId);
			}
		}
		// 没有玩家只发服务器列表

		response.setLoginType(eAccountLoginType.ZONE_LIST);
		List<ZoneInfoCache> allZoneList = PlatformFactory.getPlatformService().getAllZoneList();
		for (ZoneInfoCache zone : allZoneList) {
			response.addZoneList(getZoneInfo(zone, account.isWhiteList()));
		}

		Collection<TableServerPage> allServerPage = PlatformFactory.getPlatformService().getAllServerPage();
		for (TableServerPage tableServerPage : allServerPage) {
			int pageId = tableServerPage.getPageId();

			if (pageId == TableServerPageDAO.TEST_PAGE && !account.isWhiteList()) {
				continue;
			}

			ServerPageInfo.Builder serverPageInfo = ServerPageInfo.newBuilder();
			serverPageInfo.setPageId(pageId);
			serverPageInfo.setPageName(tableServerPage.getPageName());
			serverPageInfo.setPageServer(tableServerPage.getPageServers());
			response.addPageList(serverPageInfo);
		}

		List<Integer> lastLoginList = userAccount.getLastLoginList();
		UserInfo.Builder userInfo;
		ZoneInfoCache zone;

		for (int i = lastLoginList.size() - 1; i >= 0; i--) {
			int zoneId = lastLoginList.get(i);
			UserZoneInfo userZoneInfo = userAccount.getZoneIdInUserZoneInfoMap(zoneId);
			if (userZoneInfo == null) {
				continue;
			}
			zone = PlatformFactory.getPlatformService().getZoneInfo(zoneId);
			userInfo = UserInfo.newBuilder();
			userInfo.setZoneInfo(getZoneInfo(zone, account.isWhiteList()));
			userInfo.setHeadImage(userZoneInfo.getHeadImage());
			userInfo.setVipLv(userZoneInfo.getVipLevel());
			userInfo.setCareer(userZoneInfo.getCareer());
			userInfo.setLv(userZoneInfo.getLevel());
			userInfo.setName(userZoneInfo.getUserName());
			response.addUserList(userInfo);
		}

		response.setResultType(eLoginResultType.SUCCESS);
	}

	/**
	 * 刷新区状态信息
	 * 
	 * @param account
	 * @param response
	 * @param accountId
	 * @param zone
	 */
	private void handleRefreshZoneInfo(Account account, AccountLoginResponse.Builder response, String accountId, ZoneInfo zone) {
		response.setLoginType(eAccountLoginType.REFRESH_ZONE_STATUS);
		List<ZoneInfoCache> allZoneList = PlatformFactory.getPlatformService().getAllZoneList();
		for (ZoneInfoCache zoneCache : allZoneList) {
			// 当设置服务器不显示，则不发送到客户端
			if (zoneCache.getZoneId() == zone.getZoneId()) {
				response.setLastZone(getZoneInfo(zoneCache, account.isWhiteList()));
			}
		}
		response.setResultType(eLoginResultType.SUCCESS);
	}

	private void processKickOnlinePlayer(String userId, TableAccountLoginRecord record, String accountId) {
		UserInfoRequest.Builder gsRequest = UserInfoRequest.newBuilder();
		gsRequest.setPlatformGSMsgType(ePlatformGSMsgType.USER_STATUS);
		gsRequest.setUserId(userId);
		ZoneInfoCache zone = PlatformFactory.getPlatformService().getZoneInfo(record.getZoneId());
		if (zone.getIsOpen(zone.getStatus())) {
			PlatformFactory.clientManager.submitReqeust(zone.getServerIp(), Integer.parseInt(zone.getPort()), gsRequest.build().toByteString(), Command.MSG_PLATFORMGS, accountId);
		}
	}

	private void addAccount(Account account, String accountId) {
		UserChannelMgr.bindUserID(accountId);
		account.setAccountId(accountId);
		PlatformFactory.getPlatformService().addAccount(account);
	}

	public ILog processRegLog(int logType, String logValue, String accountId, ClientInfo clientInfo) {
		ILog log = null;
		try {
			log = LogService.getInstance().getLogByType(logType);
			log.parseLog(logValue);
			log.setLogValue(accountId);
			// LogService.getInstance().sendLog(log, clientInfo);
			return log;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return log;
	}
}
