package com.bm.targetSell.param;

import com.bm.targetSell.TargetSellManager;
import com.rw.fsutil.util.MD5;


/**
 * 5003 精准服通知游戏服推送玩家所有属性
 * 5005 精准服通知游戏服清空玩家所有推送物品
 * @author Alex
 * 2016年9月17日 下午5:54:31
 */
public class TargetSellAbsArgs extends TargetSellHeartBeatParam {

	private String userId;//这个是玩家的账号
	private String channelId;
	private String roleId;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	
	@Override
	public String initMD5Str() {
		return MD5.getMD5String("appId="+getAppId()+"&userId="+userId+"&channelId="
				+channelId+"&roleId="+roleId+"||"+ TargetSellManager.appKey);
	}
	@Override
	public void handlerMsg(int msgType) {
		TargetSellManager.getInstance().pushRoleAttrOrCleanItems(this, msgType);
	}
	

	
}
