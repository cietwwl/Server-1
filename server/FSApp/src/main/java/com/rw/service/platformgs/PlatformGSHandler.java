package com.rw.service.platformgs;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.netty.UserChannelMgr;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwproto.PlatformGSMsg.UserInfoRequest;
import com.rwproto.PlatformGSMsg.UserInfoResponse;
import com.rwproto.PlatformGSMsg.ePlatformGSMsgType;

public class PlatformGSHandler {
	private static PlatformGSHandler instance = new PlatformGSHandler();

	public static PlatformGSHandler getInstance() {
		return instance;
	}

	public ByteString getUserInfo(UserInfoRequest userInfoRequest) {
		String userId = userInfoRequest.getUserId();
		UserInfoResponse.Builder userInfoResponse = UserInfoResponse.newBuilder();

		User user = UserDataDao.getInstance().getByUserId(userId);
		Hero hero = FSHeroMgr.getInstance().getMainRoleHero(userId);
		userInfoResponse.setPlatformGSMsgType(ePlatformGSMsgType.USER_INFO);
		if (user != null && hero != null) {
			String account = user.getAccount();
			int level = hero.getLevel();
			int vipLevel = user.getVip();
			String headImage = user.getHeadImage();
			int career = hero.getCareerType();
			String userName = user.getUserName();
			long lastLoginTime = user.getLastLoginTime();
			userInfoResponse.setAccountId(account);
			userInfoResponse.setLevel(level);
			userInfoResponse.setVipLevel(vipLevel);
			userInfoResponse.setHeadImage(headImage);
			userInfoResponse.setCareer(career);
			userInfoResponse.setUserName(userName);
			userInfoResponse.setLastLoginTime(lastLoginTime);
		}
		return userInfoResponse.build().toByteString();
	}

	public ByteString processKickPlayerOnline(UserInfoRequest userInfoRequest) {
		String userId = userInfoRequest.getUserId();
		if (UserChannelMgr.isConnecting(userId)) {
			Player player = PlayerMgr.getInstance().find(userId);
			GameLog.debug("Kick Player...,userId:" + userId);
			player.KickOff("你的账号在另一处登录，请重新登录");
		}

		UserInfoResponse.Builder userInfoResponse = UserInfoResponse.newBuilder();
		userInfoResponse.setPlatformGSMsgType(ePlatformGSMsgType.USER_STATUS);
		return userInfoResponse.build().toByteString();
	}
}
