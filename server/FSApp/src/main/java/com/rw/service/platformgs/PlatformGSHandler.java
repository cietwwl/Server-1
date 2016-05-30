package com.rw.service.platformgs;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.netty.UserChannelMgr;
import com.rwproto.PlatformGSMsg.UserInfoRequest;
import com.rwproto.PlatformGSMsg.UserInfoResponse;
import com.rwproto.PlatformGSMsg.ePlatformGSMsgType;



public class PlatformGSHandler{
	private static PlatformGSHandler instance = new PlatformGSHandler();
	public static PlatformGSHandler getInstance(){
		return instance;
	}
	
	public ByteString getUserInfo(UserInfoRequest userInfoRequest){
		String userId = userInfoRequest.getUserId();
		UserInfoResponse.Builder userInfoResponse = UserInfoResponse.newBuilder();
		Player player = PlayerMgr.getInstance().find(userId);
		userInfoResponse.setPlatformGSMsgType(ePlatformGSMsgType.USER_INFO);
		if(player != null){
			String account = player.getUserDataMgr().getAccount();
			int level = player.getLevel();
			int vipLevel = player.getVip();
			String headImage = player.getHeadImage();
			int career = player.getCareer();
			String userName = player.getUserName();
			long lastLoginTime = player.getUserGameDataMgr().getLastLoginTime();
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
	
	public ByteString processKickPlayerOnline(UserInfoRequest userInfoRequest){
		String userId = userInfoRequest.getUserId();
		if (UserChannelMgr.get(userId) != null) {
			Player player = PlayerMgr.getInstance().find(userId);
			GameLog.debug("Kick Player...,userId:" + userId);
			player.KickOff("你的账号在另一处登录，请重新登录");
		}
		
		UserInfoResponse.Builder userInfoResponse = UserInfoResponse.newBuilder();
		userInfoResponse.setPlatformGSMsgType(ePlatformGSMsgType.USER_STATUS);
		return userInfoResponse.build().toByteString();
	}
}
